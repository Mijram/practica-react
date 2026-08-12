CREATE TABLE almacenes (
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    usuario_id BIGINT UNIQUE,
    nombre       VARCHAR(60)  NOT NULL,
    ciudad  VARCHAR(60),
    direccion VARCHAR(120),
    capacidad DECIMAL,
    fecha_apertura TIMESTAMP,
    activo       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_almacenes PRIMARY KEY (id),
    CONSTRAINT fk_almacenes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT ck_
);