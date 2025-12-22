CREATE TABLE questao_imagens (
    questao_id UUID NOT NULL,
    imagem TEXT,

    CONSTRAINT fk_questao_imagens_questao
    FOREIGN KEY (questao_id) REFERENCES questao(id) ON DELETE CASCADE
);