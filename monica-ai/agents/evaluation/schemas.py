from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel
from typing import Optional


class AnswerEntry(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)
    
    question: str
    answer: str


class EvaluationRequest(BaseModel):
    model_config = ConfigDict(alias_generator=to_camel, populate_by_name=True)

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