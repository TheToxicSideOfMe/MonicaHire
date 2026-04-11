from fastapi import APIRouter
from agents.job_setup.graph import run_job_setup_agent
from agents.job_setup.schemas import JobSetupRequest, JobSetupResponse
from agents.evaluation.graph import run_evaluation_agent
from agents.evaluation.schemas import EvaluationRequest, EvaluationResponse
from agents.report.schemas import ReportRequest,ReportResponse
from agents.report.graph import run_report_agent

router = APIRouter(prefix="/agents", tags=["Agents"])


@router.post("/setup-job", response_model=JobSetupResponse)
async def setup_job(payload: JobSetupRequest):
    questions = await run_job_setup_agent(payload)
    return JobSetupResponse(questions=questions)


@router.post("/evaluate", response_model=EvaluationResponse)
async def evaluate(payload: EvaluationRequest):
    return await run_evaluation_agent(payload)

@router.post("/report", response_model=ReportResponse)
async def report(payload: ReportRequest):
    return await run_report_agent(payload)