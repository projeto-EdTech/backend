CREATE TABLE artigo_imagem (
    id UUID PRIMARY KEY,
    artigo_id UUID,
    caminho TEXT NOT NULL,
    nome_original VARCHAR(255),
    content_type VARCHAR(100),
    tamanho BIGINT,
    ordem INT,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT now(),
    CONSTRAINT fk_artigo_imagem_artigo FOREIGN KEY (artigo_id) REFERENCES artigo(id) ON DELETE CASCADE
);