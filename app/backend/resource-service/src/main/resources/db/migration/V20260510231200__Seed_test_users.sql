-- =============================================
-- Migración: Datos de prueba para usuarios
-- Las contraseñas serán configuradas por el DataSeeder de Spring
-- usando el PasswordEncoder de BCrypt al iniciar la aplicación.
-- =============================================

-- Insertar usuarios con password temporal (será reemplazado por DataSeeder)
-- Solo se ejecuta una vez gracias a Flyway

-- 1. ADMIN
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('admin', 'PENDING_HASH', 'admin@innovatech.cl', 'Administración General', '+56912345678', 'Av. Providencia 1234, Santiago', '12.345.678-9', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'admin'), 'ADMIN');

-- 2. JEFE DE PROYECTO
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('jefe_proyecto', 'PENDING_HASH', 'jefe@innovatech.cl', 'Gestión de Proyectos', '+56923456789', 'Las Condes 567, Santiago', '11.222.333-4', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'jefe_proyecto'), 'JEFE_PROYECTO');

-- 3. CLIENTE 1
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('cliente_alfa', 'PENDING_HASH', 'cliente.alfa@empresa.cl', 'Retail', '+56934567890', 'Vitacura 890, Santiago', '10.111.222-3', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'cliente_alfa'), 'CLIENTE');

-- 4. CLIENTE 2
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('cliente_beta', 'PENDING_HASH', 'cliente.beta@empresa.cl', 'Logística', '+56945678901', 'Ñuñoa 1234, Santiago', '9.876.543-2', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'cliente_beta'), 'CLIENTE');

-- 5. MIEMBRO 1 - Backend Developer
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('dev_carlos', 'PENDING_HASH', 'carlos@innovatech.cl', 'Desarrollo Backend', '+56956789012', 'Maipú 456, Santiago', '15.432.109-8', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'dev_carlos'), 'MIEMBRO');

-- 6. MIEMBRO 2 - Frontend Developer
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('dev_maria', 'PENDING_HASH', 'maria@innovatech.cl', 'Desarrollo Frontend', '+56967890123', 'La Florida 789, Santiago', '16.543.210-7', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'dev_maria'), 'MIEMBRO');

-- 7. MIEMBRO 3 - DevOps
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('dev_andres', 'PENDING_HASH', 'andres@innovatech.cl', 'DevOps', '+56978901234', 'Puente Alto 321, Santiago', '17.654.321-6', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'dev_andres'), 'MIEMBRO');

-- 8. MIEMBRO 4 - QA / Testing
INSERT INTO usuarios (username, password, email, especialidad, telefono, direccion, rut, estado, is_active, created_at, updated_at)
VALUES ('dev_lucia', 'PENDING_HASH', 'lucia@innovatech.cl', 'QA / Testing', '+56989012345', 'San Bernardo 654, Santiago', '18.765.432-5', 'ACTIVO', true, NOW(), NOW());

INSERT INTO usuario_roles (usuario_id, roles)
VALUES ((SELECT id FROM usuarios WHERE username = 'dev_lucia'), 'MIEMBRO');
