CREATE TABLE almacenes (
    id             VARCHAR(6)           NOT NULL,
    nombre         VARCHAR(120)         NOT NULL,
    ciudad         VARCHAR(120)         NOT NULL,
    direccion      VARCHAR(120)         NOT NULL,
    capacidad      DECIMAL              NOT NULL,
    fecha_apertura DATE                 NOT NULL,
    activo         BOOLEAN              NOT NULL DEFAULT TRUE,
    usuario_id     BIGINT               NOT NULL UNIQUE,
    CONSTRAINT     pk_almacenes         PRIMARY KEY (id),
    CONSTRAINT     fk_almacenes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT     ck_detalle_capacidad CHECK(capacidad > 0 ),
    CONSTRAINT     ck_codigo_length CHECK(LENGTH(id) = 6)
);