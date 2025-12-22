
CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nome_usuario VARCHAR(255) NOT NULL,
    email_usuario VARCHAR(255) NOT NULL UNIQUE

);

CREATE TABLE historico (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    prova_id UUID NOT NULL,
    usuario_id UUID NOT NULL,
    feedback_gemini TEXT NOT NULL,
    nota_final FLOAT NOT NULL,
    data_historico TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_historico FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_prova_historico FOREIGN KEY (prova_id) REFERENCES prova(id) ON DELETE CASCADE
);

