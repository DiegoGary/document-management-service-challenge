--The script to initialize the schema was sourced from the Spring Batch Core dependency: org.springframework.batch.core.

CREATE SCHEMA IF NOT EXISTS document_schema;
SET SCHEMA 'document_schema';

CREATE TABLE IF NOT EXISTS user (
    id SERIAL PRIMARY KEY
    name VARCHAR(255) UNIQUE,
)

CREATE TABLE IF NOT EXISTS document (
    id SERIAL PRIMARY KEY
    user_id BIGINT REFERENCES user(id) ON DELETE CASCADE,
    minio_path VARCHAR(255),
    name VARCHAR(255),
    file_size BIGINT,
    file_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tags (
    id SERIAL PRIMARY KEY,
    document_id BIGINT REFERENCES document(id) ON DELETE CASCADE,
    tag VARCHAR(255)
);


