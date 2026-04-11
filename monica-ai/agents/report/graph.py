import httpx
import pypdf
import io
from core.config import settings
from core.database import AsyncSessionFactory
from rag.retriever import similarity_search
from agents.report.schemas import ReportRequest, ReportResponse
from agents.report.prompts import REPORT_PROMPT
from openai import AsyncOpenAI

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


async def run_report_agent(payload: ReportRequest) -> ReportResponse:
    async with AsyncSessionFactory() as db:

        # 1. Retrieve job + company context from RAG
        job_namespace = f"company:{payload.company_id}:job:{payload.job_id}"
        rag_results = await similarity_search(
            db=db,
            namespace=job_namespace,
            query=f"{payload.candidate.name} {payload.candidate.status}",
            top_k=5,
        )
        rag_context = "\n".join(r["content"] for r in rag_results) if rag_results else "No job context available."

        # 2. Fetch CV text
        try:
            cv_text = await fetch_cv_text(payload.candidate.cv_url)
        except Exception as e:
            cv_text = f"CV could not be retrieved: {str(e)}"

        # 3. Format Q&A block
        qa_block = "\n\n".join(
            f"Q: {entry.question}\nA: {entry.answer}"
            for entry in payload.candidate.answers
        ) if payload.candidate.answers else "No interview answers available."

        # 4. Build prompt
        prompt = REPORT_PROMPT.format(
            rag_context=rag_context,
            name=payload.candidate.name,
            location=payload.candidate.location or "N/A",
            status=payload.candidate.status,
            global_score=payload.candidate.global_score or "N/A",
            cv_score=payload.candidate.cv_score or "N/A",
            interview_score=payload.candidate.interview_score or "N/A",
            ai_percentage=payload.candidate.ai_percentage or "N/A",
            experience_match=payload.candidate.experience_match or "N/A",
            skills_match=payload.candidate.skills_match or "N/A",
            education_match=payload.candidate.education_match or "N/A",
            culture_fit=payload.candidate.culture_fit or "N/A",
            communication_score=payload.candidate.communication_score or "N/A",
            mindset_score=payload.candidate.mindset_score or "N/A",
            potential_score=payload.candidate.potential_score or "N/A",
            note=payload.candidate.note or "N/A",
            qa_block=qa_block,
            cv_text=cv_text,
        )

        # 5. Call DeepSeek
        response = await deepseek.chat.completions.create(
            model=settings.deepseek_model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.4,
        )

        report_markdown = response.choices[0].message.content.strip()

        if not report_markdown:
            raise ValueError("ReportAgent returned an empty response")

        return ReportResponse(report_markdown=report_markdown)