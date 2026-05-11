import { registerAction } from '@/app/actions';

export default function RegisterPage() {
  return (
    <div className="flex justify-center items-center h-full pt-8 pb-16">
      <div className="glass w-full max-w-lg p-8 rounded-2xl relative overflow-hidden group">
        <div className="absolute -inset-0.5 bg-gradient-to-r from-emerald-500 to-blue-600 rounded-2xl opacity-0 group-hover:opacity-10 transition duration-500 blur"></div>
        <div className="relative z-10">
          <h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-blue-400 mb-6">Crear Cuenta (Recurso)</h2>
          <form action={registerAction} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Usuario</label>
                <input type="text" name="username" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Email</label>
                <input type="email" name="email" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Contraseña</label>
                <input type="password" name="password" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">RUT</label>
                <input type="text" name="rut" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Teléfono</label>
                <input type="text" name="telefono" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Especialidad</label>
                <input type="text" name="especialidad" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Dirección</label>
              <input type="text" name="direccion" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-emerald-500" />
            </div>
            
            <button type="submit" className="w-full bg-emerald-600 hover:bg-emerald-500 text-white font-medium py-2 rounded-lg transition-colors mt-4">Registrarse</button>
          </form>
        </div>
      </div>
    </div>
  );
}
