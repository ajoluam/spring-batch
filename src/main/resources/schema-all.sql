DROP TABLE people IF EXISTS;

CREATE TABLE pessoa (
    pessoa_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    primeiro_nome VARCHAR(20),
    ultimo_nome VARCHAR(20)
);