from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from models.embedding import Embedding
from rag.embedder import embed_text


async def similarity_search(
    db: AsyncSession,
    namespace: str,
    query: str,
    top_k: int = 5,
) -> list[dict]:
    query_vector = await embed_text(query)

    stmt = (
        select(
            Embedding.chunk_key,
            Embedding.content,
            Embedding.embedding.cosine_distance(query_vector).label("distance"),
        )
        .where(Embedding.namespace == namespace)
        .order_by("distance")
        .limit(top_k)
    )

    result = await db.execute(stmt)
    rows = result.fetchall()

    return [
        {"chunk_key": row.chunk_key, "content": row.content, "distance": row.distance}
        for row in rows
    ]