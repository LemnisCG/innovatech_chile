# Innovatech Solutions — Documento de Presentación Técnica

> Guía de estudio para defensa oral del proyecto. Cubre arquitectura, patrones y decisiones de diseño de extremo a extremo.

---

## PARTE 1 — VISIÓN GENERAL DEL SISTEMA

### ¿Qué construimos?

Una **plataforma de gestión interna** para Innovatech Solutions (empresa de consultoría con +120 empleados) que centraliza:

- Gestión de proyectos y tareas
- Gestión de recursos humanos (profesionales, asignaciones, capacidad)
- Dashboard analítico con KPIs en tiempo real

El sistema está construido como un conjunto de **microservicios independientes** que se comunican a través de un **API Gateway** centralizado, con un **frontend Next.js** que consume esa API.

---

## PARTE 2 — PATRÓN ARQUITECTÓNICO GENERAL: MICROSERVICIOS

### ¿Qué es la arquitectura de microservicios?

Es un estilo arquitectónico donde el sistema se divide en **servicios pequeños, independientes y desplegables por separado**. Cada servicio tiene su propio proceso, su propia base de datos, y se comunica con otros a través de APIs HTTP (REST en nuestro caso).

### Servicios del sistema

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| **api-gateway** | 9000 | Punto de entrada único. Autenticación y enrutamiento |
| **project-service** | 8081 | CRUD de proyectos y tareas |
| **resource-service** | 8082 | CRUD de profesionales, asignaciones y autenticación JWT |
| **analytics-service** | 8083 | KPIs, dashboard, modelo ROLAP, ETL |
| **frontend** | 3000 | Dashboard web (Next.js 16) |

### ¿Por qué microservicios y no una aplicación monolítica?

**Razones concretas del proyecto:**

1. **Escalabilidad independiente:** Si el módulo de analítica tiene carga alta (muchos directivos consultando KPIs), se puede escalar solo ese contenedor sin tocar los demás.
2. **Aislamiento de fallos:** Si el `analytics-service` cae, los gestores de proyecto siguen operando con `project-service` y `resource-service`.
3. **Equipos independientes:** Cada microservicio puede ser desarrollado, probado y desplegado por un equipo diferente sin coordinación constante.
4. **Separación de dominio:** Proyectos/Tareas son un dominio diferente a Recursos Humanos. Mezclarlos en el mismo servicio genera acoplamiento conceptual innecesario.

**Alternativa directa — Monolito:**
Un monolito habría sido más simple de arrancar, con JOINs SQL directos entre tablas de proyectos y recursos. El problema es que escala de forma vertical (más RAM/CPU al servidor), tiene un único punto de fallo, y con 120+ usuarios concurrentes (RNF2) se convertiría en cuello de botella.

---

## PARTE 3 — PATRÓN: API GATEWAY

### ¿Qué hace el API Gateway?

Es el **único punto de entrada público** del sistema. Tecnología: **Spring Cloud Gateway** (reactivo, basado en Netty, no usa Tomcat).

Funciones que cumple:
1. **Enrutamiento:** Recibe `GET /proyectos` → redirige al `project-service:8081`
2. **Autenticación centralizada:** Valida el JWT antes de dejar pasar cualquier petición
3. **Inyección de identidad:** Agrega los headers `X-Auth-User` y `X-Auth-Roles` al request para que los servicios downstream sepan quién está llamando, sin repetir validación JWT en cada uno

### Tabla de rutas del Gateway

```yaml
/proyectos/**, /tareas/**     → project-service:8081
/recursos/**, /usuarios/**    → resource-service:8082
/api/auth/**                  → resource-service:8082  (PÚBLICO, sin validar JWT)
/api/analytics/**             → analytics-service:8083
```

### Flujo de autenticación completo

```
[Browser]
   │
   ├─1─► POST /api/auth/login  ──► [Gateway sin validar] ──► resource-service
   │                                                            │ genera JWT
   │◄──────────────────────────────────────────────────────────┘
   │   (recibe JWT)
   │
   ├─2─► GET /proyectos  (con Bearer JWT)
   │       │
   │       ▼
   │    [Gateway] ──► valida JWT, extrae username y roles
   │       │
   │       ▼
   │    agrega headers: X-Auth-User: juan / X-Auth-Roles: ADMIN
   │       │
   │       ▼
   │    [project-service] ──► lee X-Auth-User, crea SecurityContext
   │       │ retorna proyectos
   │◄──────┘
```

### ¿Por qué API Gateway y no llamadas directas del frontend a cada servicio?

Con llamadas directas (**Direct Client-to-Microservice**) el frontend tendría que:
- Conocer los puertos y URLs de cada microservicio (acoplamiento)
- Validar tokens en cada servicio por separado (repetición de lógica)
- Manejar múltiples llamadas concurrentes desde el browser

El Gateway centraliza todo eso. La alternativa más avanzada es un **Service Mesh (Istio)**, que maneja la comunicación entre servicios a nivel de red con "sidecars" junto a cada pod, pero eso es adecuado para sistemas con decenas de microservicios y equipos grandes — excesivo para esta escala.

---

## PARTE 4 — PATRÓN: DATABASE PER SERVICE

### ¿Qué es?

Cada microservicio tiene su **propia base de datos PostgreSQL**, aislada. Nadie más puede conectarse directamente a la base de datos de otro servicio.

| Servicio | Base de datos | Puerto externo |
|---|---|---|
| project-service | `innovatech_projects` | 5435 |
| resource-service | `innovatech_resources` | 5434 |
| analytics-service | `innovatech_analytics` | 5436 |

### La consecuencia más importante: referencias sin FK real

En `resource-service`, la tabla `recursos` guarda `id_proyecto` e `id_tarea` como columnas BIGINT simples, **no como Foreign Keys hacia otra base de datos** (eso es imposible entre DBs separadas). Son "referencias lógicas". Si el proyecto es eliminado del `project-service`, no hay CASCADE automático en `resource-service`.

```sql
-- En la DB de resource-service:
id_proyecto  BIGINT NOT NULL,  -- Referencia LÓGICA a project-service
id_tarea     BIGINT,           -- No hay FK real. Es responsabilidad del código.
```

### ¿Por qué no una base de datos compartida?

La **Shared Database** es el antipatrón opuesto. Todos los servicios apuntan a la misma base. Al principio parece cómodo (JOINs directos entre `proyectos` y `recursos`), pero:

1. Un schema change en la tabla `proyectos` puede romper `resource-service` en producción sin aviso
2. Si la BD se satura, caen **todos** los servicios juntos
3. Se pierde la independencia de despliegue — no puedes actualizar un servicio sin coordinar con todos los que comparten las mismas tablas

---

## PARTE 5 — PATRÓN: BFF (BACKEND FOR FRONTEND)

### ¿Qué es el BFF en este proyecto?

El **Backend For Frontend** es un servidor intermedio diseñado específicamente para las necesidades de un cliente UI. En este proyecto, el `analytics-service` actúa de BFF: agrega datos de `project-service` y los transforma en KPIs listos para mostrar en el dashboard sin que el browser tenga que hacer múltiples llamadas o cálculos.

Adicionalmente, la capa de **Server Components y Server Actions de Next.js** actúa como BFF a nivel de frontend: el servidor de Next.js hace las llamadas al Gateway y entrega al browser solo el HTML renderizado.

### Endpoints analíticos que consume el frontend

```
GET /api/analytics/kpis/productivity    → tasaCompletitud, leadTime, proyectosActivos
GET /api/analytics/kpis/system-health   → latenciaPromedioMs, tasaErroresPorcentaje
```

### ¿Por qué no GraphQL como alternativa?

GraphQL permitiría que el frontend construyera sus propias queries a demanda, eliminando la necesidad de endpoints analíticos fijos. Es una alternativa válida, pero requiere un servidor GraphQL adicional, aprender el lenguaje de queries, y es más complejo de securizar correctamente. Para el scope de este proyecto, los BFF endpoints fijos son más directos y predecibles.

---

## PARTE 6 — PATRONES DE DISEÑO (CÓDIGO)

### A. Repository Pattern

**Dónde:** Todos los servicios backend.

```java
// Spring Data JPA genera la implementación automáticamente
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> { }
```

El patrón Repository abstrae la capa de acceso a datos. El `ProyectoService` llama a `proyectoRepository.findAll()` sin saber si detrás hay PostgreSQL, H2 o un mock. Esto es lo que permite que los tests de integración funcionen con H2 en memoria sin cambiar el código de negocio.

**Alternativa directa:** Usar `EntityManager` o SQL hardcodeado en el servicio. Más control, pero rompe la separación de responsabilidades y hace los tests mucho más difíciles.

### B. DTO (Data Transfer Object)

**Dónde:** `project-service` — `ProyectoDTO`, `CreateProyectoDTO`, `TareaDTO`, `CreateTareaDTO`.

```java
// La entidad Proyecto tiene campos internos que no deben exponerse (ej. relaciones JPA lazy)
// El DTO controla exactamente qué entra y qué sale por la API
public class CreateProyectoDTO {
    private String nombre;
    private String descripcion;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
}
```

Sin DTOs, exponer directamente las entidades JPA genera problemas:
- Serialización circular (Proyecto → Tarea → Proyecto en JSON)
- Expone campos internos de Hibernate
- Un cambio en la entidad de persistencia rompe el contrato de la API

### C. AOP — Aspect-Oriented Programming (Programación Orientada a Aspectos)

**Dónde:** `project-service` — `MonitoringAspect.java`

```java
@Around("execution(* cl.innovatech.projectmanagement.services.ProyectoService.*(..))")
public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    // guarda métricas en analytics-db
    return result;
}
```

AOP permite agregar comportamiento transversal (logging, métricas, seguridad) **sin tocar el código de negocio**. `ProyectoService` no sabe que está siendo monitoreado. La alternativa es poner el código de medición dentro de cada método del servicio (duplicación masiva, difícil de mantener).

### D. Interceptor HTTP

**Dónde:** `project-service` — `HttpStatusInterceptor.java`

Complementa el Aspect: el Aspect captura tiempo de ejecución de métodos Java, el Interceptor captura el **código HTTP de respuesta** real (200, 404, 500) que el Aspect no puede ver. Juntos construyen las métricas completas de salud del servicio.

### E. Flyway — Migration Pattern

**Dónde:** `resource-service`

Gestión de versiones del schema de base de datos con archivos SQL numerados por timestamp (`V20260427234408__Init_schema.sql`). Flyway registra qué migraciones ya ejecutó en la tabla `flyway_schema_history` y solo aplica las nuevas. Sin esto, los cambios de schema son manuales y propensos a errores entre entornos (dev/staging/producción).

---

## PARTE 6B — ARQUETIPOS DE COMPONENTES

Un **arquetipo** es un rol estructural que un componente cumple de forma recurrente en la arquitectura. No es un patrón de diseño específico, sino la **categoría funcional** que explica por qué ese componente existe. Identificarlos permite razonar sobre el sistema sin leer todo el código.

### Arquetipos a nivel de servicio (macroarquitectura)

| Arquetipo | Servicio en el proyecto | Responsabilidad |
|---|---|---|
| **Edge / Gateway** | `api-gateway` | Punto de entrada único. Valida identidad, enruta, protege la red interna |
| **Domain Service** | `project-service` | Encapsula el dominio core: proyectos y tareas. Reglas de negocio propias |
| **Domain Service** | `resource-service` | Encapsula el dominio de recursos humanos. También emite identidad (JWT) |
| **Reporting / Analytics Service** | `analytics-service` | No tiene lógica transaccional. Solo lee, agrega y expone métricas |
| **BFF (Backend For Frontend)** | `analytics-service` + Next.js server | Adapta y consolida datos para las necesidades específicas del dashboard |

> Un `Domain Service` modifica estado. Un `Reporting Service` solo lee y calcula — nunca modifica datos de otros dominios.

---

### Arquetipos a nivel de componente (microarquitectura interna)

Cada microservicio sigue la misma estructura en capas. Los arquetipos de componente son los roles que cada clase cumple dentro de esa estructura.

#### 1. Entity (Entidad de dominio)

Representa un concepto del negocio con **identidad propia** que persiste en base de datos.

```
project-service   →  Proyecto.java, Tarea.java
resource-service  →  Usuario.java, Recurso.java
analytics-service →  DimProyecto.java, DimTiempo.java, FactGestionProyectos.java
```

Características: tiene `@Entity`, tiene un `id` como clave primaria, su ciclo de vida lo gestiona Hibernate. No sale directamente por la API (para eso está el DTO).

#### 2. Repository (Repositorio)

Abstrae el **acceso a la base de datos**. El resto del código no sabe si detrás hay PostgreSQL, H2 u otro motor.

```
ProyectoRepository      extends JpaRepository<Proyecto, Long>
TareaRepository         extends JpaRepository<Tarea, Long>
UsuarioRepository       extends JpaRepository<Usuario, Long>
RecursoRepository       extends JpaRepository<Recurso, Long>
DimProyectoRepository   extends JpaRepository<DimProyecto, Long>
FactGestionProyectosRepository ...
```

El `JpaRepository` de Spring Data genera automáticamente `findAll()`, `findById()`, `save()`, `deleteById()` sin código adicional.

#### 3. Service (Servicio de aplicación)

Contiene la **lógica de negocio**. Orquesta repositorios, valida reglas, decide qué persiste y qué retorna.

```
ProyectoService   →  crear proyecto, agregar tarea, listar proyectos
TareaService      →  cambiar estado, asignar profesional
RecursoService    →  crear asignación validando que el Usuario exista
UsuarioService    →  CRUD de profesionales
AnalyticsService  →  calcular KPIs consultando las fact tables
ETLService        →  orquestar el proceso Extract-Transform-Load
```

Regla importante: el Service nunca retorna entidades JPA directamente hacia el Controller. Convierte a DTOs.

#### 4. Controller (Controlador REST)

Define los **endpoints HTTP** y delega al Service. No tiene lógica de negocio.

```
ProyectosController  →  GET /proyectos, POST /proyectos, GET /proyectos/{id}
TareaController      →  POST /proyectos/{id}/tareas, PUT /tareas/{id}/estado
RecursoController    →  CRUD en /recursos/**
UsuarioController    →  CRUD en /usuarios/**
AuthController       →  POST /api/auth/login, POST /api/auth/register
AnalyticsController  →  GET /api/analytics/kpis/productivity, /system-health
```

#### 5. DTO (Data Transfer Object)

Define el **contrato público de la API**: qué campos entran y cuáles salen. Desacopla la entidad interna de la representación externa.

```
CreateProyectoDTO   →  campos que acepta POST /proyectos (sin id, sin campos de auditoría)
ProyectoDTO         →  campos que retorna GET /proyectos (incluye tareasDelProyecto)
CreateTareaDTO      →  campos que acepta POST /proyectos/{id}/tareas
TareaDTO            →  campos que retorna en respuestas
ProductivityKpiDTO  →  lo que retorna /api/analytics/kpis/productivity
SystemHealthKpiDTO  →  lo que retorna /api/analytics/kpis/system-health
```

Hay dos subtipos: **Command DTO** (lo que entra, ej. `CreateProyectoDTO`) y **View DTO** (lo que sale, ej. `ProyectoDTO`).

#### 6. Security Filter / Authentication Filter

Componente de **infraestructura de seguridad** que intercepta cada request antes de que llegue al Controller.

```
HeaderAuthenticationFilter  (project-service, resource-service, analytics-service)
  → lee X-Auth-User y X-Auth-Roles inyectados por el Gateway
  → construye el SecurityContext de Spring Security

AuthenticationFilter        (api-gateway)
  → valida el JWT Bearer del request entrante
  → extrae claims y agrega X-Auth-User / X-Auth-Roles al request downstream
```

No tiene lógica de negocio. Solo responde una pregunta: "¿quién es el que llama y puedo confiar en él?"

#### 7. Aspect / Interceptor (Componente transversal)

Comportamiento que **cruza múltiples capas** sin pertenecer a ninguna en particular.

```
MonitoringAspect       →  mide tiempo de ejecución de todos los métodos de ProyectoService (AOP)
HttpStatusInterceptor  →  captura el código HTTP de respuesta de cada endpoint del Controller
```

Estos no son llamados por nadie explícitamente — el framework los activa automáticamente en el momento correcto.

#### 8. Seeder / DataInitializer

Carga **datos iniciales** al arrancar la aplicación, solo en entornos de desarrollo.

```
DataSeeder (project-service)   →  crea proyectos y tareas de ejemplo al iniciar
DataSeeder (resource-service)  →  crea usuarios de prueba (junto a Flyway migration V...Seed)
```

---

### Mapa visual de arquetipos por servicio

```
┌─────────────────────────────────────────────────────────────────┐
│                        project-service                          │
│                                                                 │
│  [Controller]──► [Service]──► [Repository]──► PostgreSQL        │
│       │               │                                        │
│  [DTO entrada]   [DTO salida]   [Entity]                        │
│                       │                                        │
│              [MonitoringAspect]  ← arquetipo transversal        │
│              [HttpStatusInterceptor]                            │
│              [HeaderAuthenticationFilter]                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       resource-service                          │
│                                                                 │
│  [AuthController]──► [UsuarioService]──► [UsuarioRepository]   │
│  [RecursoController]──► [RecursoService]──► [RecursoRepository] │
│       │                      │                    │             │
│  [AuthRequest DTO]      [Entity]          PostgreSQL + Flyway   │
│  [AuthResponse DTO]                                             │
│              [JwtUtil] ← genera tokens                         │
│              [HeaderAuthenticationFilter]                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      analytics-service                          │
│                                                                 │
│  [AnalyticsController]──► [AnalyticsService]──► [Repositories] │
│                                │                    │           │
│                     [ProductivityKpiDTO]      Star Schema       │
│                     [SystemHealthKpiDTO]      PostgreSQL        │
│                                                                 │
│  [ETLService] ──HTTP──► project-service        @Scheduled cron  │
│              [HeaderAuthenticationFilter]                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                         api-gateway                             │
│                                                                 │
│  Internet ──► [AuthenticationFilter] ──► Route tables          │
│                      │                       │                  │
│                 [JwtUtil]            project / resource /        │
│                                      analytics service          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    frontend (Next.js)                           │
│                                                                 │
│  Browser ──► [Page/Server Component] ──► API Gateway           │
│                      │                                         │
│              [Server Actions]  ──► API Gateway (forms)          │
│              [api.ts services] ──► fetch helpers tipados        │
│              [MetricCard, Navigation] ── Client Components      │
└─────────────────────────────────────────────────────────────────┘
```

---

### Pregunta de examen frecuente sobre arquetipos

**P: ¿Qué diferencia hay entre un Service y un Controller?**

El Controller es el arquetipo de **frontera** (boundary): existe para traducir el mundo HTTP al mundo Java y viceversa. Recibe un `@RequestBody`, llama al Service, y retorna un `ResponseEntity`. No toma decisiones de negocio.

El Service es el arquetipo de **control**: tiene las reglas del dominio. Decide si se puede crear un recurso, valida invariantes, orquesta repositorios. No sabe nada de HTTP.

Esta separación es lo que hace posible testear la lógica de negocio con tests de integración sin levantar un servidor HTTP.

---

**P: ¿Por qué la Entity y el DTO son arquetipos distintos si a veces tienen los mismos campos?**

La Entity representa el estado persistido y tiene ciclo de vida gestionado por Hibernate (puede tener proxies lazy, anotaciones JPA, etc.). El DTO representa un mensaje que viaja por la red: es inmutable, serializable a JSON, y su forma puede ser distinta según quién consulte (un `GET` puede retornar más campos que un `POST`). Fusionarlos genera acoplamiento entre el modelo de persistencia y el contrato de la API.

---

## PARTE 7 — MODELO ANALÍTICO: ROLAP Y STAR SCHEMA

### ¿Qué es un modelo Star Schema?

Es un modelo de base de datos optimizado para consultas analíticas. Tiene:
- Una **tabla de hechos** (fact table) con métricas numéricas
- **Tablas de dimensiones** con los contextos (quién, qué, cuándo)

### Tablas en `analytics-service`

```
fact_gestion_proyectos
  ├── id_proyecto  ──► dim_proyecto (nombre, estado, fechas)
  ├── id_tiempo    ──► dim_tiempo   (fecha, día, mes, trimestre)
  ├── total_tareas
  ├── tareas_completadas
  ├── tareas_pendientes
  ├── lead_time_promedio_dias
  └── tasa_completitud

fact_monitoreo_servicios
  ├── id_tiempo    ──► dim_tiempo
  ├── servicio_origen
  ├── metodo
  ├── latencia_ms
  └── codigo_http
```

### El proceso ETL

**ETL = Extract, Transform, Load.** Se ejecuta automáticamente cada minuto (`@Scheduled(cron = "0 * * * * *")`):

1. **Extract:** Llama a `project-service:8081/proyectos` por HTTP con headers internos de autenticación
2. **Transform:** Calcula `tasaCompletitud`, cuenta `tareasCompletadas`, actualiza `DimProyecto`
3. **Load:** Inserta/actualiza registros en `fact_gestion_proyectos`

Los datos de monitoreo técnico (`fact_monitoreo_servicios`) los escribe directamente el `MonitoringAspect` de `project-service` en la base de datos de analytics via `JdbcTemplate`.

### ¿Por qué ROLAP en lugar de una base de datos externa (ej. BigQuery)?

Para el volumen de datos del proyecto (120 empleados, decenas de proyectos) PostgreSQL con un star schema propio es más que suficiente, no agrega dependencias externas de pago, y el equipo ya conoce SQL estándar. A escala masiva (millones de registros, petabytes), sí tendría sentido mover a un Data Warehouse cloud.

---

## PARTE 8 — FRONTEND: NEXT.JS 16 APP ROUTER

### Modelo de rendering

Next.js 16 con **App Router** usa **Server Components** por defecto: el componente se ejecuta en el servidor, hace el fetch de la API, y manda HTML renderizado al browser. El browser nunca ve el token JWT ni hace llamadas directas al backend.

```
[Browser] ──► GET /  ──► [Next.js Server]
                              │
                              ├──► fetch /api/analytics/kpis/productivity  (server-side)
                              ├──► fetch /api/analytics/kpis/system-health (server-side)
                              └──► fetch /proyectos                        (server-side)
                              │
                              └──► retorna HTML completo al browser
```

### Server Actions

Los formularios (login, crear proyecto, actualizar tarea) usan **Server Actions** (`app/actions.ts`): funciones que se ejecutan en el servidor al hacer submit del form, sin necesidad de un endpoint API explícito en el frontend.

```typescript
// En el servidor — el browser solo invoca la action, nunca ve esta lógica
export async function loginAction(formData: FormData) {
  const res = await fetch(`${API_GATEWAY_URL}/api/auth/login`, { ... });
  const { token } = await res.json();
  cookies().set('token', token, { httpOnly: true }); // Cookie segura, invisible al JS del browser
  redirect('/');
}
```

### Seguridad de autenticación en frontend

- El JWT se guarda en una **cookie `httpOnly`**: no puede ser robado por JavaScript malicioso (XSS)
- El servidor de Next.js lee la cookie y la inyecta como `Authorization: Bearer ...` en cada llamada al Gateway
- El browser nunca manipula el token directamente

---

## PARTE 9 — SEGURIDAD: MODELO JWT + HEADER PROPAGATION

### Flujo técnico detallado

**Emisión del token (resource-service):**
```java
// JwtUtil.java — usa HMAC-SHA256 con clave de 32+ caracteres
Jwts.builder()
    .subject(username)
    .claim("roles", roles)  // ["ADMIN", "PROFESIONAL"]
    .expiration(new Date(now + 86400000)) // 24 horas
    .signWith(hmacSha256Key)
    .compact();
```

**Validación en Gateway → Propagación downstream:**
```java
// AuthenticationFilter.java — Spring Cloud Gateway
Claims claims = jwtUtil.getClaims(token);
exchange.mutate().request(r -> r
    .header("X-Auth-User", claims.getSubject())      // "juan"
    .header("X-Auth-Roles", "ADMIN,PROFESIONAL")
).build();
```

**Lectura en downstream (project-service, resource-service, analytics-service):**
```java
// HeaderAuthenticationFilter.java — mismo código en los 3 servicios
String username = request.getHeader("X-Auth-User");
String roles    = request.getHeader("X-Auth-Roles");
// Crea un SecurityContext de Spring Security con esos datos
// → @PreAuthorize("hasRole('ADMIN')") funciona normalmente
```

### ¿Por qué no validar el JWT en cada microservicio directamente?

Implicaría que todos los servicios conocen el secreto JWT y lo validan. Si el secreto rota, hay que actualizar todos los servicios. El Gateway centraliza esa responsabilidad: los servicios internos confían en los headers porque saben que solo el Gateway (dentro de la red Docker) puede inyectarlos.

---

## PARTE 10 — INFRAESTRUCTURA: DOCKER COMPOSE

El sistema completo corre en **Docker Compose** con una red interna `innovatech-network`. Beneficios:

- Los servicios se comunican por nombre de contenedor (`http://project-service:8081`) sin importar IPs dinámicas
- Los puertos al host están mapeados solo donde se necesita acceso externo
- PostgreSQL, pgAdmin y todos los servicios arrancan con un solo comando: `docker-compose up -d --build`

---

## PARTE 11 — PREGUNTAS DEL PROFESOR Y RESPUESTAS

---

### BLOQUE A — ARQUITECTURA GENERAL

**P: ¿Por qué eligieron microservicios sobre un monolito?**

El caso establece 120 usuarios concurrentes (RNF2), tres módulos con responsabilidades claramente distintas (proyectos, recursos, analítica), y la necesidad de escalar y agregar funcionalidades sin interrupciones (RNF4). Un monolito tendría un único punto de fallo, escalaría todo junto aunque solo una parte esté congestionada, y cualquier cambio en el módulo de analítica requeriría redesplegar toda la aplicación, afectando la disponibilidad del módulo de proyectos. Los microservicios permiten escalar, desplegar y fallar de forma independiente.

---

**P: ¿Cuál es el mayor riesgo de los microservicios que asumieron?**

La **consistencia eventual de los datos**. Como cada servicio tiene su propia base de datos, no hay transacciones ACID entre ellos. Por ejemplo, si un proyecto es eliminado en `project-service`, las asignaciones en `resource-service` que referencian ese `id_proyecto` quedan como referencias huérfanas — no hay CASCADE. En este proyecto asumimos ese riesgo dado el scope académico. En producción se abordaría con el patrón **Saga** (transacciones distribuidas mediante eventos) o con validaciones explícitas en el código.

---

**P: ¿Qué diferencia hay entre el API Gateway y un servidor web normal?**

Un servidor web sirve contenido estático o genera respuestas por sí mismo. El API Gateway es un **proxy inteligente**: no tiene lógica de negocio, no accede a bases de datos propias, solo inspecciona, valida, transforma y redirige peticiones entre clientes y servicios. En este proyecto usa **Spring Cloud Gateway** que es reactivo (no bloquea threads mientras espera respuesta), lo que lo hace eficiente para manejar muchas conexiones concurrentes.

---

**P: ¿Por qué Spring Cloud Gateway y no Nginx o Kong como API Gateway?**

Nginx puede hacer reverse proxy y balanceo de carga, pero no tiene lógica programable fácilmente en Java — agregar validación JWT requeriría módulos Lua o configuración externa. Kong es una opción más enterprise pero agrega una dependencia externa con su propia base de datos. Spring Cloud Gateway se integra nativamente con el ecosistema Spring Boot que ya usamos, permite escribir filtros como clases Java normales, y usa el mismo lenguaje que el resto del backend.

---

### BLOQUE B — BASE DE DATOS

**P: ¿Por qué una base de datos por servicio en lugar de una compartida?**

Con una base de datos compartida, si el schema de `proyectos` cambia, puede romper `resource-service` sin aviso. Si la base de datos se satura, todos los servicios fallan juntos. La independencia de datos es lo que le da sentido real a los microservicios: cada servicio es dueño de su dominio de datos y puede cambiar su schema sin coordinación externa.

---

**P: ¿Cómo manejan la integridad referencial entre servicios entonces?**

No existe FK real entre bases de datos distintas. En `resource-service`, `id_proyecto` e `id_tarea` son solo números BIGINT. La integridad es **responsabilidad del código de negocio**: antes de crear un recurso, el sistema debería verificar que ese proyecto exista en `project-service` (esto es una deuda técnica actual del proyecto). En sistemas de producción, esto se maneja con eventos de dominio (ej. Kafka): cuando un proyecto es eliminado, `project-service` publica un evento `ProyectoEliminado` y `resource-service` lo consume para limpiar sus asignaciones.

---

**P: ¿Por qué resource-service usa Flyway y los demás no?**

`resource-service` maneja el schema más crítico y con más cambios evolutivos: usuarios, roles, asignaciones. Flyway garantiza que los cambios de schema sean reproducibles, versionados y auditables. Los demás servicios usan `ddl-auto=update` de Hibernate, que es conveniente para desarrollo pero no recomendado en producción porque puede hacer cambios destructivos al schema sin control explícito. Lo ideal sería migrar todos a Flyway.

---

**P: ¿Qué es el modelo Star Schema y para qué sirve?**

Es un modelo de datos optimizado para **consultas analíticas OLAP**, no para operaciones transaccionales OLTP. Tiene una tabla central de hechos con métricas numéricas (totales, promedios, tasas) y tablas de dimensiones alrededor con los contextos (qué proyecto, en qué fecha, qué recurso). Permite calcular KPIs con queries SQL simples y eficientes sobre datos históricos acumulados, sin necesidad de JOINs complejos entre múltiples tablas transaccionales.

---

**P: ¿Por qué el ETL corre cada minuto en lugar de en tiempo real?**

El ETL actual es una **simulación de batch scheduling** para propósitos académicos. En un escenario real, el ETL nocturno (ej. medianoche) consolida el día anterior en el Data Warehouse, y el dashboard muestra datos del día anterior. Si se necesita tiempo real, la arquitectura correcta sería **event-driven con Apache Kafka**: cada vez que una tarea cambia de estado, `project-service` publica un evento que `analytics-service` consume inmediatamente. Kafka agrega complejidad operativa significativa que está fuera del scope del curso.

---

### BLOQUE C — BACKEND / CÓDIGO

**P: ¿Qué es AOP y por qué lo usaron?**

AOP (Aspect-Oriented Programming) permite separar las **preocupaciones transversales** (logging, métricas, seguridad, transacciones) del código de negocio principal. En lugar de poner código de medición de tiempo dentro de cada método de `ProyectoService`, definimos un Aspect con `@Around` que intercepta todos esos métodos de forma transparente. `ProyectoService` no sabe que está siendo monitoreado. La alternativa es la duplicación de código: poner `long start = System.currentTimeMillis()` al inicio de cada método, lo cual viola el principio DRY y es difícil de mantener.

---

**P: ¿Por qué usan DTOs en lugar de exponer las entidades JPA directamente?**

Tres razones: (1) **Evitar serialización circular** — una entidad `Proyecto` tiene una lista de `Tarea`, y si `Tarea` tiene referencia de vuelta a `Proyecto`, Jackson (el serializador JSON) entraría en loop infinito. (2) **Separación de contratos** — un cambio interno en la entidad (agregar un campo de auditoría) no debería cambiar la API pública. (3) **Validación controlada** — el `CreateProyectoDTO` tiene solo los campos que el cliente puede enviar, sin exponer el ID autogenerado ni campos de auditoría.

---

**P: ¿Cómo funcionan los tests de integración?**

Usan `@SpringBootTest` que levanta todo el contexto de Spring en memoria. Para evitar depender de una base de datos PostgreSQL real, en las propiedades del test se configura H2 (base de datos en memoria compatible con SQL estándar):

```java
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
```

`MonitoringAspect` se mockea porque usa `JdbcTemplate` para escribir en la base de datos de analytics, que no existe en el entorno de test. El test verifica comportamiento real de la capa de negocio y persistencia sin depender de infraestructura externa.

---

**P: ¿Por qué la autenticación JWT está en resource-service y no en un servicio separado?**

Por principio de simplificación para el scope del proyecto. Lo ideal en producción sería un **Identity Provider externo** (Keycloak, AWS Cognito, Auth0) integrado directamente con el API Gateway — el Gateway delega la validación al IdP, y los servicios de dominio no saben nada de autenticación. En nuestro caso, `resource-service` maneja tanto los datos de profesionales como sus credenciales. Es una deuda técnica conocida: si se quisiera cambiar el mecanismo de autenticación (ej. pasar a OAuth2), habría que modificar `resource-service`.

---

### BLOQUE D — FRONTEND

**P: ¿Qué son los Server Components de Next.js y por qué los usaron?**

En el App Router de Next.js, los componentes se ejecutan en el servidor por defecto. El servidor hace el fetch a la API, renderiza el HTML con los datos, y envía HTML terminado al browser. Beneficios: el browser no expone el JWT ni las URLs del backend, el contenido es visible para SEO desde el primer request, y se elimina la necesidad de gestionar estados de loading/error en el cliente. La alternativa clásica es un SPA (React puro) donde el browser hace las llamadas a la API y gestiona el estado con hooks — más flexible para interactividad, pero expone las URLs del backend y requiere más lógica de estado en el cliente.

---

**P: ¿Por qué guardan el JWT en una cookie httpOnly en lugar de localStorage?**

`localStorage` es accesible por cualquier JavaScript de la página. Un ataque **XSS** (inyección de script malicioso) podría robar el token de localStorage. Una cookie `httpOnly` no es accesible desde JavaScript — solo el browser la envía automáticamente en cada request. Esto elimina el vector de ataque XSS para el robo de tokens. La desventaja es que las cookies httpOnly son vulnerables a CSRF (Cross-Site Request Forgery), que se mitiga con tokens CSRF o con el atributo `SameSite=Strict`.

---

**P: ¿Qué son las Server Actions?**

Son funciones de servidor marcadas con `'use server'` que se invocan directamente desde formularios HTML. Cuando el usuario hace submit de un form, Next.js envía los datos al servidor, ejecuta la función (que puede llamar APIs, manejar cookies, redirigir) y retorna. No hay necesidad de crear un endpoint API en el frontend ni de usar `fetch()` desde el cliente. Simplifican drásticamente la autenticación, creación de proyectos y actualización de tareas.

---

### BLOQUE E — PREGUNTAS DIFÍCILES / TRAMPA

**P: Si project-service y analytics-service se comunican internamente, ¿por qué no crearon una librería compartida en lugar de duplicar los DTOs de integración?**

Es una decisión de trade-off en microservicios. Una librería compartida (`innovatech-commons`) crea **acoplamiento en tiempo de compilación**: si cambia el DTO en la librería, todos los servicios que la usan deben recompilarse y redesplegrarse juntos, lo que niega una de las ventajas principales de microservicios. La duplicación deliberada de DTOs de integración (como `ProyectoIntegrationDTO` en analytics-service) es un patrón aceptado: cada servicio define su propio contrato de lo que necesita del exterior, y si el contrato cambia, solo ese servicio es afectado.

---

**P: El MonitoringAspect escribe directamente en la base de datos de analytics usando JdbcTemplate. ¿Es eso una buena práctica?**

No es ideal. Viola el principio de Database per Service: `project-service` tiene una dependencia directa en la base de datos de `analytics-service`. Si la base de analytics está caída, el MonitoringAspect puede lanzar excepciones que afecten al servicio de proyectos. La solución correcta sería publicar las métricas como eventos HTTP o mensajes de cola (ej. un endpoint `POST /api/analytics/metrics`) y que analytics-service los procese de forma asíncrona. En el proyecto actual esto es una deuda técnica aceptada para simplificar la implementación académica.

---

**P: ¿Cómo escalarían el sistema si tuvieran 10,000 usuarios concurrentes?**

1. **Horizontal scaling de microservicios:** Múltiples instancias de `project-service` detrás de un balanceador de carga (ya soportado por Docker Swarm o Kubernetes)
2. **Separar el Gateway:** Mover de Spring Cloud Gateway a un gateway especializado como Kong o AWS API Gateway con auto-scaling
3. **Caché:** Agregar Redis para cachear respuestas frecuentes del analytics (KPIs que no cambian cada segundo)
4. **Event-driven analytics:** Reemplazar el ETL por streaming con Kafka para analítica en tiempo real
5. **Read replicas en PostgreSQL:** Las queries analíticas irían a réplicas de solo lectura, sin afectar las escrituras transaccionales

---

**P: ¿Qué agregarían si tuvieran más tiempo?**

1. **Circuit Breaker (Resilience4j):** Proteger las llamadas del ETL a `project-service` — si el servicio no responde, el circuit breaker detiene los intentos y evita encolar el sistema
2. **Saga Pattern:** Para manejar la consistencia eventual entre bases de datos (ej. eliminar proyecto → limpiar asignaciones)
3. **Tests unitarios en resource-service y analytics-service** — actualmente solo project-service tiene tests
4. **HTTPS y secreto JWT en variables de entorno** — actualmente el secreto tiene un valor por defecto en código
5. **Paginación en todos los endpoints** — actualmente retornan todas las entidades sin límite

---

*Documento generado para uso interno de preparación de defensa oral — Innovatech Solutions*
