-- =====================================================================
--  CURSO JPA / HIBERNATE — BASE DE DATOS  tienda_jpa   (PostgreSQL)
-- =====================================================================
--  Ejecuta este archivo UNA VEZ, ANTES de arrancar la aplicacion.
--
--  Desde la terminal:
--      createdb tienda_jpa
--      psql -d tienda_jpa -f sql/01_crear_base_datos.sql
--
--  Desde IntelliJ (pestana Database, a la derecha):
--      abre este archivo y pulsa el triangulo verde con la conexion
--      a tienda_jpa seleccionada.
--
--  ¿POR QUE CREAMOS LAS TABLAS A MANO SI HIBERNATE PUEDE HACERLO?
--  Porque queremos ver las dos caras del mapeo. Con ddl-auto=update
--  Hibernate crearia las tablas solo, pero no aprenderias que columna
--  genera cada anotacion. Aqui defines el esquema, y en el Modulo 1
--  comprobaras que la entidad Java coincide exactamente con el.
--
--  En application.properties dejamos ddl-auto=validate: Hibernate NO
--  toca el esquema, solo verifica al arrancar que las entidades
--  coinciden con las tablas. Si algo no cuadra, la aplicacion no
--  arranca y el error dice exactamente que columna falta. Es la
--  configuracion que se usa en produccion.
-- =====================================================================

DROP TABLE IF EXISTS producto_etiqueta;
DROP TABLE IF EXISTS etiquetas;
DROP TABLE IF EXISTS resenas;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS usuarios;


-- ---------------------------------------------------------------------
--  MODULO 1 — las dos primeras tablas
-- ---------------------------------------------------------------------
--  GENERATED ALWAYS AS IDENTITY es el equivalente en PostgreSQL de
--  @GeneratedValue(strategy = GenerationType.IDENTITY) en la entidad.
CREATE TABLE categorias (
    id           BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre       VARCHAR(60)  NOT NULL,
    descripcion  VARCHAR(200),
    activa       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uq_categorias_nombre UNIQUE (nombre)
);

CREATE TABLE proveedores (
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR(80)  NOT NULL,
    pais          VARCHAR(40)  NOT NULL,
    email         VARCHAR(120),
    dias_entrega  INTEGER      NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_proveedores PRIMARY KEY (id),
    CONSTRAINT uq_proveedores_nombre UNIQUE (nombre),
    CONSTRAINT ck_proveedores_dias CHECK (dias_entrega > 0)
);

CREATE TABLE productos (
    id             BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre         VARCHAR(120)  NOT NULL,
    descripcion    VARCHAR(500),
    precio         NUMERIC(12,2) NOT NULL,
    stock          INTEGER       NOT NULL DEFAULT 0,
    activo         BOOLEAN       NOT NULL DEFAULT TRUE,
    fecha_alta     DATE          NOT NULL,
    categoria_id   BIGINT        NOT NULL,
    proveedor_id   BIGINT,
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id),
    CONSTRAINT fk_productos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores (id),
    CONSTRAINT ck_productos_precio CHECK (precio > 0),
    CONSTRAINT ck_productos_stock  CHECK (stock >= 0)
);


-- ---------------------------------------------------------------------
--  MODULO 2 — relaciones
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre          VARCHAR(60)  NOT NULL,
    apellido        VARCHAR(60)  NOT NULL,
    email           VARCHAR(120) NOT NULL,
    ciudad          VARCHAR(60)  NOT NULL,
    fecha_registro  DATE         NOT NULL,
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
);

--  estado se guarda como TEXTO porque la entidad usa
--  @Enumerated(EnumType.STRING). Si usara ORDINAL seria un INTEGER,
--  y bastaria reordenar el enum en Java para corromper todos los datos.
CREATE TABLE pedidos (
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    fecha         DATE          NOT NULL,
    estado        VARCHAR(20)   NOT NULL,
    direccion     VARCHAR(200)  NOT NULL,
    costo_envio   NUMERIC(10,2) NOT NULL DEFAULT 0,
    usuario_id    BIGINT        NOT NULL,
    CONSTRAINT pk_pedidos PRIMARY KEY (id),
    CONSTRAINT fk_pedidos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT ck_pedidos_estado CHECK (estado IN ('PENDIENTE','PAGADO','ENVIADO','ENTREGADO','CANCELADO'))
);

--  Tabla intermedia de un pedido y sus productos. Tiene atributos
--  propios (cantidad, precio_unitario), asi que en JPA NO es un
--  @ManyToMany: es una entidad con dos @ManyToOne. El Modulo 2 explica
--  por que esa distincion importa.
CREATE TABLE detalle_pedido (
    id               BIGINT GENERATED ALWAYS AS IDENTITY,
    pedido_id        BIGINT        NOT NULL,
    producto_id      BIGINT        NOT NULL,
    cantidad         INTEGER       NOT NULL,
    precio_unitario  NUMERIC(12,2) NOT NULL,
    CONSTRAINT pk_detalle_pedido PRIMARY KEY (id),
    CONSTRAINT fk_detalle_pedido   FOREIGN KEY (pedido_id)   REFERENCES pedidos (id),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT uq_detalle UNIQUE (pedido_id, producto_id),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0)
);

--  MODULO 2, EJERCICIO 1: la entidad Resena la escribes tu.
CREATE TABLE resenas (
    id            BIGINT GENERATED ALWAYS AS IDENTITY,
    producto_id   BIGINT       NOT NULL,
    usuario_id    BIGINT       NOT NULL,
    calificacion  INTEGER      NOT NULL,
    comentario    VARCHAR(500),
    fecha         DATE         NOT NULL,
    CONSTRAINT pk_resenas PRIMARY KEY (id),
    CONSTRAINT fk_resenas_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT fk_resenas_usuario  FOREIGN KEY (usuario_id)  REFERENCES usuarios (id),
    CONSTRAINT ck_resenas_calificacion CHECK (calificacion BETWEEN 1 AND 5)
);

--  MODULO 2, EJERCICIO 2: relacion N:N real (sin atributos propios),
--  asi que aqui SI corresponde un @ManyToMany con @JoinTable.
CREATE TABLE etiquetas (
    id      BIGINT GENERATED ALWAYS AS IDENTITY,
    nombre  VARCHAR(40) NOT NULL,
    color   VARCHAR(7)  NOT NULL DEFAULT '#CCCCCC',
    CONSTRAINT pk_etiquetas PRIMARY KEY (id),
    CONSTRAINT uq_etiquetas_nombre UNIQUE (nombre)
);

CREATE TABLE producto_etiqueta (
    producto_id  BIGINT NOT NULL,
    etiqueta_id  BIGINT NOT NULL,
    CONSTRAINT pk_producto_etiqueta PRIMARY KEY (producto_id, etiqueta_id),
    CONSTRAINT fk_pe_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
    CONSTRAINT fk_pe_etiqueta FOREIGN KEY (etiqueta_id) REFERENCES etiquetas (id)
);


-- ---------------------------------------------------------------------
--  INDICES
-- ---------------------------------------------------------------------
--  Las claves foraneas NO se indexan solas: hay que crearlas a mano.
--  Sin estos indices, cada JOIN de la aplicacion recorre la tabla entera.
CREATE INDEX idx_productos_categoria ON productos (categoria_id);
CREATE INDEX idx_productos_proveedor ON productos (proveedor_id);
CREATE INDEX idx_pedidos_usuario     ON pedidos (usuario_id);
CREATE INDEX idx_detalle_pedido_fk   ON detalle_pedido (pedido_id);
CREATE INDEX idx_detalle_producto_fk ON detalle_pedido (producto_id);
CREATE INDEX idx_resenas_producto    ON resenas (producto_id);
CREATE INDEX idx_resenas_usuario     ON resenas (usuario_id);

--  Indices para los filtros mas frecuentes de la aplicacion
CREATE INDEX idx_productos_activo_cat ON productos (activo, categoria_id);
CREATE INDEX idx_pedidos_estado_fecha ON pedidos (estado, fecha DESC);
