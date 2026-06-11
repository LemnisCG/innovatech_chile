import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { cookies } from 'next/headers';
import { Navigation } from '../Navigation';

// Navigation es un Server Component asíncrono: se resuelve su JSX con `await`
// antes de pasarlo a render(), igual que haría Next.js en el servidor.
const renderNavigation = async () => render(await Navigation());

const mockSession = (username?: string) => {
  vi.mocked(cookies).mockResolvedValue({
    get: vi.fn((name: string) => (name === 'session' && username ? { name, value: username } : undefined)),
    set: vi.fn(),
    delete: vi.fn(),
  } as unknown as Awaited<ReturnType<typeof cookies>>);
};

describe('Navigation', () => {
  it('muestra los accesos de invitado cuando no hay sesión', async () => {
    mockSession(undefined);

    await renderNavigation();

    expect(screen.getByRole('link', { name: 'Ingresar' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Registro' })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Salir' })).not.toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Nuevo Proyecto' })).not.toBeInTheDocument();
  });

  it('muestra el saludo, la navegación interna y el botón de salir con sesión activa', async () => {
    mockSession('mzamora');

    await renderNavigation();

    expect(screen.getByText('Hola, mzamora')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Salir' })).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Nuevo Proyecto' })).toBeInTheDocument();
    expect(screen.queryByRole('link', { name: 'Ingresar' })).not.toBeInTheDocument();
  });
});
