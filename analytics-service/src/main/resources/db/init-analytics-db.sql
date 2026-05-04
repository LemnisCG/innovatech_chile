-- =========================================================================
-- Esquema Estrella para Analítica (ROLAP) - analytics-service
-- =========================================================================

-- 1. Tablas de Dimensión

CREATE TABLE IF NOT EXISTS dim_tiempo (
    id_tiempo SERIAL PRIMARY KEY,
    fecha DATE NOT NULL UNIQUE,
    dia SMALLINT NOT NULL,
    mes SMALLINT NOT NULL,
    anio SMALLINT NOT NULL,
    trimestre SMALLINT NOT NULL,
    dia_semana SMALLINT NOT NULL,
    es_fin_semana BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS dim_proyecto (
    id_proyecto BIGINT PRIMARY KEY, -- Mismo ID que en project-service
    nombre VARCHAR(255) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE
);

CREATE TABLE IF NOT EXISTS dim_recurso (
    id_recurso BIGINT PRIMARY KEY, -- Mismo ID que en resource-service
    nombre VARCHAR(255) NOT NULL,
    rol VARCHAR(100) NOT NULL,
    departamento VARCHAR(100)
);

-- 2. Tablas de Hechos

-- Hechos de la gestión de proyectos (Lead time, completitud)
CREATE TABLE IF NOT EXISTS fact_gestion_proyectos (
    id_hecho_proyecto SERIAL PRIMARY KEY,
    id_proyecto BIGINT NOT NULL,
    id_recurso BIGINT, -- Recurso asignado (puede ser nulo si es a nivel de proyecto general)
    id_tiempo BIGINT NOT NULL,
    total_tareas INTEGER DEFAULT 0,
    tareas_completadas INTEGER DEFAULT 0,
    tareas_pendientes INTEGER DEFAULT 0,
    lead_time_promedio_dias NUMERIC(10, 2), -- Tiempo promedio en completar tareas
    tasa_completitud NUMERIC(5, 2), -- Porcentaje de completitud
    CONSTRAINT fk_fgp_proyecto FOREIGN KEY (id_proyecto) REFERENCES dim_proyecto(id_proyecto),
    CONSTRAINT fk_fgp_recurso FOREIGN KEY (id_recurso) REFERENCES dim_recurso(id_recurso),
    CONSTRAINT fk_fgp_tiempo FOREIGN KEY (id_tiempo) REFERENCES dim_tiempo(id_tiempo)
);

-- Hechos para monitoreo técnico de servicios
CREATE TABLE IF NOT EXISTS fact_monitoreo_servicios (
    id_hecho_monitoreo SERIAL PRIMARY KEY,
    id_tiempo BIGINT NOT NULL,
    servicio_origen VARCHAR(100) NOT NULL, -- ej. 'project-service'
    clase_interceptor VARCHAR(255), -- ej. 'ProyectoService' o 'ProyectosController'
    metodo VARCHAR(255) NOT NULL,
    latencia_ms BIGINT NOT NULL,
    codigo_http INTEGER, -- ej. 200, 500, etc.
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fms_tiempo FOREIGN KEY (id_tiempo) REFERENCES dim_tiempo(id_tiempo)
);
