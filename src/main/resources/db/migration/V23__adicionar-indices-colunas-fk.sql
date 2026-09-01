-- Postgres não cria índice automaticamente em colunas de FK (só em PK/UNIQUE).
-- Estas colunas são usadas em JOIN com frequência (RespostaRepository, PlannerRepository, etc.).
CREATE INDEX IF NOT EXISTS idx_resposta_historico_id ON resposta (historico_id);
CREATE INDEX IF NOT EXISTS idx_resposta_questao_id ON resposta (questao_id);
CREATE INDEX IF NOT EXISTS idx_resposta_alternativa_escolhida_id ON resposta (alternativa_escolhida_id);

CREATE INDEX IF NOT EXISTS idx_historico_usuario_id ON historico (usuario_id);
CREATE INDEX IF NOT EXISTS idx_historico_prova_id ON historico (prova_id);

CREATE INDEX IF NOT EXISTS idx_questao_prova_id ON questao (prova_id);
CREATE INDEX IF NOT EXISTS idx_alternativa_questao_id ON alternativa (questao_id);
CREATE INDEX IF NOT EXISTS idx_conteudo_materia_id ON conteudo (materia_id);
