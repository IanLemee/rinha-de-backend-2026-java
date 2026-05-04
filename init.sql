CREATE EXTENSION IF NOT EXISTS vector;

CREATE SEQUENCE IF NOT EXISTS referenceSeqGen
START WITH 1
INCREMENT 1;

CREATE TABLE IF NOT EXISTS reference_data(
  id BIGINT PRIMARY KEY DEFAULT nextval('referenceSeqGen'),
    vectors vector(14),
    is_fraud boolean
);

COPY reference_data(vectors, is_fraud)
    FROM PROGRAM 'zcat /docker-entrypoint-initdb.d/references.csv.gz'
    DELIMITER ','
    CSV;