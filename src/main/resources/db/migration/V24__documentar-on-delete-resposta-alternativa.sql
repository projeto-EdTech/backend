-- fk_resposta_alternativa é intencionalmente RESTRICT (sem ON DELETE CASCADE), diferente
-- de fk_resposta_historico/fk_resposta_questao: não deve ser possível apagar uma Alternativa
-- isoladamente enquanto existir Resposta de usuário referenciando-a, para não apagar
-- silenciosamente o histórico de respostas de um estudante. Deleção de Alternativa nesse
-- cenário deve ser um fluxo explícito (ex.: apagar a Questao inteira, que já cascade-deleta
-- tanto Alternativa quanto Resposta).
COMMENT ON CONSTRAINT fk_resposta_alternativa ON resposta IS 'Intencionalmente sem ON DELETE CASCADE: preserva o histórico de respostas do usuário mesmo se uma alternativa for removida isoladamente.';
