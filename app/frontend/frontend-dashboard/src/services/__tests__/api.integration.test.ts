import { describe, it, expect, vi } from 'vitest';
import { http, HttpResponse } from 'msw';
import { cookies } from 'next/headers';
import {
  server,
  proyectosFixture,
  productivityFixture,
  systemHealthFixture,
  usuariosFixture,
} from '../../../tests/fixtures/mocks';
import { fetchProjects, fetchProjectById, fetchProductivity, fetchSystemHealth, fetchUsuarios } from '../api';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

// Los fetchers atrapan el error y loguean por console.error antes de devolver
// el valor por defecto; lo silenciamos para no ensuciar la salida del runner.
const silenceConsoleError = () => vi.spyOn(console, 'error').mockImplementation(() => {});

describe('fetchProjects', () => {
  it('trae los proyectos del gateway', async () => {
    const projects = await fetchProjects();
    expect(projects).toHaveLength(3);
    expect(projects[0]).toHaveProperty('nombre');
  });

  it('devuelve una lista vacía cuando el gateway falla', async () => {
    silenceConsoleError();
    server.use(http.get(`${API_GATEWAY_URL}/proyectos`, () => new HttpResponse(null, { status: 500 })));

    await expect(fetchProjects()).resolves.toEqual([]);
  });

  it('envía el token de la cookie como header Bearer', async () => {
    vi.mocked(cookies).mockResolvedValue({
      get: vi.fn((name: string) => (name === 'token' ? { name, value: 'jwt-de-prueba' } : undefined)),
      set: vi.fn(),
      delete: vi.fn(),
    } as unknown as Awaited<ReturnType<typeof cookies>>);
    let authHeader: string | null = null;
    server.use(
      http.get(`${API_GATEWAY_URL}/proyectos`, ({ request }) => {
        authHeader = request.headers.get('Authorization');
        return HttpResponse.json(proyectosFixture);
      }),
    );

    await fetchProjects();

    expect(authHeader).toBe('Bearer jwt-de-prueba');
  });
});

describe('fetchProjectById', () => {
  it('trae el proyecto cuando existe', async () => {
    const proyecto = await fetchProjectById('1');
    expect(proyecto?.nombre).toBe(proyectosFixture[0].nombre);
  });

  it('devuelve null cuando el proyecto no existe (404)', async () => {
    await expect(fetchProjectById('999')).resolves.toBeNull();
  });

  it('devuelve null cuando el gateway es inalcanzable', async () => {
    silenceConsoleError();
    server.use(http.get(`${API_GATEWAY_URL}/proyectos/:id`, () => HttpResponse.error()));

    await expect(fetchProjectById('1')).resolves.toBeNull();
  });
});

describe('fetchProductivity', () => {
  it('trae el KPI de productividad del gateway', async () => {
    await expect(fetchProductivity()).resolves.toEqual(productivityFixture);
  });

  it('devuelve valores por defecto cuando el gateway falla', async () => {
    silenceConsoleError();
    server.use(
      http.get(`${API_GATEWAY_URL}/api/analytics/kpis/productivity`, () => new HttpResponse(null, { status: 500 })),
    );

    const data = await fetchProductivity();
    expect(data).toEqual({ leadTimePromedioDias: 0, tasaCompletitud: 0, totalProyectosActivos: 0 });
  });
});

describe('fetchSystemHealth', () => {
  it('trae el KPI de salud del sistema del gateway', async () => {
    await expect(fetchSystemHealth()).resolves.toEqual(systemHealthFixture);
  });

  it('devuelve valores por defecto cuando el gateway falla', async () => {
    silenceConsoleError();
    server.use(
      http.get(`${API_GATEWAY_URL}/api/analytics/kpis/system-health`, () => new HttpResponse(null, { status: 500 })),
    );

    const data = await fetchSystemHealth();
    expect(data).toEqual({ latenciaPromedioMs: 0, tasaErroresPorcentaje: 0 });
  });
});

describe('fetchUsuarios', () => {
  it('trae los usuarios del gateway', async () => {
    await expect(fetchUsuarios()).resolves.toEqual(usuariosFixture);
  });

  it('devuelve una lista vacía cuando el gateway falla', async () => {
    silenceConsoleError();
    server.use(http.get(`${API_GATEWAY_URL}/usuarios`, () => new HttpResponse(null, { status: 500 })));

    await expect(fetchUsuarios()).resolves.toEqual([]);
  });
});
