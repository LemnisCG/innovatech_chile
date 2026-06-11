import { defineConfig } from 'vitest/config';
import path from 'path';

export default defineConfig({
  test: {
    // Simula el ambiente del navegador (necesario para renderizar componentes)
    environment: 'jsdom',
    // Archivo que se ejecuta ANTES de cada test (para configurar matchers, MSW, etc.)
    setupFiles: ['./tests/setup.ts'],
    // Hace que no tengas que importar describe/it/expect en cada test
    globals: true,
    // Limpia las llamadas registradas de todos los mocks antes de cada test;
    // sin esto, asserts como `not.toHaveBeenCalled()` sobre revalidatePath
    // verían llamadas acumuladas de tests anteriores.
    clearMocks: true,
    // tests/e2e usa @playwright/test, no Vitest — si no se excluye, Vitest
    // intenta correr esos *.spec.ts como propios y falla al no reconocer su `test`.
    exclude: ['node_modules', 'tests/e2e/**'],
    coverage: {
      provider: 'v8',
      // Solo el código con lógica testeable; las páginas RSC y layouts se
      // cubren con Playwright, no con unit tests.
      include: ['src/services/**', 'src/app/actions.ts', 'src/components/**'],
      reporter: ['text', 'html'],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
