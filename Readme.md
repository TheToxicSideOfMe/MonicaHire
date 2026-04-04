# MonicaHire

> AI-powered hiring platform built as a distributed system.

MonicaHire automates the technical screening pipeline — from generating tailored interview questions to evaluating candidates via AI agents with per-company RAG context — so hiring teams can focus on the final human decision.

---

## What it does

Companies post jobs and invite candidates via a one-time email link. Candidates complete an async interview and upload their CV. Three AI agents handle the rest: generating context-aware questions, scoring the candidate against the job's ideal profile using semantic retrieval, and producing a detailed PDF report for enterprise users.

---

## Architecture

MonicaHire is a distributed system composed of 10 Spring Boot microservices and one FastAPI AI layer, communicating over HTTP (synchronous) and Kafka (asynchronous).

```
┌─────────────────────────────────────────────────────────┐
│                      API Gateway                         │
│              JWT validation · routing                    │
└────────────────────────┬────────────────────────────────┘
                         │
        ┌────────────────┼─────────────────┐
        │                │                 │
   ┌────▼────┐    ┌──────▼──────┐   ┌─────▼──────┐
   │  auth   │    │    user     │   │    job     │
   │ service │    │   service   │   │  service   │
   └─────────┘    └─────────────┘   └─────┬──────┘
                                          │
              ┌───────────────────────────┼──────────────┐
              │                           │              │
       ┌──────▼──────┐            ┌───────▼──────┐  ┌───▼────────────┐
       │  candidate  │            │  interview   │  │  subscription  │
       │   service   │            │   service    │  │    service     │
       └──────┬──────┘            └──────────────┘  └────────────────┘
              │
     ┌────────┼──────────┐
     │        │          │
┌────▼───┐ ┌──▼────┐ ┌───▼──────┐
│  file  │ │report │ │notification│
│service │ │service│ │  service  │
└────────┘ └───────┘ └───────────┘
                          ▲
                    Kafka events

                  ┌───────────────┐
                  │   monica-ai   │  ← FastAPI
                  │               │
                  │  JobSetup     │
                  │  Evaluation   │  ← LangGraph agents
                  │  Report       │
                  │               │
                  │  RAG / pgvector│ ← per-company namespace
                  └───────────────┘
```

---

## Services

| Service | Responsibility | Port |
|---|---|---|
| `api-gateway` | Routing, JWT validation, header forwarding | 8080 |
| `auth-service` | Register, login, JWT issue/refresh | 8081 |
| `user-service` | Company profile, identity | 8082 |
| `job-service` | Job creation, listing, dashboard | 8083 |
| `candidate-service` | Candidate lifecycle, CV, scores | 8084 |
| `interview-service` | One-time tokens, link expiry | 8085 |
| `subscription-service` | Plans, quotas, billing status | 8086 |
| `notification-service` | Emails — Kafka consumer, stateless | 8087 |
| `file-service` | Cloudinary proxy for CVs and PDFs | 8088 |
| `report-service` | PDF generation, enterprise only | 8089 |
| `monica-ai` | FastAPI — all AI agent logic | 8000 |

---

## AI Layer — monica-ai

Three LangGraph agents, each with a defined scope. All share one pgvector store partitioned by company.

### JobSetupAgent
Triggered when a company creates a job. Retrieves the company's RAG context, analyzes job requirements, generates 5 tailored interview questions, and embeds the job context into pgvector for later retrieval.

### EvaluationAgent
Triggered when a candidate submits their interview. Retrieves the job's RAG context, semantically evaluates the CV against the ideal profile, scores the interview answers against company culture, runs AI detection on the answers, and returns a full breakdown of scores.

### ReportAgent
Triggered on demand for enterprise users. Retrieves the full candidate and job context from pgvector, builds a structured markdown report section by section, and returns it to `report-service` for PDF generation.

### RAG Namespace Design
```
company:{id}
└── job:{job_id}
    ├── job description
    ├── ideal profile
    ├── required skills
    └── tailored questions + reasoning
```
Each evaluation only retrieves from its own `company_id:job_id` namespace — no data bleeds between companies.

---

## Tech Stack

**Backend services**
- Java 21, Spring Boot 3
- Spring Cloud Gateway
- Spring Security (JWT at gateway level)
- Spring Data JPA + PostgreSQL
- Spring Kafka

**AI layer**
- Python 3.11, FastAPI
- LangGraph (agent orchestration)
- LangChain
- pgvector (RAG store)
- Ollama + nomic-embed-text (embeddings)
- DeepSeek API (LLM)

**Infrastructure**
- Apache Kafka + Zookeeper
- PostgreSQL 16 (one DB per service, single instance in dev)
- pgvector (dedicated instance)
- Cloudinary (file storage)
- Docker Compose

---

## Kafka Topics

```
user.registered
user.profile.updated
job.created
job.closed
candidate.created
candidate.evaluated
candidate.status.changed
report.requested
report.completed
subscription.updated
subscription.expired
```

---

## Getting Started

### Prerequisites
- Docker + Docker Compose
- Java 21
- Python 3.11+
- DeepSeek API key
- Cloudinary account

### 1. Clone the repo
```bash
git clone https://github.com/thetoxicsideofme/monicahire.git
cd monicahire
```

### 2. Configure environment
```bash
cp .env.example .env
# Fill in DEEPSEEK_API_KEY, CLOUDINARY_*, JWT_SECRET
```

### 3. Start infrastructure
```bash
docker compose up -d
```

### 4. Pull the embedding model
```bash
chmod +x pull-models.sh
./pull-models.sh
```

### 5. Start services
Each service is a standalone Spring Boot app or FastAPI app. Start them individually during development or via their respective run configurations.

```bash
# Example — auth-service
cd auth-service
./mvnw spring-boot:run

# monica-ai
cd monica-ai
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

---

## Project Structure

```
monicahire/
├── api-gateway/
├── auth-service/
├── user-service/
├── job-service/
├── candidate-service/
├── interview-service/
├── subscription-service/
├── notification-service/
├── file-service/
├── report-service/
├── monica-ai/
│   ├── agents/
│   │   ├── job_setup_agent/
│   │   ├── evaluation_agent/
│   │   └── report_agent/
│   ├── rag/
│   ├── routers/
│   ├── models/
│   ├── core/
│   └── main.py
├── init-db/
│   └── init.sql
├── docker-compose.yml
├── .env.example
└── README.md
```

---

## Service Ports (local dev)

| Resource | URL |
|---|---|
| API Gateway | http://localhost:8080 |
| Kafka UI | http://localhost:8090 |
| Postgres | localhost:5435 |
| pgvector | localhost:5436 |
| Ollama | http://localhost:11435 |
| monica-ai | http://localhost:8000 |

---

## Status

🚧 **In active development**

- [x] Infrastructure — Docker Compose, Kafka, Postgres, pgvector, Ollama
- [ ] api-gateway — JWT filter, routing
- [ ] auth-service
- [ ] user-service + RAG embedding
- [ ] job-service + JobSetupAgent
- [ ] interview-service
- [ ] candidate-service + EvaluationAgent
- [ ] subscription-service
- [ ] notification-service
- [ ] file-service
- [ ] report-service + ReportAgent

---

## License

MIT