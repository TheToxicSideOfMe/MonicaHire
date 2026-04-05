#!/bin/bash
set -e

echo "Creating MonicaHire databases..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE auth_db;
    GRANT ALL PRIVILEGES ON DATABASE auth_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE user_db;
    GRANT ALL PRIVILEGES ON DATABASE user_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE job_db;
    GRANT ALL PRIVILEGES ON DATABASE job_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE candidate_db;
    GRANT ALL PRIVILEGES ON DATABASE candidate_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE interview_db;
    GRANT ALL PRIVILEGES ON DATABASE interview_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE subscription_db;
    GRANT ALL PRIVILEGES ON DATABASE subscription_db TO $POSTGRES_USER;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
    CREATE DATABASE report_db;
    GRANT ALL PRIVILEGES ON DATABASE report_db TO $POSTGRES_USER;
EOSQL

echo "All databases created successfully."