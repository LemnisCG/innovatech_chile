CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    especialidad VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    rut VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_login TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    roles VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
