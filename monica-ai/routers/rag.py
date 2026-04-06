from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from pydantic import BaseModel
from typing import Optional
from core.database import get_db
from rag.store import upsert_chunk

router = APIRouter(prefix="/rag", tags=["RAG"])


class CompanyIdentity(BaseModel):
    industry: Optional[str] = None
    companySize: Optional[str] = None
    mission: Optional[str] = None
    culture: Optional[str] = None
    values: Optional[str] = None
    workEnvironment: Optional[str] = None
    tone: Optional[str] = None


class EmbedCompanyRequest(BaseModel):
    company_id: str
    identity: CompanyIdentity


@router.post("/embed-company")
async def embed_company(payload: EmbedCompanyRequest, db: AsyncSession = Depends(get_db)):
    namespace = f"company:{payload.company_id}"
    identity = payload.identity

    chunks = {}
    if identity.industry:        chunks["industry"] = f"Industry: {identity.industry}"
    if identity.companySize:     chunks["company_size"] = f"Company size: {identity.companySize}"
    if identity.mission:         chunks["mission"] = f"Company mission: {identity.mission}"
    if identity.culture:         chunks["culture"] = f"Company culture: {identity.culture}"
    if identity.values:          chunks["values"] = f"Company values: {identity.values}"
    if identity.workEnvironment: chunks["work_environment"] = f"Work environment: {identity.workEnvironment}"
    if identity.tone:            chunks["tone"] = f"Communication tone: {identity.tone}"

    if not chunks:
        raise HTTPException(status_code=400, detail="No identity fields provided.")

    for chunk_key, content in chunks.items():
        await upsert_chunk(db, namespace=namespace, chunk_key=chunk_key, content=content)

    return {"company_id": payload.company_id, "chunks_stored": len(chunks)}