-- Sonido Vivo y Android: cuentas compartidas y datos de la tienda.
-- Flyway ejecuta esta migracion en la base configurada por JDBC.

-- ---------------------------------------------------------------------
-- usuario: identidad compartida por Sonido Vivo y Android
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)    NOT NULL,
    correo         VARCHAR(150)    NOT NULL COMMENT 'Identificador de inicio de sesion. Se guarda en minusculas.',
    password_hash  VARCHAR(255)    NOT NULL COMMENT 'Hash de la contrasena.',
    activo         BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'FALSE = cuenta desactivada, no puede iniciar sesion.',
    creado_en      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY usuario_correo_uk (correo)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Cuentas compartidas';

-- Un rol por cuenta y aplicacion. Sin fila, la cuenta no tiene acceso a esa aplicacion.
CREATE TABLE usuario_aplicacion (
    usuario_id BIGINT UNSIGNED NOT NULL,
    aplicacion ENUM('SONIDO_VIVO', 'MOVIL') NOT NULL,
    rol        ENUM('CLIENTE', 'VENDEDOR', 'ADMIN', 'DOCENTE', 'ASISTENTE') NOT NULL,
    PRIMARY KEY (usuario_id, aplicacion),
    CONSTRAINT usuario_aplicacion_usuario_fk
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT usuario_aplicacion_rol_chk CHECK (
        (aplicacion = 'SONIDO_VIVO' AND rol IN ('CLIENTE', 'VENDEDOR', 'ADMIN'))
        OR (aplicacion = 'MOVIL' AND rol IN ('DOCENTE', 'ASISTENTE'))
    )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Accesos por aplicacion asignados por el backend';

CREATE TABLE categoria (
    id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre       VARCHAR(80)     NOT NULL,
    slug         VARCHAR(80)     NOT NULL COMMENT 'Texto usado en la URL /categoria/:slug del frontend.',
    descripcion  VARCHAR(255)    NULL,
    imagen       VARCHAR(255)    NULL,
    PRIMARY KEY (id),
    UNIQUE KEY categoria_nombre_uk (nombre),
    UNIQUE KEY categoria_slug_uk (slug)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Categorias del catalogo';

CREATE TABLE producto (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    codigo        VARCHAR(20)     NOT NULL COMMENT 'Codigo del catalogo de la tienda, por ejemplo GA001.',
    categoria_id  BIGINT UNSIGNED NOT NULL,
    nombre        VARCHAR(150)    NOT NULL,
    marca         VARCHAR(80)     NOT NULL,
    modelo        VARCHAR(80)     NULL,
    precio        INT UNSIGNED    NOT NULL COMMENT 'Precio en pesos chilenos (CLP), sin decimales. Mayor que 0.',
    stock         INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT 'Unidades disponibles. Nunca negativo.',
    descripcion   TEXT            NULL,
    imagen        VARCHAR(255)    NULL,
    activo        BOOLEAN         NOT NULL DEFAULT TRUE COMMENT 'FALSE = retirado del catalogo. Se usa en vez de borrar si ya tiene pedidos.',
    PRIMARY KEY (id),
    UNIQUE KEY producto_codigo_uk (codigo),
    CONSTRAINT producto_precio_chk CHECK (precio > 0),
    KEY producto_categoria_idx (categoria_id),
    CONSTRAINT producto_categoria_fk
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Productos del catalogo';

CREATE TABLE pedido (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id          BIGINT UNSIGNED NOT NULL COMMENT 'Dueno del pedido. El backend lo toma del JWT (sub), nunca del cuerpo de la solicitud.',
    fecha               DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nombre_contacto     VARCHAR(100)    NOT NULL,
    apellidos_contacto  VARCHAR(100)    NOT NULL,
    correo_contacto     VARCHAR(150)    NOT NULL COMMENT 'Dato de contacto del checkout. No define al dueno del pedido.',
    entrega             ENUM('DESPACHO', 'RETIRO') NOT NULL,
    direccion           VARCHAR(200)    NULL COMMENT 'Obligatoria solo si la entrega es DESPACHO.',
    comuna              VARCHAR(80)     NULL COMMENT 'Obligatoria solo si la entrega es DESPACHO.',
    estado              ENUM('PREPARACION', 'DESPACHADO', 'ENTREGADO') NOT NULL DEFAULT 'PREPARACION',
    total               INT UNSIGNED    NOT NULL COMMENT 'Lo calcula el servidor con los precios de la base de datos.',
    PRIMARY KEY (id),
    CONSTRAINT pedido_despacho_chk CHECK (
        entrega <> 'DESPACHO'
        OR (direccion IS NOT NULL AND CHAR_LENGTH(TRIM(direccion)) > 0
            AND comuna IS NOT NULL AND CHAR_LENGTH(TRIM(comuna)) > 0)
    ),
    KEY pedido_usuario_idx (usuario_id),
    CONSTRAINT pedido_usuario_fk
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Pedidos de la tienda online';

CREATE TABLE detalle_pedido (
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    pedido_id        BIGINT UNSIGNED NOT NULL,
    producto_id      BIGINT UNSIGNED NOT NULL,
    cantidad         INT UNSIGNED    NOT NULL COMMENT 'Mayor que 0.',
    precio_unitario  INT UNSIGNED    NOT NULL COMMENT 'Copia del precio al momento de comprar. Lo asigna el servidor.',
    PRIMARY KEY (id),
    UNIQUE KEY detalle_pedido_producto_uk (pedido_id, producto_id),
    CONSTRAINT detalle_cantidad_chk CHECK (cantidad > 0),
    KEY detalle_producto_idx (producto_id),
    CONSTRAINT detalle_pedido_fk
        FOREIGN KEY (pedido_id) REFERENCES pedido (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT detalle_producto_fk
        FOREIGN KEY (producto_id) REFERENCES producto (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Lineas de cada pedido';

CREATE TABLE mensaje_contacto (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id  BIGINT UNSIGNED NULL COMMENT 'Opcional: solo si quien escribe tenia la sesion iniciada.',
    nombre      VARCHAR(100)    NOT NULL,
    correo      VARCHAR(150)    NOT NULL,
    asunto      VARCHAR(50)     NOT NULL,
    mensaje     TEXT            NOT NULL,
    fecha       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY mensaje_usuario_idx (usuario_id),
    CONSTRAINT mensaje_usuario_fk
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Mensajes del formulario de contacto';
