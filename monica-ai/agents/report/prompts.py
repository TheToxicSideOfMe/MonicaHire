REPORT_PROMPT = """
You are an expert hiring analyst. Generate a comprehensive, professional candidate evaluation report in Markdown format.

## Company & Job Context (from RAG)
{rag_context}

## Candidate Profile
- Name: {name}
- Location: {location}
- Status: {status}

## Evaluation Scores
- Global Score: {global_score}/100
- CV Score: {cv_score}/100
- Interview Score: {interview_score}/100
- AI Usage Detected: {ai_percentage}%
- Experience Match: {experience_match}/100
- Skills Match: {skills_match}/100
- Education Match: {education_match}/100
- Culture Fit: {culture_fit}/100
- Communication Score: {communication_score}/100
- Mindset Score: {mindset_score}/100
- Potential Score: {potential_score}/100

## Evaluator Note
{note}

## Interview Q&A
{qa_block}

## CV Content
{cv_text}

## Report Instructions
Write a detailed hiring report with the following sections using Markdown (## for section headers):

## Executive Summary
2-3 sentences summarizing the candidate's overall fit for the role.

## Strengths
List the candidate's key strengths based on their CV, answers, and scores.

## Areas of Concern
Honest assessment of weaknesses, gaps, or red flags (including AI usage if high).

## Score Breakdown
Interpret each score in plain language — not just numbers, explain what they mean for this candidate.

## Interview Performance
Qualitative analysis of the candidate's answers — depth, relevance, authenticity.

## CV Analysis
Assessment of the candidate's background, experience, and education relative to the role.

## Culture & Mindset Fit
How well the candidate aligns with the company's values and work environment.

## Hiring Recommendation
Clear recommendation: Strong Hire / Hire / Maybe / Do Not Hire — with a brief justification.

Write in a professional but direct tone. Be specific — reference actual answers and CV details, not generic statements.
Return ONLY the Markdown report, no preamble, no extra commentary.
"""