CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    login VARCHAR(100) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_login UNIQUE (login)
);

INSERT INTO usuarios (login, clave)
VALUES ('andres', '$2a$10$P0QxQADVm6J/pRmuK4n5neIIYim7zhpL2.RASQtizaSMPSkKanTTy');
