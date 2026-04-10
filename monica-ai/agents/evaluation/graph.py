import json
import httpx
import pypdf
import io
from core.config import settings
from core.database import AsyncSessionFactory
from rag.retriever import similarity_search
from agents.evaluation.schemas import EvaluationRequest, EvaluationResponse
from agents.evaluation.prompts import EVALUATION_PROMPT
from openai import AsyncOpenAI
import logging

logger = logging.getLogger(__name__)

deepseek = AsyncOpenAI(
    api_key=settings.deepseek_api_key,
    base_url=settings.deepseek_base_url,
)


async def fetch_cv_text(cv_url: str) -> str:
    """Download PDF from Cloudinary and extract plain text."""
    async with httpx.AsyncClient(timeout=30) as client:
        response = await client.get(cv_url)
        response.raise_for_status()

    pdf_bytes = io.BytesIO(response.content)
    reader = pypdf.PdfReader(pdf_bytes)

    pages = [page.extract_text() or "" for page in reader.pages]
    text = "\n".join(pages).strip()

    if not text:
        return "CV text could not be extracted."

    words = text.split()
    if len(words) > 3000:
        text = " ".join(words[:3000]) + "\n[CV truncated]"

    return text


async def run_evaluation_agent(payload: EvaluationRequest) -> EvaluationResponse:
    async with AsyncSessionFactory() as db:

        # 1. Retrieve job + company context from RAG
        job_namespace = f"company:{payload.company_id}:job:{payload.job_id}"
        rag_results = await similarity_search(
            db=db,
            namespace=job_namespace,
            query=" ".join(a.answer for a in payload.answers[:3]),
            top_k=5,
        )
        rag_context = "\n".join(r["content"] for r in rag_results) if rag_results else "No job context available."

        # 2. Extract CV text from PDF
        try:
            cv_text = await fetch_cv_text(payload.cv_url)
        except Exception as e:
            cv_text = f"CV could not be retrieved: {str(e)}"

        # 3. Format Q&A block
        qa_block = "\n\n".join(
            f"Q: {entry.question}\nA: {entry.answer}"
            for entry in payload.answers
        )

        # 4. Build prompt
        prompt = EVALUATION_PROMPT.format(
            rag_context=rag_context,
            cv_text=cv_text,
            qa_block=qa_block,
        )

        # 5. Call DeepSeek
        response = await deepseek.chat.completions.create(
            model=settings.deepseek_model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.2,
        )
        raw = response.choices[0].message.content.strip()

        logger.info(f"DeepSeek raw response: {repr(raw)}")

        # 6. Strip markdown fences if present
        if raw.startswith("```"):
            lines = raw.splitlines()
            raw = "\n".join(lines[1:-1]).strip()

        if not raw:
            raise ValueError("DeepSeek returned an empty response")

        # 7. Parse and validate
        data = json.loads(raw)

        required_keys = {
            "global_score", "cv_score", "interview_score", "ai_percentage",
            "experience_match", "skills_match", "education_match", "culture_fit",
            "communication_score", "mindset_score", "potential_score", "note"
        }
        missing = required_keys - data.keys()
        if missing:
            raise ValueError(f"EvaluationAgent response missing keys: {missing}")

        return EvaluationResponse(**data)