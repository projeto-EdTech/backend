CREATE TABLE gabarito (
    id_gabarito UUID PRIMARY KEY,
    id_usuario UUID NOT NULL,
    id_questao UUID NOT NULL,
    acertou BOOLEAN NOT NULL,
    resposta_usuario CHAR(1) NOT NULL,
    CONSTRAINT fk_gabarito_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_gabarito_questao FOREIGN KEY (id_questao) REFERENCES questao(id) ON DELETE CASCADE
);