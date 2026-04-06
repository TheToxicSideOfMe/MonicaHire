from contextlib import asynccontextmanager
from fastapi import FastAPI
from core.database import init_db
from routers.rag import router as rag_router


@asynccontextmanager
async def lifespan(app: FastAPI):
    await init_db()
    yield


app = FastAPI(title="Monica AI", lifespan=lifespan)

app.include_router(rag_router)


@app.get("/health")
async def health():
    return {"status": "ok"}