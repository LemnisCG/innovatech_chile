CREATE TABLE recursos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    id_proyecto BIGINT NOT NULL,
    id_tarea BIGINT,
    rol_en_proyecto VARCHAR(255) NOT NULL,
    horas_asignadas INTEGER,
    fecha_asignacion DATE NOT NULL,
    fecha_liberacion DATE,
    estado VARCHAR(255) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
