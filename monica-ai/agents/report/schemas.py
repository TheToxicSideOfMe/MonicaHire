from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel
from typing import Optional


class AnswerEntry(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    question: str
    answer: str


class CandidateDetail(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    id: str
    job_id: str
    company_id: str
    name: str
    phone: Optional[str] = None
    location: Optional[str] = None
    cv_url: str
    answers: list[AnswerEntry]
    status: str
    global_score: Optional[float] = None
    cv_score: Optional[float] = None
    interview_score: Optional[float] = None
    ai_percentage: Optional[float] = None
    experience_match: Optional[float] = None
    skills_match: Optional[float] = None
    education_match: Optional[float] = None
    culture_fit: Optional[float] = None
    communication_score: Optional[float] = None
    mindset_score: Optional[float] = None
    potential_score: Optional[float] = None
    note: Optional[str] = None


class ReportRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

    company_id: str
    job_id: str
    candidate_id: str
    candidate: CandidateDetail


class ReportResponse(BaseModel):
    report_markdown: str