CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE instituicao (
   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   nome VARCHAR(255) NOT NULL UNIQUE,
   tipo_instituicao VARCHAR(50) NOT NULL,
   sigla VARCHAR(25) NOT NULL,
   logo VARCHAR(255),
   estado_origem VARCHAR(19) NOT NULL
);

CREATE TABLE prova (
   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   nome VARCHAR(255) NOT NULL,
   ano INT NOT NULL,
   quantidade_questoes INT NOT NULL,
   instituicao_id UUID NOT NULL,

   CONSTRAINT fk_prova_intituicao FOREIGN KEY (instituicao_id) REFERENCES instituicao(id)
);

CREATE TABLE questao (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    enunciado TEXT NOT NULL,
    numero_questao TEXT NOT NULL,
    prova_id UUID NOT NULL,

    CONSTRAINT fk_questao_prova FOREIGN KEY (prova_id) REFERENCES prova(id)
);

CREATE TABLE alternativa (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    alternativa CHAR(1) NOT NULL,
    texto_alternativa TEXT NOT NULL,
    correta BOOLEAN NOT NULL,
    questao_id UUID NOT NULL,

    CONSTRAINT fk_alternativa_questao FOREIGN KEY (questao_id) REFERENCES questao(id)
);