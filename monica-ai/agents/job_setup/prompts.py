JOB_SETUP_PROMPT = """
You are an expert hiring assistant. Using the company context below, generate exactly 5 interview questions tailored to the job posting.

## Company Context (from RAG)
{rag_context}

## Job Details
- Title: {title}
- Description: {description}
- Location: {location}
- Employment Type: {employment_type}
- Work Mode: {work_mode}
- Experience Required: {experience_years} years

## Instructions
- Generate exactly 5 interview questions
- Questions must reflect the company culture and be specific to this job
- Mix technical, behavioral, and situational questions
- Return ONLY a JSON array of 5 strings, nothing else
- Example: ["Question 1", "Question 2", "Question 3", "Question 4", "Question 5"]
"""