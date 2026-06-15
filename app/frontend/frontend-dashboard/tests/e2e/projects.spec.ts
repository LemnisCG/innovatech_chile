import { test, expect } from '@playwright/test';
import { E2E_USER, STORAGE_STATE } from './credentials';

// Requiere el stack completo corriendo (docker-compose up -d --build).
// El usuario E2E ya existe gracias a auth.setup.ts, por lo que aparece en el
// selector de responsable al crear una tarea.

test.describe('Flujo de proyectos y tareas', () => {
  test.use({ storageState: STORAGE_STATE });

  test('crea un proyecto, le agrega una tarea y la asigna al usuario E2E', async ({ page }) => {
    const projectName = `Proyecto E2E ${Date.now()}`;
    const taskName = 'Tarea de integración E2E';

    // Paso 1: crear proyecto
    await page.goto('/projects/create');
    await page.locator('input[name="nombre"]').fill(projectName);
    await page.locator('textarea[name="descripcion"]').fill('Descripción creada por test E2E.');
    await page.locator('select[name="estado"]').selectOption('EN_PROGRESO');
    await page.locator('input[name="fechaInicio"]').fill('2026-01-01');
    await page.locator('input[name="fechaFin"]').fill('2026-12-31');
    await page.getByRole('button', { name: 'Guardar Proyecto' }).click();

    // createProjectAction redirige al dashboard
    await expect(page).toHaveURL('/');

    // Paso 2: abrir el detalle del proyecto recién creado
    const row = page.getByRole('row').filter({ hasText: projectName });
    await expect(row).toBeVisible();
    await row.getByRole('link', { name: /Ver detalles/ }).click();

    await expect(page).toHaveURL(/\/projects\/\d+/);
    await expect(page.getByRole('heading', { level: 1 })).toHaveText(projectName);

    // Paso 3: abrir el modal de nueva tarea
    await page.getByRole('button', { name: 'Agregar Nueva Tarea' }).click();
    await expect(page.getByRole('heading', { name: 'Crear Nueva Tarea' })).toBeVisible();

    // Paso 4: llenar el formulario y asignar al usuario E2E como responsable
    await page.locator('#task-nombre').fill(taskName);
    await page.locator('#task-descripcion').fill('Tarea creada automáticamente por el test E2E.');
    await page.locator('#task-estado').selectOption('PENDIENTE');
    await page.locator('#task-responsable').selectOption({ label: `${E2E_USER.username} (${E2E_USER.especialidad})` });
    await page.locator('#task-fecha-inicio').fill('2026-01-15');
    await page.locator('#task-fecha-fin').fill('2026-06-30');
    await page.getByRole('button', { name: 'Crear Tarea' }).click();

    // createTaskAction redirige de vuelta al detalle del proyecto
    await expect(page).toHaveURL(/\/projects\/\d+/);

    // Paso 5: verificar que la tarea aparece con el responsable correcto
    await expect(page.getByText(taskName)).toBeVisible();
    await expect(page.getByText(`Responsable: ${E2E_USER.username}`)).toBeVisible();
  });
});
