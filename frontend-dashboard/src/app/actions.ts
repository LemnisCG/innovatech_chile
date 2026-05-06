'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { revalidatePath } from 'next/cache';

const API_GATEWAY_URL = 'http://localhost:8080';

export async function loginAction(formData: FormData) {
  const username = formData.get('username') as string;
  const password = formData.get('password') as string;

  try {
    const res = await fetch(`${API_GATEWAY_URL}/usuarios/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
      throw new Error('Credenciales inválidas');
    }

    const user = await res.json();
    
    // Simular sesión guardando el username en una cookie HTTP-only
    const cookieStore = await cookies();
    cookieStore.set('session', user.username, { secure: true, httpOnly: true });

  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }
  
  redirect('/');
}

export async function logoutAction() {
  const cookieStore = await cookies();
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
    active: true,
  };

  try {
    const res = await fetch(`${API_GATEWAY_URL}/usuarios`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      throw new Error('Error al registrar el recurso. Posible nombre duplicado.');
    }
  } catch (error) {
    if (error instanceof Error && error.message === 'NEXT_REDIRECT') throw error;
    throw new Error('Error de conexión con el servidor');
  }
  
  redirect('/login');
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
    const res = await fetch(`${API_GATEWAY_URL}/proyectos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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
  };

  try {
    const res = await fetch(`${API_GATEWAY_URL}/proyectos/${projectId}/tareas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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
