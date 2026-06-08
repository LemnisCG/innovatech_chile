import { describe, it, expect, vi } from 'vitest';
import { cookies } from 'next/headers';
import { loginAction } from '../actions';

const buildLoginForm = (username: string, password: string) => {
  const formData = new FormData();
  formData.set('username', username);
  formData.set('password', password);
  return formData;
};

describe('loginAction', () => {
  it('debe guardar las cookies de sesión y redirigir cuando las credenciales son correctas', async () => {
    const setCookie = vi.fn();
    vi.mocked(cookies).mockResolvedValueOnce({
      get: vi.fn(() => undefined),
      set: setCookie,
      delete: vi.fn(),
    } as unknown as Awaited<ReturnType<typeof cookies>>);

    await expect(loginAction(buildLoginForm('testuser', 'password123'))).rejects.toThrow('NEXT_REDIRECT');

    expect(setCookie).toHaveBeenCalledWith('token', 'jwt-mock-token', expect.objectContaining({ httpOnly: true }));
    expect(setCookie).toHaveBeenCalledWith('session', 'testuser', expect.objectContaining({ path: '/' }));
  });

  it('debe lanzar un error de conexión cuando el gateway rechaza las credenciales', async () => {
    await expect(loginAction(buildLoginForm('testuser', 'wrong-password'))).rejects.toThrow('Error de conexión con el servidor');
  });
});
