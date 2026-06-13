'use client';

import { useActionState } from 'react';
import { loginAction, LoginState } from '@/app/actions';

const initialState: LoginState = {};

export function LoginForm() {
  const [state, formAction, pending] = useActionState(loginAction, initialState);

  return (
    <form action={formAction} className="space-y-4">
      <div>
        <label htmlFor="login-username" className="block text-sm font-medium text-slate-300 mb-1">Usuario</label>
        <input type="text" id="login-username" name="username" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      <div>
        <label htmlFor="login-password" className="block text-sm font-medium text-slate-300 mb-1">Contraseña</label>
        <input type="password" id="login-password" name="password" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      {state.error && (
        <p role="alert" aria-live="polite" className="text-sm text-rose-400 text-center">
          {state.error}
        </p>
      )}
      <button type="submit" disabled={pending} className="w-full bg-blue-600 hover:bg-blue-500 disabled:opacity-60 text-white font-medium py-2 rounded-lg transition-colors">
        {pending ? 'Entrando…' : 'Entrar'}
      </button>
    </form>
  );
}
