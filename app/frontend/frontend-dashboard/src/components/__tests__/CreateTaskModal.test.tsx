import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { CreateTaskModal } from '../CreateTaskModal';
import { usuariosFixture } from '../../../tests/fixtures/mocks';

describe('CreateTaskModal', () => {
  it('pide iniciar sesión cuando el usuario no está autenticado', () => {
    render(<CreateTaskModal usuarios={[]} addTaskAction={vi.fn()} isLoggedIn={false} />);

    expect(screen.getByText('Debes iniciar sesión para agregar tareas.')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Ir al Login' })).toHaveAttribute('href', '/login');
    expect(screen.queryByRole('button', { name: /Agregar Nueva Tarea/ })).not.toBeInTheDocument();
  });

  it('abre el modal con los responsables disponibles al pulsar el botón', async () => {
    const user = userEvent.setup();
    render(<CreateTaskModal usuarios={usuariosFixture} addTaskAction={vi.fn()} isLoggedIn={true} />);

    await user.click(screen.getByRole('button', { name: /Agregar Nueva Tarea/ }));

    expect(screen.getByRole('heading', { name: 'Crear Nueva Tarea' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'mzamora (Backend)' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'jperez (Frontend)' })).toBeInTheDocument();
  });

  it('envía el formulario a la action y cierra el modal', async () => {
    const user = userEvent.setup();
    const addTaskAction = vi.fn();
    render(<CreateTaskModal usuarios={usuariosFixture} addTaskAction={addTaskAction} isLoggedIn={true} />);

    await user.click(screen.getByRole('button', { name: /Agregar Nueva Tarea/ }));
    await user.type(screen.getByLabelText('Nombre de la Tarea'), 'Configurar CI');
    await user.type(screen.getByLabelText('Descripción'), 'Pipeline de tests del frontend');
    await user.selectOptions(screen.getByLabelText('Estado'), 'EN_PROGRESO');
    await user.selectOptions(screen.getByLabelText('Responsable'), '2');
    await user.click(screen.getByRole('button', { name: 'Crear Tarea' }));

    expect(addTaskAction).toHaveBeenCalledTimes(1);
    const formData = addTaskAction.mock.calls[0][0] as FormData;
    expect(formData.get('nombre')).toBe('Configurar CI');
    expect(formData.get('descripcion')).toBe('Pipeline de tests del frontend');
    expect(formData.get('estado')).toBe('EN_PROGRESO');
    expect(formData.get('idProfesionalAsignado')).toBe('2');
    expect(screen.queryByRole('heading', { name: 'Crear Nueva Tarea' })).not.toBeInTheDocument();
  });

  it('cierra el modal sin enviar nada al pulsar Cancelar', async () => {
    const user = userEvent.setup();
    const addTaskAction = vi.fn();
    render(<CreateTaskModal usuarios={usuariosFixture} addTaskAction={addTaskAction} isLoggedIn={true} />);

    await user.click(screen.getByRole('button', { name: /Agregar Nueva Tarea/ }));
    await user.click(screen.getByRole('button', { name: 'Cancelar' }));

    expect(addTaskAction).not.toHaveBeenCalled();
    expect(screen.queryByRole('heading', { name: 'Crear Nueva Tarea' })).not.toBeInTheDocument();
  });
});
