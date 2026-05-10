import { createProjectAction } from '@/app/actions';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export default async function CreateProjectPage() {
  const cookieStore = await cookies();
  const session = cookieStore.get('session');

  if (!session) {
    redirect('/login');
  }

  return (
    <div className="flex justify-center items-center h-full pt-8 pb-16">
      <div className="glass w-full max-w-lg p-8 rounded-2xl relative overflow-hidden group">
        <div className="absolute -inset-0.5 bg-gradient-to-r from-purple-500 to-pink-600 rounded-2xl opacity-0 group-hover:opacity-10 transition duration-500 blur"></div>
        <div className="relative z-10">
          <h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-purple-400 to-pink-400 mb-6">Crear Nuevo Proyecto</h2>
          <form action={createProjectAction} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Nombre del Proyecto</label>
              <input type="text" name="nombre" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-purple-500" />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Descripción</label>
              <textarea name="descripcion" rows={3} required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-purple-500"></textarea>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Estado</label>
                <select name="estado" className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-purple-500">
                  <option value="PLANIFICACION">Planificación</option>
                  <option value="EN_PROGRESO">En Progreso</option>
                  <option value="COMPLETADO">Completado</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Fecha de Inicio</label>
                <input type="date" name="fechaInicio" required className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-purple-500 [color-scheme:dark]" />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Fecha de Fin (Estimada)</label>
              <input type="date" name="fechaFin" className="w-full bg-slate-900/50 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:ring-2 focus:ring-purple-500 [color-scheme:dark]" />
            </div>
            
            <button type="submit" className="w-full bg-purple-600 hover:bg-purple-500 text-white font-medium py-2 rounded-lg transition-colors mt-4">Guardar Proyecto</button>
          </form>
        </div>
      </div>
    </div>
  );
}
