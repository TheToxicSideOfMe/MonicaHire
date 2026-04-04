#!/bin/bash

# ─────────────────────────────────────────
# pull-models.sh
# Run this ONCE after docker compose up
# to pull the embedding model into Ollama
# ─────────────────────────────────────────

echo "Waiting for Ollama to be ready..."
until curl -s http://localhost:11435/api/tags > /dev/null 2>&1; do
  sleep 2
done

echo "Ollama is up. Pulling nomic-embed-text..."
curl -X POST http://localhost:11435/api/pull \
  -H "Content-Type: application/json" \
  -d '{"name": "nomic-embed-text"}'

echo ""
echo "Done. nomic-embed-text is ready."