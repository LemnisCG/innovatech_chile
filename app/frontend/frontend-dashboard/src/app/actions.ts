'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { revalidatePath } from 'next/cache';

const API_GATEWAY_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9000';

const getAuthHeaders = async () => {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  return token ? { Authorization: `Bearer ${token}` } : undefined;
};

export async function loginAction(formData: FormData) {
  const username = formData.get('username') as string;
  const password = formData.get('password') as string;

  try {
    const res = await fetch(`${API_GATEWAY_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
      throw new Error('Credenciales inválidas');
    }

    const authResponse = await res.json();
    const cookieStore = await cookies();
    cookieStore.set('token', authResponse.token, { httpOnly: true, path: '/' });
    cookieStore.set('session', username, { path: '/' });

  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }

  redirect('/');
}

export async function logoutAction() {
  const cookieStore = await cookies();
  cookieStore.delete('token');
  cookieStore.delete('session');
  redirect('/login');
}

export async function registerAction(formData: FormData) {
  const payload = {
    username: formData.get('username'),
    email: formData.get('email'),
    password: formData.get('password'),
    especialidad: formData.get('especialidad'),
    telefono: formData.get('telefono'),
    direccion: formData.get('direccion'),
    rut: formData.get('rut'),
    estado: 'ACTIVO',
  };

  try {
    const res = await fetch(`${API_GATEWAY_URL}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error('Error al registrar el recurso. Posible nombre duplicado.');
    }

    const authResponse = await res.json();
    const cookieStore = await cookies();
    cookieStore.set('token', authResponse.token, { httpOnly: true, path: '/' });
    cookieStore.set('session', payload.username as string, { path: '/' });
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }

  redirect('/');
}

export async function createProjectAction(formData: FormData) {
  const payload = {
    id: Date.now(), // Simulación de ID auto si no está configurado en el backend
    nombre: formData.get('nombre'),
    estado: formData.get('estado'),
    fechaInicio: formData.get('fechaInicio'),
    fechaFin: formData.get('fechaFin'),
    descripcion: formData.get('descripcion'),
  };

  try {
    const authHeaders = await getAuthHeaders();
    const res = await fetch(`${API_GATEWAY_URL}/proyectos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error('Error al crear el proyecto.');
    }
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }

  revalidatePath('/');
  redirect('/');
}

export async function createTaskAction(projectId: string, formData: FormData) {
  const payload = {
    nombre: formData.get('nombre'),
    descripcion: formData.get('descripcion'),
    estado: formData.get('estado') || 'PENDIENTE',
    idProfesionalAsignado: formData.get('idProfesionalAsignado') ? parseInt(formData.get('idProfesionalAsignado') as string) : null,
    fechaInicio: formData.get('fechaInicio'),
    fechaFin: formData.get('fechaFin'),
    comentarios: formData.get('comentarios'),
  };

  try {
    const authHeaders = await getAuthHeaders();
    const res = await fetch(`${API_GATEWAY_URL}/proyectos/${projectId}/tareas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...authHeaders },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error('Error al crear la tarea.');
    }
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }

  revalidatePath(`/projects/${projectId}`);
  redirect(`/projects/${projectId}`);
}

export async function updateTaskStatusAction(projectId: string, taskId: number, userId: number, formData: FormData) {
  const payload = {
    estado: formData.get('estado'),
    userId: userId.toString(),
  };

  try {
    const authHeaders = await getAuthHeaders();
    const res = await fetch(`${API_GATEWAY_URL}/tareas/${taskId}/estado`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...authHeaders },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const err = await res.text();
      console.error('Backend returned an error:', res.status, err);
      throw new Error(`Error al actualizar el estado de la tarea. Status: ${res.status}`);
    }
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    console.error('Error during updateTaskStatusAction:', error);
    throw error;
  }

  revalidatePath(`/projects/${projectId}`);
  redirect(`/projects/${projectId}`);
}
