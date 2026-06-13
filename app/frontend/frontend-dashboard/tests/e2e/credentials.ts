import path from 'path';

// Usuario dedicado a E2E. El proyecto `setup` de Playwright lo registra vía
// /api/auth/register antes de correr la suite, así los tests no dependen de
// datos pre-cargados a mano en la base de datos.
export const E2E_USER = {
  username: process.env.E2E_USERNAME || 'e2e.tester',
  password: process.env.E2E_PASSWORD || 'E2e-Password-123',
  email: 'e2e.tester@innovatech.cl',
  especialidad: 'QA Automation',
  telefono: '+56900000000',
  direccion: 'Pipeline CI s/n',
  rut: '99.999.999-9',
};

export const GATEWAY_URL = process.env.E2E_GATEWAY_URL || 'http://localhost:9000';

// Sesión autenticada que guarda auth.setup.ts; los tests que no prueban el
// login en sí la reutilizan vía `test.use({ storageState: STORAGE_STATE })`.
export const STORAGE_STATE = path.join(__dirname, '.auth', 'user.json');
