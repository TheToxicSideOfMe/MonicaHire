import json
from core.config import settings
from core.database import AsyncSessionFactory
from rag.retriever import similarity_search
from rag.store import upsert_chunk
from agents.job_setup.schemas import JobSetupRequest
from agents.job_setup.prompts import JOB_SETUP_PROMPT
from openai import AsyncOpenAI

deepseek = AsyncOpenAI(
    api_key=settings.deepseek_api_key,
    base_url=settings.deepseek_base_url,
)


async def run_job_setup_agent(payload: JobSetupRequest) -> list[str]:
    async with AsyncSessionFactory() as db:
        # 1. Retrieve company identity from RAG
        rag_results = await similarity_search(
            db=db,
            namespace=f"company:{payload.company_id}",
            query=f"{payload.title} {payload.description}",
            top_k=5,
        )
        rag_context = "\n".join(r["content"] for r in rag_results) if rag_results else "No company context available."

        # 2. Build prompt
        prompt = JOB_SETUP_PROMPT.format(
            rag_context=rag_context,
            title=payload.title,
            description=payload.description,
            location=payload.location or "N/A",
            employment_type=payload.employmentType or "N/A",
            work_mode=payload.workMode or "N/A",
            experience_years=payload.experienceYears or 0,
        )

        # 3. Call DeepSeek
        response = await deepseek.chat.completions.create(
            model=settings.deepseek_model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.7,
        )
        raw = response.choices[0].message.content.strip()

        # 4. Parse questions
        questions = json.loads(raw)
        if not isinstance(questions, list) or len(questions) != 5:
            raise ValueError(f"Expected 5 questions, got: {raw}")

        # 5. Embed job context into pgvector
        job_namespace = f"company:{payload.company_id}:job:{payload.job_id}"
        await upsert_chunk(db, namespace=job_namespace, chunk_key="job_description", content=f"Job title: {payload.title}\n{payload.description}")
        await upsert_chunk(db, namespace=job_namespace, chunk_key="questions", content="Interview questions:\n" + "\n".join(f"- {q}" for q in questions))

        return questions