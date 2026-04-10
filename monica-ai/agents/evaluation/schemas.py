from pydantic import BaseModel
from typing import Optional


class AnswerEntry(BaseModel):
    question: str
    answer: str


class EvaluationRequest(BaseModel):
    company_id: str
    job_id: str
    cv_url: str
    answers: list[AnswerEntry]


class EvaluationResponse(BaseModel):
    global_score: float
    cv_score: float
    interview_score: float
    ai_percentage: float
    experience_match: float
    skills_match: float
    education_match: float
    culture_fit: float
    communication_score: float
    mindset_score: float
    potential_score: float
    note: str