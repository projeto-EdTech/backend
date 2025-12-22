CREATE TABLE materia (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_materia VARCHAR(255) NOT NULL
);

CREATE TABLE conteudo (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_fundamento VARCHAR(255) NOT NULL,
    materia_id UUID,
    CONSTRAINT fk_conteudo_materia FOREIGN KEY (materia_id) REFERENCES materia(id)
);

CREATE TABLE questao_conteudo (
    questao_id UUID NOT NULL,
    conteudo_id UUID NOT NULL,
    PRIMARY KEY (questao_id, conteudo_id),
    CONSTRAINT fk_questao_conteudo_questao FOREIGN KEY (questao_id) REFERENCES questao(id),
    CONSTRAINT fk_questao_conteudo_conteudo FOREIGN KEY (conteudo_id) REFERENCES conteudo(id)
);
