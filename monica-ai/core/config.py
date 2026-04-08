from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    pgvector_url: str
    ollama_base_url: str
    ollama_embed_model: str
    deepseek_api_key: str
    deepseek_base_url: str
    deepseek_model: str

    class Config:
        env_file = ".env"


settings = Settings()