import { describe, it, expect } from 'vitest';
import { http, HttpResponse } from 'msw';
import { server } from '../../../tests/fixtures/mocks';
import { fetchProjects, fetchProductivity } from '../api';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

describe('API Integration Tests', () => {
  it('fetchProjects debe traer los proyectos del gateway', async () => {
    const projects = await fetchProjects();
    expect(projects).toHaveLength(3);
    expect(projects[0]).toHaveProperty('nombre');
  });

  it('fetchProductivity debe devolver valores por defecto cuando el gateway falla', async () => {
    server.use(
      http.get(`${API_GATEWAY_URL}/api/analytics/kpis/productivity`, () => {
        return new HttpResponse(null, { status: 500 });
      }),
    );

    const data = await fetchProductivity();
    expect(data).toEqual({ leadTimePromedioDias: 0, tasaCompletitud: 0, totalProyectosActivos: 0 });
  });
});
