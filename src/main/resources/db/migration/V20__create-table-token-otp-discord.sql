ALTER TABLE usuario
ADD COLUMN discord_id VARCHAR(100) UNIQUE;

CREATE TABLE token_otp_discord (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    token VARCHAR(20) NOT NULL UNIQUE,
    data_expiracao TIMESTAMP NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_token_usuario FOREIGN KEY (user_id) REFERENCES usuario(id) ON DELETE CASCADE
);