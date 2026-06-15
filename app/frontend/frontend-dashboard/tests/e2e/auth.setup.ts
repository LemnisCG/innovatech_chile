import { test as setup, expect } from '@playwright/test';
import { E2E_USER, GATEWAY_URL, STORAGE_STATE } from './credentials';

// Corre una sola vez antes de los proyectos de navegador (ver `dependencies`
// en playwright.config.ts). Deja el entorno listo sin intervención manual:
// 1. registra el usuario E2E contra el gateway (idempotente: si ya existe,
//    el register falla y simplemente seguimos), y
// 2. hace login por la UI real y guarda la sesión (cookies httpOnly incluidas)
//    en STORAGE_STATE para que otros tests partan ya autenticados.
setup('registra el usuario E2E y guarda una sesión autenticada', async ({ page, request }) => {
  await request.post(`${GATEWAY_URL}/api/auth/register`, {
    data: {
      username: E2E_USER.username,
      email: E2E_USER.email,
      password: E2E_USER.password,
      especialidad: E2E_USER.especialidad,
      telefono: E2E_USER.telefono,
      direccion: E2E_USER.direccion,
      rut: E2E_USER.rut,
      estado: 'ACTIVO',
    },
    failOnStatusCode: false, // un 4xx aquí significa "ya registrado"
  });

  await page.goto('/login');
  await page.locator('input[name="username"]').fill(E2E_USER.username);
  await page.locator('input[name="password"]').fill(E2E_USER.password);
  await page.getByRole('button', { name: 'Entrar' }).click();

  await expect(page, 'el usuario E2E debe poder iniciar sesión tras el setup').toHaveURL('/');
  await page.context().storageState({ path: STORAGE_STATE });
});
