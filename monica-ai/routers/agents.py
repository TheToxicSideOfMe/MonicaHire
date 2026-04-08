from fastapi import APIRouter
from agents.job_setup.graph import run_job_setup_agent
from agents.job_setup.schemas import JobSetupRequest, JobSetupResponse

router = APIRouter(prefix="/agents", tags=["Agents"])


@router.post("/setup-job", response_model=JobSetupResponse)
async def setup_job(payload: JobSetupRequest):
    questions = await run_job_setup_agent(payload)
    return JobSetupResponse(questions=questions)