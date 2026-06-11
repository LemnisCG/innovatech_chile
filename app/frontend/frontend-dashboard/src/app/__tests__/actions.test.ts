import { describe, it, expect, vi } from 'vitest';
import { http, HttpResponse } from 'msw';
import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';
import { server, validCredentials } from '../../../tests/fixtures/mocks';
import {
  loginAction,
  logoutAction,
  registerAction,
  createProjectAction,
  createTaskAction,
  updateTaskStatusAction,
} from '../actions';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

const buildForm = (fields: Record<string, string>) => {
  const formData = new FormData();
  Object.entries(fields).forEach(([key, value]) => formData.set(key, value));
  return formData;
};

// Reemplaza el cookie store del setup global por uno espiable; si se pasa
// `token`, simula un usuario autenticado (getAuthHeaders agrega el Bearer).
const mockCookieStore = ({ token }: { token?: string } = {}) => {
  const store = {
    get: vi.fn((name: string) => (name === 'token' && token ? { name, value: token } : undefined)),
    set: vi.fn(),
    delete: vi.fn(),
  };
  vi.mocked(cookies).mockResolvedValue(store as unknown as Awaited<ReturnType<typeof cookies>>);
  return store;
};

describe('loginAction', () => {
  it('guarda las cookies de sesión y redirige cuando las credenciales son correctas', async () => {
    const store = mockCookieStore();

    await expect(loginAction({}, buildForm(validCredentials))).rejects.toThrow('NEXT_REDIRECT:/');

    expect(store.set).toHaveBeenCalledWith('token', 'jwt-mock-token', expect.objectContaining({ httpOnly: true }));
    expect(store.set).toHaveBeenCalledWith('session', validCredentials.username, expect.objectContaining({ path: '/' }));
  });

  it('devuelve "Credenciales inválidas" cuando el gateway responde 401, sin tocar cookies', async () => {
    const store = mockCookieStore();

    const state = await loginAction({}, buildForm({ username: 'testuser', password: 'wrong-password' }));

    expect(state).toEqual({ error: 'Credenciales inválidas' });
    expect(store.set).not.toHaveBeenCalled();
  });

  it('devuelve un error de conexión cuando el gateway es inalcanzable', async () => {
    server.use(http.post(`${API_GATEWAY_URL}/api/auth/login`, () => HttpResponse.error()));

    const state = await loginAction({}, buildForm(validCredentials));

    expect(state).toEqual({ error: 'Error de conexión con el servidor' });
  });
});

describe('logoutAction', () => {
  it('borra las cookies de sesión y redirige al login', async () => {
    const store = mockCookieStore({ token: 'jwt-activo' });

    await expect(logoutAction()).rejects.toThrow('NEXT_REDIRECT:/login');

    expect(store.delete).toHaveBeenCalledWith('token');
    expect(store.delete).toHaveBeenCalledWith('session');
  });
});

describe('registerAction', () => {
  const registroValido = {
    username: 'nuevo-usuario',
    email: 'nuevo@innovatech.cl',
    password: 'clave-segura',
    especialidad: 'QA',
    telefono: '+56911111111',
    direccion: 'Av. Siempre Viva 123',
    rut: '11.111.111-1',
  };

  it('envía el payload completo, guarda la sesión y redirige al dashboard', async () => {
    const store = mockCookieStore();
    let payloadRecibido: Record<string, unknown> | undefined;
    server.use(
      http.post(`${API_GATEWAY_URL}/api/auth/register`, async ({ request }) => {
        payloadRecibido = (await request.json()) as Record<string, unknown>;
        return HttpResponse.json({ token: 'jwt-mock-token-registro' });
      }),
    );

    await expect(registerAction(buildForm(registroValido))).rejects.toThrow('NEXT_REDIRECT:/');

    expect(payloadRecibido).toEqual({ ...registroValido, estado: 'ACTIVO' });
    expect(store.set).toHaveBeenCalledWith('token', 'jwt-mock-token-registro', expect.objectContaining({ httpOnly: true }));
    expect(store.set).toHaveBeenCalledWith('session', registroValido.username, expect.objectContaining({ path: '/' }));
  });

  // Comportamiento actual: el catch re-envuelve cualquier fallo (incluido un 409
  // de duplicado) como error de conexión. Ver nota en el informe de tests.
  it('lanza un error y no guarda cookies cuando el username está duplicado', async () => {
    const store = mockCookieStore();

    await expect(registerAction(buildForm({ ...registroValido, username: 'usuario-duplicado' }))).rejects.toThrow(
      'Error de conexión con el servidor',
    );
    expect(store.set).not.toHaveBeenCalled();
  });
});

describe('createProjectAction', () => {
  const proyectoForm = {
    nombre: 'Proyecto E2E',
    estado: 'PENDIENTE',
    fechaInicio: '2026-07-01',
    fechaFin: '2026-12-31',
    descripcion: 'Cobertura completa del frontend',
  };

  it('envía el proyecto con el token de sesión, revalida y redirige al dashboard', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    let authHeader: string | null = null;
    let payloadRecibido: Record<string, unknown> | undefined;
    server.use(
      http.post(`${API_GATEWAY_URL}/proyectos`, async ({ request }) => {
        authHeader = request.headers.get('Authorization');
        payloadRecibido = (await request.json()) as Record<string, unknown>;
        return new HttpResponse(null, { status: 201 });
      }),
    );

    await expect(createProjectAction(buildForm(proyectoForm))).rejects.toThrow('NEXT_REDIRECT:/');

    expect(authHeader).toBe('Bearer jwt-activo');
    expect(payloadRecibido).toMatchObject(proyectoForm);
    expect(vi.mocked(revalidatePath)).toHaveBeenCalledWith('/');
  });

  it('lanza un error y no redirige cuando el backend rechaza la creación', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    server.use(http.post(`${API_GATEWAY_URL}/proyectos`, () => new HttpResponse(null, { status: 500 })));

    await expect(createProjectAction(buildForm(proyectoForm))).rejects.toThrow('Error de conexión con el servidor');
    expect(vi.mocked(revalidatePath)).not.toHaveBeenCalled();
  });
});

describe('createTaskAction', () => {
  it('parsea el responsable a número, revalida y redirige al detalle del proyecto', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    let payloadRecibido: Record<string, unknown> | undefined;
    server.use(
      http.post(`${API_GATEWAY_URL}/proyectos/7/tareas`, async ({ request }) => {
        payloadRecibido = (await request.json()) as Record<string, unknown>;
        return new HttpResponse(null, { status: 201 });
      }),
    );

    const form = buildForm({
      nombre: 'Diseñar esquema',
      descripcion: 'Modelo estrella para analytics',
      estado: 'EN_PROGRESO',
      idProfesionalAsignado: '2',
      fechaInicio: '2026-07-01',
      fechaFin: '2026-07-15',
      comentarios: 'Revisar con el equipo de datos',
    });

    await expect(createTaskAction('7', form)).rejects.toThrow('NEXT_REDIRECT:/projects/7');

    expect(payloadRecibido).toMatchObject({ nombre: 'Diseñar esquema', estado: 'EN_PROGRESO', idProfesionalAsignado: 2 });
    expect(vi.mocked(revalidatePath)).toHaveBeenCalledWith('/projects/7');
  });

  it('usa PENDIENTE como estado por defecto y null cuando no hay responsable', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    let payloadRecibido: Record<string, unknown> | undefined;
    server.use(
      http.post(`${API_GATEWAY_URL}/proyectos/7/tareas`, async ({ request }) => {
        payloadRecibido = (await request.json()) as Record<string, unknown>;
        return new HttpResponse(null, { status: 201 });
      }),
    );

    const form = buildForm({ nombre: 'Tarea mínima', descripcion: 'Sin asignar' });

    await expect(createTaskAction('7', form)).rejects.toThrow('NEXT_REDIRECT:/projects/7');

    expect(payloadRecibido).toMatchObject({ estado: 'PENDIENTE', idProfesionalAsignado: null });
  });

  it('lanza un error cuando el backend rechaza la tarea', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    server.use(http.post(`${API_GATEWAY_URL}/proyectos/7/tareas`, () => new HttpResponse(null, { status: 500 })));

    await expect(createTaskAction('7', buildForm({ nombre: 'x', descripcion: 'y' }))).rejects.toThrow(
      'Error de conexión con el servidor',
    );
  });
});

describe('updateTaskStatusAction', () => {
  it('envía el nuevo estado con el userId, revalida y redirige al proyecto', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    let payloadRecibido: Record<string, unknown> | undefined;
    server.use(
      http.put(`${API_GATEWAY_URL}/tareas/3/estado`, async ({ request }) => {
        payloadRecibido = (await request.json()) as Record<string, unknown>;
        return new HttpResponse(null, { status: 200 });
      }),
    );

    await expect(updateTaskStatusAction('7', 3, 2, buildForm({ estado: 'COMPLETADO' }))).rejects.toThrow(
      'NEXT_REDIRECT:/projects/7',
    );

    expect(payloadRecibido).toEqual({ estado: 'COMPLETADO', userId: '2' });
    expect(vi.mocked(revalidatePath)).toHaveBeenCalledWith('/projects/7');
  });

  it('propaga el error con el status cuando el backend falla', async () => {
    mockCookieStore({ token: 'jwt-activo' });
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
    server.use(http.put(`${API_GATEWAY_URL}/tareas/3/estado`, () => new HttpResponse(null, { status: 500 })));

    await expect(updateTaskStatusAction('7', 3, 2, buildForm({ estado: 'COMPLETADO' }))).rejects.toThrow(
      'Error al actualizar el estado de la tarea. Status: 500',
    );

    consoleError.mockRestore();
  });
});
