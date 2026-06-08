import { http, HttpResponse } from 'msw';
import { setupServer } from 'msw/node';
import type { Proyecto } from '../../src/services/api';

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

export const handlers = [
  http.post(`${API_GATEWAY_URL}/api/auth/login`, async ({ request }) => {
    const body = (await request.json()) as { username: string; password: string };
    if (body.username === 'testuser' && body.password === 'password123') {
      return HttpResponse.json({ token: 'jwt-mock-token' });
    }
    return new HttpResponse(null, { status: 401 });
  }),

  http.get(`${API_GATEWAY_URL}/proyectos`, () => {
    return HttpResponse.json(proyectosFixture);
  }),

  http.get(`${API_GATEWAY_URL}/api/analytics/kpis/productivity`, () => {
    return HttpResponse.json(productivityFixture);
  }),
];

export const server = setupServer(...handlers);
