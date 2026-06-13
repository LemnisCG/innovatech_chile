import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/e2e',
  testMatch: '**/*.spec.ts',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  use: {
    baseURL: process.env.E2E_BASE_URL || 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  // Requiere el stack completo (gateway + servicios + DBs) corriendo vía
  // docker-compose, ya que las páginas hacen fetch real al gateway en :9000.
  // Por eso no se define `webServer`: levantar solo `next dev` no basta.
  projects: [
    // Prepara el entorno: registra el usuario E2E y guarda la sesión
    // autenticada (storageState) que reutilizan los tests de logout.
    { name: 'setup', testMatch: /auth\.setup\.ts/ },
    { name: 'chromium', use: { ...devices['Desktop Chrome'] }, dependencies: ['setup'] },
    { name: 'firefox', use: { ...devices['Desktop Firefox'] }, dependencies: ['setup'] },
  ],
});
