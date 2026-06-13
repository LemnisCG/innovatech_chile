import '@testing-library/jest-dom';
import { afterAll, afterEach, beforeAll, vi } from 'vitest';
import { server } from './fixtures/mocks';

// Ciclo de vida de MSW: se levanta una vez para toda la suite, se resetean
// los handlers tras cada test (aislamiento entre tests) y se cierra al final.
beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

// `cookies()` de `next/headers` solo funciona dentro de un request scope real
// (Server Component / Server Action). Fuera de Next.js lanza
// "called outside a request scope", así que se reemplaza por una cookie store
// falsa sin token. Los tests que necesiten simular un usuario autenticado
// pueden sobreescribir este mock con `vi.mocked(cookies)...`.
vi.mock('next/headers', () => ({
  cookies: vi.fn(async () => ({
    get: vi.fn(() => undefined),
    set: vi.fn(),
    delete: vi.fn(),
  })),
}));

// `redirect()` de `next/navigation` interrumpe la ejecución lanzando una
// excepción especial (`NEXT_REDIRECT`) que Next.js intercepta más arriba en
// el árbol. Replicamos esa señal con un `throw` para poder afirmar, en los
// Server Actions, "esto debía redirigir" con `.rejects.toThrow('NEXT_REDIRECT')`.
vi.mock('next/navigation', () => ({
  redirect: vi.fn((path: string) => {
    throw new Error(`NEXT_REDIRECT:${path}`);
  }),
  notFound: vi.fn(() => {
    throw new Error('NEXT_NOT_FOUND');
  }),
  useRouter: () => ({ push: vi.fn(), replace: vi.fn(), prefetch: vi.fn() }),
  useSearchParams: () => new URLSearchParams(),
  usePathname: () => '',
}));

// `revalidatePath()` toca la caché de Next.js; en tests basta con un stub
// que no haga nada, ya que no probamos comportamiento de caché aquí.
vi.mock('next/cache', () => ({
  revalidatePath: vi.fn(),
}));
