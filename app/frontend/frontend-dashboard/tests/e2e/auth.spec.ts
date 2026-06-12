import { test, expect } from '@playwright/test';
import { E2E_USER, STORAGE_STATE } from './credentials';

// Estos tests requieren el stack completo corriendo (docker-compose up -d --build):
// el login real pasa por resource-service -> api-gateway -> cookie httpOnly.
// El usuario E2E lo crea automáticamente tests/e2e/auth.setup.ts.

test.describe('Flujo de autenticación', () => {
  test('un login correcto redirige al dashboard y muestra la sesión activa', async ({ page }) => {
    await page.goto('/login');

    await page.locator('input[name="username"]').fill(E2E_USER.username);
    await page.locator('input[name="password"]').fill(E2E_USER.password);
    await page.getByRole('button', { name: 'Entrar' }).click();

    await expect(page).toHaveURL('/');
    await expect(page.getByText(`Hola, ${E2E_USER.username}`)).toBeVisible();
    await expect(page.getByRole('button', { name: 'Salir' })).toBeVisible();
  });

  test('un login con credenciales inválidas muestra el error y permanece en el login', async ({ page }) => {
    await page.goto('/login');

    await page.locator('input[name="username"]').fill('usuario-inexistente');
    await page.locator('input[name="password"]').fill('clave-incorrecta');
    await page.getByRole('button', { name: 'Entrar' }).click();

    // Señal positiva (el mensaje de error renderizado por useActionState) en
    // lugar de un assert negativo sobre la URL, que podía pasar "en verde"
    // antes de que la redirección tuviera tiempo de ocurrir.
    await expect(page.getByRole('alert')).toHaveText('Credenciales inválidas');
    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByRole('button', { name: 'Salir' })).not.toBeVisible();
  });
});

test.describe('Cierre de sesión', () => {
  // Parte de la sesión guardada por auth.setup.ts: no repite el login por UI,
  // que ya está cubierto arriba.
  test.use({ storageState: STORAGE_STATE });

  test('logout limpia la sesión y vuelve al login', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByText(`Hola, ${E2E_USER.username}`)).toBeVisible();

    await page.getByRole('button', { name: 'Salir' }).click();

    await expect(page).toHaveURL('/login');
    await expect(page.locator('input[name="username"]')).toBeVisible();
  });
});
