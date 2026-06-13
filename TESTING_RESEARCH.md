# Investigación: Testing Frontend y E2E para Innovatech

**Fecha:** 2026-06-05  
**Contexto:** Next.js 16 + App Router + Server Components + Server Actions

---

## 1. Estado Actual del Frontend

### Configuración Existente
- **Framework:** Next.js 16.2.4 (última versión)
- **React:** 19.2.4 (últimas características)
- **Arquitectura:** App Router con Server Components
- **Server Actions:** Implementadas para autenticación y CRUD
- **Styling:** Tailwind CSS 4 + PostCSS
- **Visualizaciones:** Recharts
- **Autenticación:** JWT en cookies httpOnly + Bearer token

### Herramientas de Testing Actuales
❌ **NINGUNA configurada**
- No hay Jest, Vitest, Playwright, Cypress, etc.
- No existen archivos `.test.ts`, `.test.tsx`, `.spec.ts`
- No hay configuración de testing en `package.json`

### Dependencias Actuales (sin testing)
```json
{
  "dependencies": ["next", "react", "react-dom", "lucide-react", "recharts"],
  "devDependencies": ["@tailwindcss/postcss", "tailwindcss", "eslint", "typescript", "@types/*"]
}
```

---

## 2. Análisis de la Arquitectura del Frontend

### Componentes y Patrones Utilizados

#### **Server Components & Server Actions** (La parte crítica)
```typescript
// actions.ts - Operaciones servidor
'use server'
- loginAction()
- logoutAction()
- registerAction()
- createProjectAction()
- Acceso directo a cookies, headers, revalidatePath
```

#### **Data Fetching Functions** (Cliente/Servidor)
```typescript
// api.ts - Llamadas al gateway
- fetchProductivity()
- fetchSystemHealth()
- fetchProjects()
- fetchProjectById()
- fetchUsuarios()
- Con autenticación JWT
```

#### **Componentes React** (Parcialmente Server, parcialmente Cliente)
```
CreateTaskModal.tsx    - Modal interactivo (Client Component)
MetricCard.tsx         - Card de métricas (Server/Client)
Navigation.tsx         - Navegación (Server Component)
```

#### **Layout y Páginas**
```
/                      - Home (Server Component)
/login                 - Login (Server Component con formulario)
/register              - Registro (Server Component con formulario)
/projects              - Listado de proyectos (Server Component)
/projects/create       - Crear proyecto (Server Component con form)
/projects/[id]         - Detalle de proyecto (Server Component dinámico)
```

---

## 3. Recomendación de Stack de Testing

### **Arquitectura de Testing Recomendada**

```
┌─────────────────────────────────────────────────────┐
│           TESTING PYRAMID PARA NEXT.JS              │
├─────────────────────────────────────────────────────┤
│ E2E (Playwright)              [5-10% de tests]      │
│  ├─ Flujos completos de usuario                     │
│  ├─ Autenticación real                              │
│  └─ Integración completa                            │
├─────────────────────────────────────────────────────┤
│ Integration Tests (Vitest + MSW)  [20-30% tests]   │
│  ├─ Server Actions con mocks de API                 │
│  ├─ Data fetching + estado                          │
│  └─ Forms y validación                              │
├─────────────────────────────────────────────────────┤
│ Unit Tests (Vitest)           [60-70% de tests]     │
│  ├─ Funciones utilitarias                           │
│  ├─ Lógica pura                                      │
│  ├─ Componentes aislados                            │
│  └─ Conversiones de datos                           │
└─────────────────────────────────────────────────────┘
```

---

## 4. Herramientas Recomendadas (Análisis Comparativo)

### **4.1 Unit Testing: Vitest vs Jest**

| Aspecto | Vitest | Jest |
|---------|--------|------|
| **Velocidad** | ✅ Extremadamente rápido | ❌ Más lento |
| **Next.js 16** | ✅ Soporte nativo | ⚠️ Requiere config |
| **Server Components** | ✅ Soporte completo | ❌ Problemas conocidos |
| **ESM** | ✅ Soporte nativo | ❌ Requiere transpilación |
| **HMR en modo watch** | ✅ Sí | ❌ No |
| **Configuración** | ✅ Mínima requerida | ❌ Más compleja |
| **Ecosistema** | ✅ Moderno | ✅ Mayor |

**→ RECOMENDACIÓN: Vitest** (mejor para Next.js 16 moderno)

---

### **4.2 Testing de React Components: React Testing Library**

```typescript
// Tu caso de uso:
- Components "use client" (CreateTaskModal, MetricCard)
- Componentes interactivos
- Formularios (login, register, create project)

// Vitest + @testing-library/react
- ✅ Testing de User Interactions
- ✅ Accesibilidad
- ✅ No requiere simuladores DOM complejos
- ✅ Mejor que Enzyme o Snapshot Testing
```

**→ RECOMENDACIÓN: Vitest + @testing-library/react**

---

### **4.3 Server Actions & Data Fetching: Integration Testing**

```typescript
// Tu caso específico:
- Server Actions (loginAction, createProjectAction)
- Fetch requests con JWT
- Cookie management

// Opciones:
1. Vitest + MSW (Mock Service Worker) - Testing funcional
2. Vitest + node-fetch mocks - Testing simple
3. Vitest con testcontainers - Testing con DB real (pesado)
```

**→ RECOMENDACIÓN: Vitest + MSW (por flexibilidad)**

**MSW es superior porque:**
- ✅ Intercepta requests a nivel de red
- ✅ Funciona con fetch, axios, etc.
- ✅ Simula respuestas realistas
- ✅ Fácil de mantener handlers
- ✅ Excelente documentación

---

### **4.4 E2E Testing: Playwright vs Cypress**

| Aspecto | Playwright | Cypress |
|---------|-----------|---------|
| **Navegadores** | ✅ Chrome, Firefox, Webkit | ⚠️ Chrome, Firefox, Edge |
| **Cross-browser** | ✅ Excelente | ⚠️ Limitado |
| **Velocidad** | ✅ Más rápido | ❌ Más lento |
| **API** | ✅ Mejor documentada | ✅ Intuitiva |
| **Mobile testing** | ✅ Soportado | ❌ No soportado |
| **Parallel execution** | ✅ Nativo | ⚠️ Requiere worker |
| **Next.js 16** | ✅ Perfecto | ✅ Bueno |
| **Server Actions** | ✅ Puede testear completo | ✅ Puede testear completo |
| **Comunidad** | ✅ Muy activa | ✅ Mayor |

**→ RECOMENDACIÓN: Playwright** (mejor para tu arquitectura moderna)

**Playwright es superior porque:**
- ✅ Mejor parallelización
- ✅ Testing mobile nativo
- ✅ Múltiples navegadores
- ✅ API más consistente
- ✅ Mejor para testing de API real en background

---

## 5. Stack Final Recomendado

### **Instalación Propuesta**

```json
{
  "devDependencies": {
    "vitest": "^1.6.0",
    "@vitest/ui": "^1.6.0",
    "@testing-library/react": "^15.0.0",
    "@testing-library/jest-dom": "^6.1.4",
    "jsdom": "^24.0.0",
    "@testing-library/user-event": "^14.5.1",
    "msw": "^2.0.0",
    "@playwright/test": "^1.40.0",
    "@types/node": "^20",
    "typescript": "^5"
  }
}
```

### **Estructura de Directorio**

```
src/
├── app/
│   ├── __tests__/
│   │   ├── login.test.tsx           (login flow)
│   │   ├── register.test.tsx        (register flow)
│   │   └── actions.server.test.ts   (Server Actions)
│   ├── actions.ts
│   ├── layout.tsx
│   └── page.tsx
├── components/
│   ├── __tests__/
│   │   ├── CreateTaskModal.test.tsx
│   │   ├── MetricCard.test.tsx
│   │   └── Navigation.test.tsx
│   └── ...
├── services/
│   ├── __tests__/
│   │   ├── api.test.ts              (data fetching)
│   │   └── api.integration.test.ts  (con MSW)
│   └── api.ts
└── ...

tests/
├── e2e/
│   ├── auth.spec.ts                 (login/logout)
│   ├── projects.spec.ts             (CRUD proyectos)
│   ├── fixtures/                    (shared setup)
│   └── utils/                       (helpers)
├── fixtures/
│   ├── mocks.ts                     (MSW handlers)
│   └── test-data.ts
├── integration/
│   └── server-actions.test.ts       (Server Actions)
└── setup.ts                         (setup global)
```

---

## 6. Comparativa Detallada por Tipo de Test

### **6.1 Unit Tests (Vitest + React Testing Library)**

```typescript
// Ejemplo: MetricCard.test.tsx
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import MetricCard from '@/components/MetricCard';

describe('MetricCard', () => {
  it('debe renderizar la métrica correctamente', () => {
    render(<MetricCard title="Velocidad" value={95} unit="%" />);
    expect(screen.getByText('Velocidad')).toBeInTheDocument();
    expect(screen.getByText('95%')).toBeInTheDocument();
  });

  it('debe manejar valores nulos', () => {
    render(<MetricCard title="Métrica" value={null} />);
    expect(screen.getByText('N/A')).toBeInTheDocument();
  });
});
```

**Casos para Unit Tests:**
- ✅ Componentes sin dependencias externas
- ✅ Lógica de conversión/transformación
- ✅ Validaciones de datos
- ✅ Componentes "use client" simples

---

### **6.2 Integration Tests (Vitest + MSW)**

```typescript
// Ejemplo: api.integration.test.ts
import { describe, it, expect, beforeAll, afterEach, afterAll } from 'vitest';
import { server } from '@/tests/fixtures/mocks';
import { fetchProjects, fetchProductivity } from '@/services/api';

describe('API Integration Tests', () => {
  beforeAll(() => server.listen());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.close());

  it('fetchProjects debe traer proyectos', async () => {
    const projects = await fetchProjects();
    expect(projects).toHaveLength(2);
    expect(projects[0]).toHaveProperty('nombre');
  });

  it('fetchProductivity debe manejar errores', async () => {
    // Server retorna 500
    server.use(http.get('*/kpis/productivity', () => HttpResponse.error()));
    const data = await fetchProductivity();
    expect(data).toEqual({ leadTimePromedioDias: 0, ... });
  });
});
```

**Casos para Integration Tests:**
- ✅ Funciones de API fetch + datos
- ✅ Server Actions con mocks
- ✅ Formularios con validación backend
- ✅ Error handling y edge cases

---

### **6.3 Server Actions Testing (Vitest)**

```typescript
// Ejemplo: actions.server.test.ts
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { loginAction } from '@/app/actions';

// Mock next/navigation y next/headers
vi.mock('next/navigation', () => ({
  redirect: vi.fn(),
}));
vi.mock('next/headers', () => ({
  cookies: vi.fn(),
}));

describe('Server Actions', () => {
  it('loginAction debe setear cookie y redirigir', async () => {
    const formData = new FormData();
    formData.set('username', 'testuser');
    formData.set('password', 'password123');

    // Mock fetch
    global.fetch = vi.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ token: 'jwt-token' }),
      })
    );

    await expect(loginAction(formData)).rejects.toThrow('NEXT_REDIRECT');
    // Verificar que el fetch fue llamado correctamente
  });
});
```

**Casos para Server Actions Testing:**
- ✅ Validación de inputs
- ✅ Errores de API
- ✅ Cookie management
- ✅ Redirecciones

---

### **6.4 E2E Tests (Playwright)**

```typescript
// Ejemplo: tests/e2e/auth.spec.ts
import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test('debe permitir login correcto', async ({ page }) => {
    await page.goto('http://localhost:3000/login');
    
    // Llenar formulario
    await page.fill('[name="username"]', 'testuser');
    await page.fill('[name="password"]', 'password123');
    await page.click('button[type="submit"]');

    // Esperar redirección y verificar
    await expect(page).toHaveURL('http://localhost:3000/');
    await expect(page.locator('[data-testid="welcome"]')).toContainText('testuser');
  });

  test('debe mostrar error con credenciales inválidas', async ({ page }) => {
    await page.goto('http://localhost:3000/login');
    
    await page.fill('[name="username"]', 'invalid');
    await page.fill('[name="password"]', 'wrong');
    await page.click('button[type="submit"]');

    // Verificar mensaje de error
    await expect(page.locator('[data-testid="error"]')).toBeVisible();
  });
});

test.describe('Projects CRUD', () => {
  test.beforeEach(async ({ page }) => {
    // Setup: Login
    await page.goto('http://localhost:3000/login');
    await page.fill('[name="username"]', 'testuser');
    await page.fill('[name="password"]', 'password123');
    await page.click('button[type="submit"]');
  });

  test('debe crear proyecto', async ({ page }) => {
    await page.goto('http://localhost:3000/projects/create');
    
    await page.fill('[name="nombre"]', 'Nuevo Proyecto');
    await page.fill('[name="descripcion"]', 'Descripción test');
    await page.click('button[type="submit"]');

    await expect(page).toHaveURL(/\/projects\/\d+/);
    await expect(page.locator('h1')).toContainText('Nuevo Proyecto');
  });

  test('debe editar proyecto', async ({ page }) => {
    // Navegar a proyecto existente y editarlo
    await page.goto('http://localhost:3000/projects/1');
    await page.click('[data-testid="edit-btn"]');
    // ... resto del test
  });
});
```

**Casos para E2E Tests:**
- ✅ Flujo completo de autenticación
- ✅ Crear/editar/eliminar proyectos
- ✅ Navegación entre páginas
- ✅ Integración real con el servidor
- ✅ Validaciones visuales

---

## 7. Configuración Recomendada

### **7.1 vitest.config.ts**

```typescript
import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './tests/setup.ts',
    include: ['**/*.test.{ts,tsx}'],
    exclude: ['node_modules', 'dist'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*.{ts,tsx}'],
      exclude: [
        'src/**/*.test.{ts,tsx}',
        'src/**/__tests__/**',
      ],
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
});
```

### **7.2 playwright.config.ts**

```typescript
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
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },
  webServer: {
    command: 'npm run dev',
    reuseExistingServer: !process.env.CI,
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
    // Descomentar para mobile testing
    // {
    //   name: 'Mobile Chrome',
    //   use: { ...devices['Pixel 5'] },
    // },
  ],
});
```

### **7.3 tests/setup.ts (Global Setup)**

```typescript
import '@testing-library/jest-dom';
import { server } from '@/tests/fixtures/mocks';

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

// Mock next/navigation
vi.mock('next/navigation', () => ({
  redirect: vi.fn(() => {
    throw new Error('NEXT_REDIRECT');
  }),
  useRouter: () => ({
    push: vi.fn(),
    prefetch: vi.fn(),
  }),
  useSearchParams: () => new URLSearchParams(),
  usePathname: () => '',
}));

// Mock next/headers
vi.mock('next/headers', () => ({
  cookies: vi.fn(() => ({
    get: vi.fn(),
    set: vi.fn(),
    delete: vi.fn(),
  })),
}));
```

### **7.4 tests/fixtures/mocks.ts (MSW Handlers)**

```typescript
import { http, HttpResponse, setupServer } from 'msw';

export const handlers = [
  // Login
  http.post('http://localhost:9000/api/auth/login', () => {
    return HttpResponse.json({ token: 'jwt-mock-token' });
  }),

  // Projects
  http.get('http://localhost:9000/proyectos', () => {
    return HttpResponse.json([
      { id: 1, nombre: 'Project 1', estado: 'PENDIENTE' },
      { id: 2, nombre: 'Project 2', estado: 'EN_PROGRESO' },
    ]);
  }),

  // Analytics
  http.get('http://localhost:9000/api/analytics/kpis/productivity', () => {
    return HttpResponse.json({
      leadTimePromedioDias: 5,
      tasaCompletitud: 85,
      totalProyectosActivos: 3,
    });
  }),
];

export const server = setupServer(...handlers);
```

### **7.5 package.json Scripts**

```json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "eslint",
    "test": "vitest",
    "test:watch": "vitest --watch",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "test:e2e": "playwright test",
    "test:e2e:ui": "playwright test --ui",
    "test:e2e:debug": "playwright test --debug",
    "test:all": "npm run test && npm run test:e2e"
  }
}
```

---

## 8. Roadmap de Implementación Recomendado

### **Fase 1: Setup Inicial (1-2 horas)**
1. Instalar Vitest + complementos
2. Crear vitest.config.ts
3. Crear tests/setup.ts y MSW handlers
4. Primer test unitario (ejemplo: MetricCard.test.tsx)

### **Fase 2: Unit Tests (2-3 días)**
1. Componentes "use client" (CreateTaskModal, MetricCard, Navigation)
2. Funciones de utilidad
3. Conversiones de datos
4. **Target:** 60-70% cobertura

### **Fase 3: Integration Tests (2-3 días)**
1. API fetching (fetchProjects, fetchProductivity, etc.)
2. Server Actions (loginAction, registerAction, createProjectAction)
3. Formularios con validación
4. **Target:** 20-30% cobertura

### **Fase 4: E2E Tests (3-5 días)**
1. Setup Playwright
2. Flujos de autenticación (login, logout, register)
3. CRUD de proyectos
4. Navegación y flujos complejos
5. **Target:** 5-10% cobertura (tests principales)

### **Fase 5: CI/CD Integration (1-2 días)**
1. GitHub Actions para ejecutar tests
2. Coverage reports
3. Bloquear PRs sin tests pasados

---

## 9. Decisiones de Diseño y Justificación

| Decisión | Justificación |
|----------|--------------|
| **Vitest sobre Jest** | Mejor soporte para ESM, Server Components, más rápido, menor config |
| **MSW para mocks** | Intercepta a nivel de red, realista, mantenible, industria standard |
| **Playwright sobre Cypress** | Mejor parallelización, mobile testing, múltiples navegadores, API consistente |
| **React Testing Library** | Testing desde perspectiva del usuario, más mantenible que snapshot testing |
| **Cobertura 70%+** | Equilibrio entre tiempo invertido y confianza en el código |
| **E2E enfocado en happy paths** | Los tests E2E son lentos, mejor enfocarse en flujos críticos |

---

## 10. Posibles Desafíos y Soluciones

| Desafío | Solución |
|---------|----------|
| **Testear Server Actions** | Usar vi.mock() para next/navigation y next/headers |
| **Testear componentes con cookies** | Usar MSW para simular respuestas + mock de cookies |
| **E2E con autenticación real** | Crear usuario test en BD durante setup, usar mismo flujo |
| **Tests lentos** | Parallelizar Playwright, usar fixtures para reutilizar estado |
| **Mock API compleja** | Usar MSW con patrones, fixtures con test-data.ts |
| **Mantener tests sincronizados** | Testing library fuerza esto naturalmente (user-centric) |

---

## 11. Comparativa Final de Herramientas

### **Testing Unitario e Integración**

```
┌─────────────────┬──────────┬─────────┬──────────┬────────────┐
│ Aspecto         │  Vitest  │  Jest   │  Mocha   │  Jasmine   │
├─────────────────┼──────────┼─────────┼──────────┼────────────┤
│ Velocidad       │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐   │
│ Next.js 16      │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐   │
│ Configuración   │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐   │
│ Comunidad       │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐⭐  │ ⭐⭐⭐    │
│ Documentación   │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐    │ ⭐⭐⭐    │
│ Ecosistema      │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐    │ ⭐⭐⭐    │
│ Server Actions  │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │ ⭐⭐    │ ⭐⭐    │
└─────────────────┴──────────┴─────────┴──────────┴────────────┘
```

### **Testing E2E**

```
┌──────────────────┬────────────┬──────────┬──────────┐
│ Aspecto          │ Playwright │ Cypress  │ Webdriver│
├──────────────────┼────────────┼──────────┼──────────┤
│ Velocidad        │ ⭐⭐⭐⭐⭐  │ ⭐⭐⭐⭐  │ ⭐⭐⭐   │
│ Parallelización  │ ⭐⭐⭐⭐⭐  │ ⭐⭐⭐   │ ⭐⭐⭐   │
│ Multi-navegador  │ ⭐⭐⭐⭐⭐  │ ⭐⭐⭐⭐  │ ⭐⭐⭐⭐⭐ │
│ Mobile Testing   │ ⭐⭐⭐⭐⭐  │ ❌       │ ⭐⭐⭐⭐  │
│ API Testing      │ ⭐⭐⭐⭐⭐  │ ⭐⭐     │ ⭐⭐⭐   │
│ Debugging        │ ⭐⭐⭐⭐   │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │
│ Comunidad        │ ⭐⭐⭐⭐   │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐⭐  │
│ Documentación    │ ⭐⭐⭐⭐⭐  │ ⭐⭐⭐⭐⭐ │ ⭐⭐⭐   │
│ Next.js 16       │ ⭐⭐⭐⭐⭐  │ ⭐⭐⭐⭐  │ ⭐⭐⭐   │
└──────────────────┴────────────┴──────────┴──────────┘
```

---

## 12. Conclusión

### **Stack Recomendado Definitivo**

Para tu proyecto **Innovatech** con Next.js 16 + Server Components + Server Actions:

```
┌──────────────────────────────────────────────┐
│   STACK TESTING RECOMENDADO                  │
├──────────────────────────────────────────────┤
│ ✅ Unit + Integration:  Vitest               │
│ ✅ React Component:     @testing-library/react│
│ ✅ API Mocking:         MSW (Mock Service Worker)
│ ✅ E2E:                 Playwright           │
│ ✅ Server Actions:      Vitest + vi.mock()   │
└──────────────────────────────────────────────┘
```

### **Razones Principales**

1. **Vitest:** 
   - Velocidad nativa para Next.js 16
   - Soporte perfecto para ESM y Server Components
   - Configuración mínima
   - HMR en modo watch

2. **React Testing Library:**
   - Testing user-centric (lo que importa)
   - Evita snapshot testing (frágil)
   - Accesibilidad integrada

3. **MSW:**
   - Intercepción a nivel de red (realista)
   - Funciona con cualquier cliente HTTP
   - Perfecta para Server Actions
   - Industria standard

4. **Playwright:**
   - Mejor para parallelización
   - Testing mobile nativo
   - API moderna y consistente
   - Perfect para tu arquitectura moderna

### **Próximos Pasos**
1. Revisar esta investigación
2. Decidir si proceder con la implementación del stack
3. Comenzar con Phase 1 (Setup inicial)

---

**Documento preparado:** 2026-06-05  
**Versión:** 1.0
