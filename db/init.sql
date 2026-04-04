-- ─────────────────────────────────────────
-- MonicaHire — Database Initialization
-- Runs once on first postgres container boot
-- ─────────────────────────────────────────

CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE job_db;
CREATE DATABASE candidate_db;
CREATE DATABASE interview_db;
CREATE DATABASE subscription_db;
CREATE DATABASE report_db;

-- Grant all privileges to the main user
GRANT ALL PRIVILEGES ON DATABASE auth_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE user_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE job_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE candidate_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE interview_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE subscription_db TO monicahire;
GRANT ALL PRIVILEGES ON DATABASE report_db TO monicahire;