ALTER TABLE resposta ADD COLUMN revisado BOOLEAN;

UPDATE resposta SET revisado = TRUE WHERE acertou = TRUE;
UPDATE resposta SET revisado = FALSE WHERE acertou = FALSE;

ALTER TABLE resposta ALTER COLUMN revisado SET NOT NULL;

CREATE OR REPLACE FUNCTION tg_definir_revisado_ia()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.acertou = TRUE THEN
        NEW.revisado := TRUE;
    ELSE
        NEW.revisado := FALSE;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_resposta_revisado_ia
BEFORE INSERT ON resposta
FOR EACH ROW
EXECUTE FUNCTION tg_definir_revisado_ia();