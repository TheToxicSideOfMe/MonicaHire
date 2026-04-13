# MonicaHire

> AI-powered hiring platform built as a distributed microservices system.

MonicaHire automates the entire hiring pipeline — from job posting to candidate evaluation — using AI agents, RAG, and an async event-driven architecture.

---

## Architecture

```
                        ┌─────────────────┐
                        │   API Gateway   │  :8080
                        │ (Spring Cloud)  │
                        └────────┬────────┘
                                 │ JWT Auth Filter
           ┌──────────┬──────────┼──────────┬──────────┬──────────┐
           │          │          │          │          │          │
     ┌─────▼───┐ ┌────▼────┐ ┌──▼──────┐ ┌─▼───────┐ ┌▼────────┐ ┌▼──────────────┐
     │  auth   │ │  user   │ │   job   │ │candidate│ │interview│ │ subscription  │
     │:8081    │ │:8082    │ │:8083    │ │:8084    │ │:8085    │ │:8086          │
     └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └───────────────┘
           │          │          │          │          │          │
     ┌─────▼──────────▼──────────▼──────────▼──────────▼──────────▼─────┐
     │                         Apache Kafka                               │
     │  user.registered · job.created · candidate.created                │
     │  candidate.submitted · candidate.evaluated · candidate.status.changed │
     │  report.requested · report.completed · subscription.expired       │
     └──────────┬──────────────────────────┬──────────────────────────┬──┘
                │                          │                          │
         ┌──────▼──────┐           ┌───────▼──────┐          ┌───────▼──────┐
         │notification │           │    report    │          │   monica-ai  │
         │   :8087     │           │    :8089     │          │   :8000      │
         └─────────────┘           └──────┬───────┘          └───────┬──────┘
                                          │                          │
                                   ┌──────▼───────┐         ┌───────▼──────┐
                                   │     file     │         │   pgvector   │
                                   │    :8088     │         │  + ollama    │
                                   └──────────────┘         └──────────────┘
```

---

## Stack

| Layer | Technology |
|---|---|
| Backend services | Spring Boot 3 (Java 21) |
| AI layer | FastAPI (Python 3.12) + DeepSeek API |
| Message broker | Apache Kafka |
| Databases | PostgreSQL (one per service) |
| Vector store | pgvector |
| Embeddings | Ollama (nomic-embed-text) |
| File storage | Cloudinary |
| API Gateway | Spring Cloud Gateway |
| Containerization | Docker Compose |
| Email | Brevo (Sendinblue) |

---

## Services

| Service | Port | Description |
|---|---|---|
| api-gateway | 8080 | Routing + JWT auth filter |
| auth-service | 8081 | Register, login, JWT issue/refresh |
| user-service | 8082 | Company profile, RAG embedding trigger |
| job-service | 8083 | Job CRUD, slot management, JobSetupAgent trigger |
| candidate-service | 8084 | Apply, submit interview, evaluation pipeline |
| interview-service | 8085 | One-time token generation and validation |
| subscription-service | 8086 | Plans, quotas, usage tracking |
| notification-service | 8087 | Email notifications via Brevo |
| file-service | 8088 | Cloudinary proxy for CV and report uploads |
| report-service | 8089 | PDF report generation |
| monica-ai | 8000 | FastAPI — all AI agents + RAG logic |

---

## AI Agents

### JobSetupAgent
Triggered when a company creates a job. Retrieves company identity from RAG, analyzes job requirements, generates 5 tailored interview questions, and embeds job context into pgvector.

### EvaluationAgent
Triggered asynchronously when a candidate submits their interview. Fetches job context from RAG, analyzes the CV, scores interview answers across 11 dimensions, detects AI usage, and produces a global score with a hiring note.

**Scoring dimensions:** global score · CV score · interview score · AI percentage · experience match · skills match · education match · culture fit · communication · mindset · potential

### ReportAgent
Triggered on demand for enterprise users. Produces a full markdown hiring report including executive summary, strengths, areas of concern, score breakdown, interview analysis, CV analysis, culture fit, and a hiring recommendation.

---

## Key Flows

### Candidate applies
```
POST /candidates/apply
  → claim slot [job-service]        ← atomic, no orphan candidates
  → save candidate as PENDING_INTERVIEW
  → generate token [interview-service]
  → publish candidate.created       → notification-service sends interview email
```

### Candidate submits interview
```
POST /candidates/submit
  → validate + consume token [interview-service]
  → pair questions + answers
  → save as SUBMITTED
  → publish candidate.submitted     ← returns 202 immediately
  → [async] EvaluationAgent runs
  → save scores, status = EVALUATED
  → publish candidate.evaluated
```

### Report generation
```
POST /reports/generate/{candidateId}
  → check enterprise quota [subscription-service]
  → save report as PENDING
  → publish report.requested        ← returns 202 immediately
  → [async] ReportAgent runs
  → convert markdown to PDF
  → upload to Cloudinary [file-service]
  → save pdfUrl, status = COMPLETED
  → publish report.completed        → notification-service emails company
```

---

## Getting Started

### Prerequisites
- Docker + Docker Compose
- DeepSeek API key
- Brevo API key
- Cloudinary account

### Setup

1. Clone the repo
```bash
git clone https://github.com/thetoxicsideofme/monicahire.git
cd monicahire
```

2. Create `.env` file in the root
```env
JWT_SECRET=your_jwt_secret
DEEPSEEK_API_KEY=your_deepseek_key
BREVO_API_KEY=your_brevo_key
BREVO_SENDER_EMAIL=noreply@yourdomain.com
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_key
CLOUDINARY_API_SECRET=your_cloudinary_secret
FRONTEND_URL=http://localhost:3000
```

3. Pull the Ollama embedding model
```bash
./pull-models.sh
```

4. Build and start all services
```bash
docker compose build
docker compose up -d
```

5. Verify everything is running
```bash
docker compose ps
```

### Useful URLs
| URL | Description |
|---|---|
| http://localhost:8080 | API Gateway |
| http://localhost:8000/docs | Monica AI (Swagger) |
| http://localhost:8090 | Kafka UI |

---

## RAG Namespace Design

```
company:{company_id}
└── company_identity        ← embedded on profile completion
    (culture, values, mission, tone)

company:{company_id}:job:{job_id}
└── job_description         ← embedded after JobSetupAgent runs
└── questions               ← tailored interview questions + reasoning
```

---

## Candidate Status Flow

```
PENDING_INTERVIEW → SUBMITTED → EVALUATED → SHORTLISTED / REJECTED / HIRED
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
│   │   ├── job_setup/
│   │   ├── evaluation/
│   │   └── report/
│   ├── rag/
│   ├── routers/
│   └── core/
├── init-db/
│   └── init.sh
└── docker-compose.yml
```

---

## Environment Variables Reference

| Variable | Used by | Description |
|---|---|---|
| `JWT_SECRET` | api-gateway, auth-service | JWT signing key |
| `DEEPSEEK_API_KEY` | monica-ai | DeepSeek LLM API key |
| `BREVO_API_KEY` | notification-service | Brevo email API key |
| `BREVO_SENDER_EMAIL` | notification-service | Sender email address |
| `CLOUDINARY_CLOUD_NAME` | file-service | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | file-service | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | file-service | Cloudinary API secret |
| `FRONTEND_URL` | notification-service | Frontend base URL for email links |

---

Built with 🤝 by Rayen Stark