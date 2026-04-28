# 📘 Documentación Técnica — Resource Service

> **Módulo:** `resource-service`  
> **Puerto:** 8082  
> **Framework:** Spring Boot 3.2.4 · Java 21  
> **Base de datos:** PostgreSQL 15 — `innovatech_resources` (puerto 5434)  
> **Última actualización:** 28 de Abril de 2026

---

## 📑 Índice

1. [Descripción del Servicio](#1-descripción-del-servicio)
2. [Dependencias (pom.xml)](#2-dependencias-pomxml)
3. [Configuración (application.properties)](#3-configuración-applicationproperties)
4. [Entidades](#4-entidades)
5. [Repositorios](#5-repositorios)
6. [Servicios (Lógica de Negocio)](#6-servicios-lógica-de-negocio)
7. [Controladores (API REST)](#7-controladores-api-rest)
8. [Migraciones Flyway](#8-migraciones-flyway)
9. [Modelo de Datos (ERD)](#9-modelo-de-datos-erd)
10. [Guía de Ejecución](#10-guía-de-ejecución)

---

## 1. Descripción del Servicio

El `resource-service` es el microservicio encargado de gestionar los **recursos humanos profesionales** (ingenieros, arquitectos, supervisores, etc.) y sus **asignaciones a proyectos y tareas**. Maneja dos conceptos principales:

- **Usuario:** Representa al profesional con sus datos personales, credenciales, especialidad y roles.
- **Recurso:** Representa la asignación de un profesional a un proyecto/tarea específica, incluyendo rol, horas y estado de la asignación.

### Estructura de paquetes

```
resource-service/src/main/java/cl/innovatech/resourcemanagement/
├── InnovatechResourceManagementMicroserviceApplication.java   # Clase main
├── controller/
│   ├── UsuarioController.java       # Endpoints REST de usuarios
│   └── RecursoController.java       # Endpoints REST de recursos
├── entities/
│   ├── Usuario.java                 # Entidad JPA — profesional
│   └── Recurso.java                 # Entidad JPA — asignación
├── repository/
│   ├── UsuarioRepository.java       # Acceso a datos de usuarios
│   └── RecursoRepository.java       # Acceso a datos de recursos
└── services/
    ├── UsuarioService.java          # Lógica de negocio de usuarios
    └── RecursoService.java          # Lógica de negocio de recursos
```

---

## 2. Dependencias (pom.xml)

| Dependencia | Artifact | Scope | Propósito |
|---|---|---|---|
| Spring Web | `spring-boot-starter-web` | compile | Servidor HTTP, REST controllers, JSON |
| Spring Data JPA | `spring-boot-starter-data-jpa` | compile | ORM Hibernate, repositorios automáticos |
| PostgreSQL Driver | `postgresql` | runtime | Conexión a la base de datos PostgreSQL |
| H2 Database | `h2` | runtime | Base de datos en memoria para tests |
| Lombok | `lombok` | provided | Generación automática de getters/setters/toString |
| Flyway Core | `flyway-core` | compile | Sistema de migraciones versionadas de BD |

**Plugin de build:** `spring-boot-maven-plugin` para empaquetar y ejecutar la aplicación.

---

## 3. Configuración (application.properties)

```properties
spring.application.name=innovatech-resource-management-microservice
server.port=8082

# --- CONEXIÓN A POSTGRESQL ---
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5434}/innovatech_resources
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# --- JPA / HIBERNATE ---
spring.jpa.hibernate.ddl-auto=validate    # Solo valida, NO crea tablas
spring.jpa.show-sql=true                  # Muestra queries SQL en consola
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# --- FLYWAY (MIGRACIONES) ---
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true    # Acepta BD existentes sin historial
```

### Notas de configuración

- **`ddl-auto=validate`**: Hibernate **no crea ni modifica** tablas. Solo valida que las entidades Java coincidan con el esquema de la base de datos. Si no coinciden, la aplicación falla al arrancar.
- **`DB_HOST` / `DB_PORT`**: Variables de entorno que toman valores por defecto (`localhost:5434`) cuando se ejecuta en local, y los valores del `docker-compose.yml` cuando se ejecuta en contenedores.
- **`baseline-on-migrate=true`**: Permite que Flyway se integre con una base de datos que ya tenía tablas creadas previamente.

---

## 4. Entidades

### 4.1 Usuario

**Archivo:** `entities/Usuario.java`  
**Tabla:** `usuarios`  
**Anotaciones de clase:** `@Entity`, `@Table(name = "usuarios")`, `@Data` (Lombok)

| Campo | Tipo Java | Columna DB | Tipo DB | Restricciones | Descripción |
|---|---|---|---|---|---|
| `id` | `Long` | `id` | `BIGSERIAL` | PK, auto-generated | Identificador único |
| `username` | `String` | `username` | `VARCHAR(255)` | UNIQUE, NOT NULL | Nombre de usuario |
| `password` | `String` | `password` | `VARCHAR(255)` | NOT NULL | Contraseña |
| `email` | `String` | `email` | `VARCHAR(255)` | UNIQUE, NOT NULL | Correo electrónico |
| `especialidad` | `String` | `especialidad` | `VARCHAR(255)` | NOT NULL | Ej: "Ingeniería Civil" |
| `telefono` | `String` | `telefono` | `VARCHAR(255)` | NOT NULL | Teléfono de contacto |
| `direccion` | `String` | `direccion` | `VARCHAR(255)` | NOT NULL | Dirección física |
| `rut` | `String` | `rut` | `VARCHAR(255)` | NOT NULL | RUT chileno |
| `estado` | `String` | `estado` | `VARCHAR(255)` | NOT NULL | Ej: "ACTIVO", "INACTIVO" |
| `roles` | `Set<String>` | tabla `usuario_roles` | — | `@ElementCollection` EAGER | Roles del profesional |
| `active` | `boolean` | `is_active` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Indicador de cuenta activa |
| `createdAt` | `LocalDateTime` | `created_at` | `TIMESTAMP` | NOT NULL, `@CreationTimestamp` | Fecha de creación (automática) |
| `updatedAt` | `LocalDateTime` | `updated_at` | `TIMESTAMP` | NOT NULL, `@UpdateTimestamp` | Fecha de modificación (automática) |
| `lastLogin` | `LocalDateTime` | `last_login` | `TIMESTAMP` | nullable | Último inicio de sesión |

**Tabla auxiliar — `usuario_roles`** (relación `@ElementCollection`):

| Columna | Tipo | Descripción |
|---|---|---|
| `usuario_id` | `BIGINT` | FK → `usuarios(id)` |
| `roles` | `VARCHAR(255)` | Valor del rol: `PROFESIONAL`, `JEFE_PROYECTO`, `SUPERVISOR`, `ADMIN` |

### 4.2 Recurso

**Archivo:** `entities/Recurso.java`  
**Tabla:** `recursos`  
**Anotaciones de clase:** `@Entity`, `@Table(name = "recursos")`, `@Data` (Lombok)

| Campo | Tipo Java | Columna DB | Tipo DB | Restricciones | Descripción |
|---|---|---|---|---|---|
| `id` | `Long` | `id` | `BIGSERIAL` | PK, auto-generated | Identificador único |
| `usuario` | `Usuario` | `usuario_id` | `BIGINT` | FK → `usuarios(id)`, NOT NULL | Profesional asignado |
| `idProyecto` | `Long` | `id_proyecto` | `BIGINT` | NOT NULL | ID del proyecto (en project-service) |
| `idTarea` | `Long` | `id_tarea` | `BIGINT` | nullable | ID de la tarea (en project-service) |
| `rolEnProyecto` | `String` | `rol_en_proyecto` | `VARCHAR(255)` | NOT NULL | Ej: "Jefe de Obra", "Diseñadora" |
| `horasAsignadas` | `Integer` | `horas_asignadas` | `INTEGER` | nullable | Horas semanales asignadas |
| `fechaAsignacion` | `LocalDate` | `fecha_asignacion` | `DATE` | NOT NULL | Fecha en que se asignó |
| `fechaLiberacion` | `LocalDate` | `fecha_liberacion` | `DATE` | nullable | Fecha en que se liberó |
| `estado` | `String` | `estado` | `VARCHAR(255)` | NOT NULL | `ASIGNADO`, `LIBERADO`, `EN_ESPERA` |

**Relación JPA con Usuario:**
```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "usuario_id", nullable = false)
private Usuario usuario;
```
- **`@ManyToOne`**: Muchos recursos pueden pertenecer a un mismo usuario.
- **`FetchType.EAGER`**: Al consultar un recurso, siempre se trae la información completa del usuario.
- **`usuario_id`**: Es una FK real dentro de la misma base de datos.

> **Nota importante:** `idProyecto` e `idTarea` **NO son Foreign Keys reales** en la base de datos. Son IDs referenciales lógicos que apuntan a datos en el `project-service` (otra base de datos). Este es el patrón estándar en arquitecturas de microservicios con Database per Service.

---

## 5. Repositorios

### 5.1 UsuarioRepository

**Archivo:** `repository/UsuarioRepository.java`

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> { }
```

Hereda de `JpaRepository` con los métodos CRUD automáticos: `findAll()`, `findById()`, `save()`, `deleteById()`, `existsById()`.

### 5.2 RecursoRepository

**Archivo:** `repository/RecursoRepository.java`

```java
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> { }
```

Mismos métodos CRUD automáticos. Anotado con `@Repository` de forma explícita.

> **Nota:** El `RecursoService` invoca métodos como `findByUsuarioId()`, `findByIdProyecto()` y `findByIdTarea()` que actualmente no están declarados en el repositorio. Estos necesitan ser agregados como query methods de Spring Data para funcionar correctamente.

---

## 6. Servicios (Lógica de Negocio)

### 6.1 UsuarioService

**Archivo:** `services/UsuarioService.java`  
**Inyección:** `@Autowired` (inyección de campo)

| Método | Retorno | Descripción |
|---|---|---|
| `getAllUsuarios()` | `List<Usuario>` | Retorna la lista completa de usuarios |
| `getUsuarioById(Long id)` | `Optional<Usuario>` | Busca un usuario por ID. Retorna `Optional.empty()` si no existe |
| `createUsuario(Usuario)` | `Usuario` | Persiste y retorna el nuevo usuario. `createdAt` y `updatedAt` se generan automáticamente |
| `updateUsuario(Long id, Usuario)` | `Optional<Usuario>` | Busca el usuario existente y actualiza: username, email, password, especialidad, telefono, direccion, rut, estado, roles, active. Retorna `Optional.empty()` si el ID no existe |
| `deleteUsuario(Long id)` | `boolean` | Retorna `true` si eliminó exitosamente, `false` si el ID no existía |

### 6.2 RecursoService

**Archivo:** `services/RecursoService.java`  
**Inyección:** Por constructor (`RecursoRepository` + `UsuarioRepository`)

| Método | Retorno | Descripción |
|---|---|---|
| `getAllRecursos()` | `List<Recurso>` | Lista todas las asignaciones |
| `getRecursoById(Long id)` | `Optional<Recurso>` | Busca una asignación por ID |
| `getRecursosByUsuarioId(Long)` | `List<Recurso>` | Busca todas las asignaciones de un profesional |
| `getRecursosByProyectoId(Long)` | `List<Recurso>` | Busca todos los profesionales asignados a un proyecto |
| `getRecursosByTareaId(Long)` | `List<Recurso>` | Busca todos los profesionales asignados a una tarea |
| `createRecurso(Recurso)` | `Recurso` | **Valida** que el `usuario.id` exista en BD antes de persistir. Lanza `RuntimeException` si no existe |
| `updateRecurso(Long id, Recurso)` | `Optional<Recurso>` | Actualiza: idProyecto, idTarea, rolEnProyecto, horasAsignadas, fechaAsignacion, fechaLiberacion, estado |
| `deleteRecurso(Long id)` | `boolean` | `true` si eliminó, `false` si no existía |

#### Validación en `createRecurso`

```java
public Recurso createRecurso(Recurso recurso) {
    Usuario usuario = usuarioRepository.findById(recurso.getUsuario().getId())
            .orElseThrow(() -> new RuntimeException(
                "Usuario no encontrado con id: " + recurso.getUsuario().getId()));
    recurso.setUsuario(usuario);
    return recursoRepository.save(recurso);
}
```

Esta validación asegura que no se pueda crear una asignación con un ID de usuario inexistente, lanzando una excepción antes de intentar el `INSERT` en la base de datos.

---

## 7. Controladores (API REST)

### 7.1 UsuarioController

**Archivo:** `controller/UsuarioController.java`  
**Base URL:** `/usuarios`

| Método HTTP | Endpoint | Request Body | Response | Código HTTP |
|---|---|---|---|---|
| `GET` | `/usuarios` | — | `List<Usuario>` | 200 |
| `GET` | `/usuarios/{id}` | — | `Usuario` | 200 / 404 |
| `POST` | `/usuarios` | `Usuario JSON` | `Usuario` creado | 200 |
| `PUT` | `/usuarios/{id}` | `Usuario JSON` | `Usuario` actualizado | 200 / 404 |
| `DELETE` | `/usuarios/{id}` | — | — | 200 / 404 |

#### Ejemplo — Crear usuario (POST /usuarios)

```json
{
  "username": "jperez",
  "password": "SecurePass123!",
  "email": "juan.perez@innovatech.cl",
  "especialidad": "Ingeniería Civil",
  "telefono": "+56912345678",
  "direccion": "Av. Providencia 1234, Santiago",
  "rut": "12.345.678-9",
  "estado": "ACTIVO",
  "roles": ["PROFESIONAL", "JEFE_PROYECTO"],
  "active": true
}
```

### 7.2 RecursoController

**Archivo:** `controller/RecursoController.java`  
**Base URL:** `/recursos`

| Método HTTP | Endpoint | Request Body | Response | Código HTTP |
|---|---|---|---|---|
| `GET` | `/recursos` | — | `List<Recurso>` | 200 |
| `GET` | `/recursos/{id}` | — | `Recurso` | 200 / 404 |
| `GET` | `/recursos/usuario/{usuarioId}` | — | `List<Recurso>` | 200 |
| `GET` | `/recursos/proyecto/{idProyecto}` | — | `List<Recurso>` | 200 |
| `GET` | `/recursos/tarea/{idTarea}` | — | `List<Recurso>` | 200 |
| `POST` | `/recursos` | `Recurso JSON` | `Recurso` creado | 200 |
| `PUT` | `/recursos/{id}` | `Recurso JSON` | `Recurso` actualizado | 200 / 404 |
| `DELETE` | `/recursos/{id}` | — | — | 200 / 404 |

#### Ejemplo — Crear recurso (POST /recursos)

```json
{
  "usuario": { "id": 1 },
  "idProyecto": 1,
  "idTarea": 1,
  "rolEnProyecto": "Jefe de Obra",
  "horasAsignadas": 40,
  "fechaAsignacion": "2026-04-28",
  "fechaLiberacion": null,
  "estado": "ASIGNADO"
}
```

> El campo `usuario` solo necesita el `id`. El servicio busca y adjunta el objeto completo del usuario antes de guardar.

---

## 8. Migraciones Flyway

### Configuración

Flyway está habilitado y ejecuta automáticamente al arrancar la aplicación. Los scripts se ubican en `src/main/resources/db/migration/`.

### Convención de nombres

```
V{YYYYMMDDHHMMSS}__{Descripcion}.sql
   └── Timestamp ──┘  └── Nombre ──┘
```

### Scripts existentes

#### V20260427234408__Init_schema.sql

Crea las tablas base para la entidad `Usuario`:

```sql
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
```

#### V20260428012900__Create_recursos_table.sql

Crea la tabla para la entidad `Recurso`:

```sql
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
```

### Cómo agregar nuevas migraciones

1. Crear un nuevo archivo SQL en `src/main/resources/db/migration/`
2. Nombrar con el timestamp actual: `V20260429120000__Add_telefono2_column.sql`
3. Escribir el DDL:
   ```sql
   ALTER TABLE usuarios ADD COLUMN telefono2 VARCHAR(255);
   ```
4. Al reiniciar la aplicación, Flyway detecta y ejecuta el script automáticamente
5. El historial se guarda en la tabla `flyway_schema_history`

---

## 9. Modelo de Datos (ERD)

```
┌──────────────────────────┐
│        USUARIOS          │
├──────────────────────────┤
│ id         BIGSERIAL  PK │───┐
│ username   VARCHAR UQ NN │   │
│ password   VARCHAR    NN │   │
│ email      VARCHAR UQ NN │   │    ┌───────────────────────┐
│ especialidad VARCHAR  NN │   │    │    USUARIO_ROLES      │
│ telefono   VARCHAR    NN │   ├───>├───────────────────────┤
│ direccion  VARCHAR    NN │   │    │ usuario_id  BIGINT FK │
│ rut        VARCHAR    NN │   │    │ roles       VARCHAR   │
│ estado     VARCHAR    NN │   │    └───────────────────────┘
│ is_active  BOOLEAN    NN │   │
│ created_at TIMESTAMP  NN │   │    ┌───────────────────────────┐
│ updated_at TIMESTAMP  NN │   │    │        RECURSOS           │
│ last_login TIMESTAMP     │   │    ├───────────────────────────┤
└──────────────────────────┘   │    │ id             BIGSERIAL  │
                               └───>│ usuario_id     BIGINT  FK │
                                    │ id_proyecto     BIGINT NN │ ─ ─ ─ ┐
                                    │ id_tarea        BIGINT    │ ─ ─ ┐ │
                                    │ rol_en_proyecto VARCHAR NN│       │
                                    │ horas_asignadas INTEGER   │     Referencias
                                    │ fecha_asignacion DATE  NN │     lógicas a
                                    │ fecha_liberacion DATE     │     project-service
                                    │ estado         VARCHAR NN │       │
                                    └───────────────────────────┘ ─ ─ ─ ┘
```

---

## 10. Guía de Ejecución

### Prerrequisitos

- Java 21+
- Docker Desktop corriendo (para PostgreSQL)

### Paso 1 — Levantar la base de datos

```bash
docker compose up -d resource-db
```

### Paso 2 — Ejecutar el servicio

Desde la raíz de `innovatech_chile/`:

```bash
.\mvnw.cmd -pl resource-service spring-boot:run
```

### Paso 3 — Verificar

```bash
curl http://localhost:8082/usuarios
# Debería retornar: []
```

### Datos de prueba

Ver el archivo `resource-service/test-data.json` con JSON listos para probar con Postman o curl.