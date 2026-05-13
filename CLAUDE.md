# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Innovatech Solutions is a Spring Boot microservices monorepo (Java 21, Spring Boot 3.2.4) with a Next.js 16 frontend. The system manages projects, resources/personnel, and provides analytics KPIs.

## Commands

### Full stack (Docker)
```bash
docker-compose up -d --build      # Start all services
docker-compose up -d project-db resource-db analytics-db pgadmin  # DBs only (for local dev)
docker-compose down
docker-compose logs <service>
```

### Backend (Maven multi-module)
```bash
./mvnw clean install              # Build all modules
./mvnw -pl app/backend/project-service spring-boot:run    # Run one service
./mvnw -pl app/backend/project-service test               # Test one service
./mvnw test                       # Test all modules
```

### Frontend
```bash
cd app/frontend/frontend-dashboard
npm install
npm run dev      # Dev server on http://localhost:3000
npm run build
npm run lint
```

## Architecture

### Services and Ports
| Service | Port | DB |
|---------|------|----|
| api-gateway | 9000 | — |
| project-service | 8081 | postgres:5435 (`innovatech_projects`) |
| resource-service | 8082 | postgres:5434 (`innovatech_resources`) |
| analytics-service | 8083 | postgres:5436 (`innovatech_analytics`) |
| frontend (Next.js) | 3000 | — |
| pgAdmin | 5052 | — (admin@innovatech.cl / admin) |

### Authentication Flow
JWT is issued exclusively by **resource-service** (`/api/auth/login`, `/api/auth/register`). The **api-gateway** (Spring Cloud Gateway, reactive) validates the token using `AuthenticationFilter` and injects `X-Auth-User` and `X-Auth-Roles` headers into downstream requests. Downstream services (`project-service`, `resource-service`, `analytics-service`) trust those headers via `HeaderAuthenticationFilter` — they never validate JWTs directly. The `/api/auth/**` path is whitelisted in the gateway.

The frontend stores the JWT in an httpOnly cookie named `token` and sends it as a `Bearer` header via Next.js Server Actions/Server Components.

### Analytics (ROLAP + ETL)
`analytics-service` implements a star schema: `fact_gestion_proyectos`, `fact_monitoreo_servicios`, `dim_proyecto`, `dim_recurso`, `dim_tiempo`. The `ETLService` runs a `@Scheduled` cron (every minute in dev) that pulls data from `project-service` over HTTP using hardcoded `X-Auth-User: analytics-system` / `X-Auth-Roles: ADMIN` headers (bypasses gateway auth for internal calls).

`project-service` also writes monitoring metrics to the analytics DB directly via `MonitoringAspect` (AOP `@Around` all `ProyectoService` methods) and `HttpStatusInterceptor` (captures HTTP status codes for controller calls).

### Database Migrations
Only **resource-service** uses Flyway (`spring.jpa.hibernate.ddl-auto=validate`). Migrations live in `app/backend/resource-service/src/main/resources/db/migration/`. Other services use `ddl-auto=update`.

### Frontend
Next.js 16 App Router with Server Components and Server Actions (`app/actions.ts`). All API calls go through the gateway at `NEXT_PUBLIC_API_URL` (default `http://localhost:9000`). The `src/services/api.ts` file contains typed fetch helpers. Authentication is enforced on all routes except `/login` and `/register`.

## Testing

Integration tests in `project-service` use H2 in-memory DB, configured inline in the test class with `@SpringBootTest(properties = {...})`. `MonitoringAspect` must be `@MockBean` in those tests because it uses `JdbcTemplate` to write to the analytics DB which is unavailable in tests.

There are currently no tests in `resource-service` or `analytics-service`.

## Key Cross-Service Conventions

- Task state values: `PENDIENTE`, `EN_PROGRESO`, `COMPLETADO` (the ETL also checks `COMPLETADA` and `DONE` for compatibility).
- Controllers receive and return DTOs (`CreateProyectoDTO`, `ProyectoDTO`, etc.); entities are not exposed directly.
- `resource-service` uses Lombok; the other backend services do not.
- The JWT shared secret is configured via `jwt.secret` property (defaults to a 32-char placeholder). Both `resource-service` and `api-gateway` must use the same secret.
