ALTER TABLE topicos
ADD COLUMN mensaje_hash CHAR(64) AS (SHA2(mensaje, 256)) STORED,
ADD CONSTRAINT uk_topicos_titulo_mensaje_hash UNIQUE (titulo, mensaje_hash);
