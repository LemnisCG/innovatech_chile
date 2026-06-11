import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import type { Proyecto, Usuario } from '../../src/services/api';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

export const proyectosFixture: Proyecto[] = [
  {
    id: 1,
    nombre: 'Migración a microservicios',
    descripcion: 'Separar el monolito en servicios independientes',
    estado: 'EN_PROGRESO',
    fechaInicio: '2026-01-10',
    fechaFin: '2026-06-30',
    comentarios: 'Prioridad alta',
    tareasDelProyecto: [
      { id: 1, nombre: 'Diseñar API Gateway', descripcion: 'Definir rutas y filtros', estado: 'COMPLETADO', idProfesionalAsignado: 1 },
    ],
  },
  {
    id: 2,
    nombre: 'Dashboard de analítica',
    descripcion: 'Construir KPIs en tiempo real',
    estado: 'PENDIENTE',
    fechaInicio: '2026-03-01',
    fechaFin: '2026-09-01',
    comentarios: '',
    tareasDelProyecto: [],
  },
  {
    id: 3,
    nombre: 'Renovación de autenticación',
    descripcion: 'Migrar a JWT centralizado',
    estado: 'COMPLETADO',
    fechaInicio: '2025-11-01',
    fechaFin: '2026-02-15',
    comentarios: 'Cerrado sin observaciones',
    tareasDelProyecto: [
      { id: 2, nombre: 'Configurar resource-service', descripcion: 'Endpoints de login y registro', estado: 'COMPLETADO', idProfesionalAsignado: 2 },
      { id: 3, nombre: 'Validar filtro en gateway', descripcion: 'AuthenticationFilter reactivo', estado: 'COMPLETADO', idProfesionalAsignado: 2 },
    ],
  },
];

export const productivityFixture = {
  leadTimePromedioDias: 5,
  tasaCompletitud: 85,
  totalProyectosActivos: 3,
};

export const systemHealthFixture = {
  latenciaPromedioMs: 120,
  tasaErroresPorcentaje: 2,
};

export const usuariosFixture: Usuario[] = [
  { id: 1, username: 'mzamora', email: 'mzamora@innovatech.cl', especialidad: 'Backend', estado: 'ACTIVO', roles: ['ADMIN'] },
  { id: 2, username: 'jperez', email: 'jperez@innovatech.cl', especialidad: 'Frontend', estado: 'ACTIVO' },
];

// Credenciales que el handler de login acepta como válidas; los tests las
// importan de aquí para no duplicar strings mágicos.
export const validCredentials = { username: 'testuser', password: 'password123' };

export const handlers = [
  http.post(`${API_GATEWAY_URL}/api/auth/login`, async ({ request }) => {
    const body = (await request.json()) as { username: string; password: string };
    if (body.username === validCredentials.username && body.password === validCredentials.password) {
      return HttpResponse.json({ token: 'jwt-mock-token' });
    }
    return new HttpResponse(null, { status: 401 });
  }),

  http.post(`${API_GATEWAY_URL}/api/auth/register`, async ({ request }) => {
    const body = (await request.json()) as { username: string };
    if (body.username === 'usuario-duplicado') {
      return new HttpResponse(null, { status: 409 });
    }
    return HttpResponse.json({ token: 'jwt-mock-token-registro' });
  }),

  http.get(`${API_GATEWAY_URL}/proyectos`, () => {
    return HttpResponse.json(proyectosFixture);
  }),

  http.get(`${API_GATEWAY_URL}/proyectos/:id`, ({ params }) => {
    const proyecto = proyectosFixture.find((p) => p.id === Number(params.id));
    return proyecto ? HttpResponse.json(proyecto) : new HttpResponse(null, { status: 404 });
  }),

  http.post(`${API_GATEWAY_URL}/proyectos`, () => {
    return new HttpResponse(null, { status: 201 });
  }),

  http.post(`${API_GATEWAY_URL}/proyectos/:id/tareas`, () => {
    return new HttpResponse(null, { status: 201 });
  }),

  http.put(`${API_GATEWAY_URL}/tareas/:id/estado`, () => {
    return new HttpResponse(null, { status: 200 });
  }),

  http.get(`${API_GATEWAY_URL}/usuarios`, () => {
    return HttpResponse.json(usuariosFixture);
  }),

  http.get(`${API_GATEWAY_URL}/api/analytics/kpis/productivity`, () => {
    return HttpResponse.json(productivityFixture);
  }),

  http.get(`${API_GATEWAY_URL}/api/analytics/kpis/system-health`, () => {
    return HttpResponse.json(systemHealthFixture);
  }),
];

export const server = setupServer(...handlers);
