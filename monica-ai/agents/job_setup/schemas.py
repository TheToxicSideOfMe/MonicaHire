from pydantic import BaseModel
from typing import Optional


class JobSetupRequest(BaseModel):
    company_id: str
    job_id: str
    title: str
    description: str
    location: Optional[str] = None
    employmentType: Optional[str] = None
    workMode: Optional[str] = None
    experienceYears: Optional[int] = None


class JobSetupResponse(BaseModel):
    questions: list[str]