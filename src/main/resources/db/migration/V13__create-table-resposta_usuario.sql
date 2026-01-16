CREATE TABLE resposta (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    alternativa_escolhida_id UUID NOT NULL,
    historico_id UUID NOT NULL,
    questao_id UUID NOT NULL,
    acertou BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_resposta_historico FOREIGN KEY (historico_id) REFERENCES historico(id) ON DELETE CASCADE,
    CONSTRAINT fk_resposta_questao FOREIGN KEY (questao_id) REFERENCES questao(id) ON DELETE CASCADE,

    -- Adicionada a FK para garantir que a alternativa existe
    CONSTRAINT fk_resposta_alternativa FOREIGN KEY (alternativa_escolhida_id) REFERENCES alternativa(id)
);