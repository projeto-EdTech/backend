CREATE TABLE log_erro (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fingerprint VARCHAR(300) NOT NULL,
    exception_class VARCHAR(255) NOT NULL,
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    status_http INT,
    severidade VARCHAR(20) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    mensagem TEXT,
    stack_trace TEXT,
    usuario_id UUID,
    request_id VARCHAR(100),
    ambiente VARCHAR(50),
    versao_aplicacao VARCHAR(50),
    quantidade_ocorrencias INT NOT NULL DEFAULT 0,
    primeira_ocorrencia TIMESTAMP NOT NULL,
    ultima_ocorrencia TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    observacoes TEXT,
    versao_corrigida VARCHAR(50)
);

-- Busca do "achar registro em aberto pra incrementar" (LogErroRepository.findFirstByFingerprintAndStatusIn)
CREATE INDEX idx_log_erro_fingerprint_status ON log_erro (fingerprint, status);

-- Filtros de listagem (status/severidade/ambiente) e ordenação por última ocorrência
CREATE INDEX idx_log_erro_status ON log_erro (status);
CREATE INDEX idx_log_erro_ultima_ocorrencia ON log_erro (ultima_ocorrencia DESC);
