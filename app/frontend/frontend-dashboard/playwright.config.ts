import { defineConfig, devices } from '@playwright/test';

// Ubuntu 26.04 no está soportado por los binarios bundleados de Playwright.
// Usar PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH para apuntar a un Chrome del sistema
// (e.g. google-chrome-stable). Firefox requiere el binario parchado de Playwright
// y no puede reemplazarse con el del sistema, por eso está deshabilitado hasta que
// Playwright soporte Ubuntu 26.04.
const chromiumLaunchOptions = process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH
  ? {
      launchOptions: {
        executablePath: process.env.PLAYWRIGHT_CHROMIUM_EXECUTABLE_PATH,
        args: ['--no-sandbox', '--disable-setuid-sandbox'],
      },
    }
  : {};

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
    { name: 'setup', testMatch: /auth\.setup\.ts/, use: { ...chromiumLaunchOptions } },
    { name: 'chromium', use: { ...devices['Desktop Chrome'], ...chromiumLaunchOptions }, dependencies: ['setup'] },
  ],
});
