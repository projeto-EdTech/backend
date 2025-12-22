CREATE TABLE artigo (
    id UUID PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    conteudo TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    criado_por TEXT NOT NULL,
    materia_id UUID NOT NULL,
    CONSTRAINT fk_artigo_materia FOREIGN KEY (materia_id) REFERENCES materia(id) ON DELETE CASCADE
);

CREATE TABLE artigo_stats (
    id UUID PRIMARY KEY,
    artigo_id UUID NOT NULL,
    visualizacoes INT NOT NULL DEFAULT 0,
    curtidas INT NOT NULL DEFAULT 0,
    compartilhamentos INT NOT NULL DEFAULT 0,
    tempo_medio_leitura TEXT NOT NULL,
    CONSTRAINT fk_artigo_stats_artigo FOREIGN KEY (artigo_id) REFERENCES artigo(id) ON DELETE CASCADE
);