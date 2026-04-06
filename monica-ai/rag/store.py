from sqlalchemy import delete
from sqlalchemy.ext.asyncio import AsyncSession
from models.embedding import Embedding
from rag.embedder import embed_text


async def upsert_chunk(db: AsyncSession, namespace: str, chunk_key: str, content: str) -> None:
    await db.execute(
        delete(Embedding).where(
            Embedding.namespace == namespace,
            Embedding.chunk_key == chunk_key,
        )
    )
    vector = await embed_text(content)
    chunk = Embedding(namespace=namespace, chunk_key=chunk_key, content=content, embedding=vector)
    db.add(chunk)
    await db.commit()