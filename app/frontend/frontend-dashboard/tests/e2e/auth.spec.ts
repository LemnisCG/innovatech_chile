import { test, expect } from '@playwright/test';

// Estos tests requieren el stack completo corriendo (docker-compose up -d --build):
// el login real pasa por resource-service -> api-gateway -> cookie httpOnly.
// Ajusta las credenciales según un usuario existente en tu entorno de pruebas.
const VALID_USER = { username: process.env.E2E_USERNAME || 'testuser', password: process.env.E2E_PASSWORD || 'password123' };

test.describe('Flujo de autenticación', () => {
  test('un login correcto redirige al dashboard y muestra la sesión activa', async ({ page }) => {
    await page.goto('/login');

    await page.locator('input[name="username"]').fill(VALID_USER.username);
    await page.locator('input[name="password"]').fill(VALID_USER.password);
    await page.locator('button[type="submit"]').click();

    await expect(page).toHaveURL('/');
    await expect(page.getByText(`Hola, ${VALID_USER.username}`)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
  });

  test('un login con credenciales inválidas no concede acceso al dashboard', async ({ page }) => {
    await page.goto('/login');

    await page.locator('input[name="username"]').fill('usuario-inexistente');
    await page.locator('input[name="password"]').fill('clave-incorrecta');
    await page.locator('button[type="submit"]').click();

    // `loginAction` lanza un Error que Next.js no muestra hoy en una UI propia
    // (no existe error.tsx ni mensaje en el formulario), así que la señal
    // observable desde afuera es que NUNCA llegamos al dashboard autenticado.
    await expect(page).not.toHaveURL('/');
    await expect(page.getByRole('button', { name: 'Salir' })).not.toBeVisible();
  });

  test('logout limpia la sesión y vuelve al login', async ({ page }) => {
    await page.goto('/login');
    await page.locator('input[name="username"]').fill(VALID_USER.username);
    await page.locator('input[name="password"]').fill(VALID_USER.password);
    await page.locator('button[type="submit"]').click();
    await expect(page).toHaveURL('/');

    await page.getByRole('button', { name: 'Salir' }).click();

    await expect(page).toHaveURL('/login');
    await expect(page.locator('input[name="username"]')).toBeVisible();
  });
});
