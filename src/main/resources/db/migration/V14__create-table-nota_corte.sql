CREATE TABLE nota_corte (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ano_vestibular INTEGER NOT NULL,
    modalidade_concorrencia VARCHAR(255),
    nome_curso VARCHAR(255) NOT NULL,
    nota_corte DOUBLE PRECISION NOT NULL,
    instituicao_id UUID NOT NULL,

    CONSTRAINT pk_instituicao_nota_corte FOREIGN KEY (instituicao_id) REFERENCES instituicao(id) ON DELETE CASCADE
);

