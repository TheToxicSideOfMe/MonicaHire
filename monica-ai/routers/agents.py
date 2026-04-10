from fastapi import APIRouter
from agents.job_setup.graph import run_job_setup_agent
from agents.job_setup.schemas import JobSetupRequest, JobSetupResponse
from agents.evaluation.graph import run_evaluation_agent
from agents.evaluation.schemas import EvaluationRequest, EvaluationResponse

router = APIRouter(prefix="/agents", tags=["Agents"])


@router.post("/setup-job", response_model=JobSetupResponse)
async def setup_job(payload: JobSetupRequest):
    questions = await run_job_setup_agent(payload)
    return JobSetupResponse(questions=questions)


@router.post("/evaluate", response_model=EvaluationResponse)
async def evaluate(payload: EvaluationRequest):
    return await run_evaluation_agent(payload)