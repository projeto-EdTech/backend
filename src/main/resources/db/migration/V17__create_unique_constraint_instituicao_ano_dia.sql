ALTER TABLE prova
ADD CONSTRAINT unique_instituicao_ano_dia UNIQUE (instituicao_id, ano, dia)