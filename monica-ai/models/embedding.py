import uuid
from sqlalchemy import Column, String, Text, DateTime, func
from sqlalchemy.dialects.postgresql import UUID
from pgvector.sqlalchemy import Vector
from core.database import Base

VECTOR_DIM = 768  # nomic-embed-text


class Embedding(Base):
    __tablename__ = "embeddings"

    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    namespace = Column(String(255), nullable=False, index=True)
    chunk_key = Column(String(255), nullable=False)
    content = Column(Text, nullable=False)
    embedding = Column(Vector(VECTOR_DIM), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())