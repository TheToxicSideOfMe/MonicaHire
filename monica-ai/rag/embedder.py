import httpx
from core.config import settings


async def embed_text(text: str) -> list[float]:
    async with httpx.AsyncClient(base_url=settings.ollama_base_url, timeout=30.0) as client:
        response = await client.post(
            "/api/embeddings",
            json={"model": settings.ollama_embed_model, "prompt": text},
        )
        response.raise_for_status()
        return response.json()["embedding"]