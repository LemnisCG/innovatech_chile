import { fetchProjectById, fetchUsuarios } from '@/services/api';
import { createTaskAction, updateTaskStatusAction } from '@/app/actions';
import { notFound } from 'next/navigation';
import { cookies } from 'next/headers';
import { Calendar, CheckCircle2 } from 'lucide-react';
import { CreateTaskModal } from '@/components/CreateTaskModal';

export default async function ProjectDetailsPage({ params }: { params: { id: string } }) {
  const cookieStore = await cookies();
  const session = cookieStore.get('session');
  
  // We need to await params in Next.js 15
  const { id } = await params;
  const username = session?.value;
  
  const [project, usuarios] = await Promise.all([
    fetchProjectById(id),
    fetchUsuarios()
  ]);

  const loggedInUser = username ? usuarios.find(u => u.username === username) : null;

  if (!project) {
    notFound();
  }

  // Bind the projectId to the Server Action
  const addTask = createTaskAction.bind(null, id);

  return (
    <div className="min-h-screen p-8 max-w-6xl mx-auto space-y-8">
      {/* Cabecera del Proyecto */}
      <div className="glass p-8 rounded-2xl relative overflow-hidden">
        <div className="absolute top-0 right-0 w-64 h-64 bg-blue-500/10 rounded-full blur-3xl -mr-10 -mt-10"></div>
        <div className="relative z-10 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h1 className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400 mb-2">
              {project.nombre}
            </h1>
            <p className="text-slate-400">{project.descripcion}</p>
          </div>
          <div className="flex gap-4">
            <div className="px-4 py-2 bg-slate-800/50 rounded-xl border border-slate-700/50">
              <span className="text-xs text-slate-500 block">Estado</span>
              <span className="font-semibold text-slate-200">{project.estado}</span>
            </div>
            <div className="px-4 py-2 bg-slate-800/50 rounded-xl border border-slate-700/50">
              <span className="text-xs text-slate-500 block">Fecha Inicio</span>
              <span className="font-semibold text-slate-200">{project.fechaInicio}</span>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Lista de Tareas */}
        <div className="lg:col-span-2 space-y-6">
          <h2 className="text-xl font-semibold text-slate-200 flex items-center gap-2">
            <CheckCircle2 className="text-blue-400 w-5 h-5" /> Tareas del Proyecto
          </h2>
          
          <div className="space-y-4">
            {!project.tareasDelProyecto || project.tareasDelProyecto.length === 0 ? (
              <div className="glass p-8 text-center rounded-2xl text-slate-400">
                Aún no hay tareas en este proyecto.
              </div>
            ) : (
              project.tareasDelProyecto.map(tarea => (
                <div key={tarea.id} className="glass p-5 rounded-2xl flex items-start justify-between">
                  <div>
                    <h3 className="font-medium text-slate-200 text-lg mb-1">{tarea.nombre}</h3>
                    <p className="text-slate-400 text-sm">{tarea.descripcion}</p>
                    
                    {tarea.idProfesionalAsignado && (
                      <p className="text-slate-500 text-xs mt-2">
                        Responsable: {usuarios.find(u => u.id === tarea.idProfesionalAsignado)?.username || 'Desconocido'}
                      </p>
                    )}
                  </div>
                  
                  {loggedInUser && tarea.idProfesionalAsignado === loggedInUser.id ? (
                    <form action={updateTaskStatusAction.bind(null, project.id.toString(), tarea.id)} className="flex items-center gap-2">
                      <select 
                        name="estado" 
                        defaultValue={tarea.estado}
                        className="text-xs px-3 py-1 bg-slate-900 text-slate-300 rounded-full border border-slate-700 focus:outline-none focus:border-blue-500"
                      >
                        <option value="PENDIENTE">PENDIENTE</option>
                        <option value="EN_PROGRESO">EN_PROGRESO</option>
                        <option value="COMPLETADO">COMPLETADO</option>
                      </select>
                      <button type="submit" className="text-xs px-3 py-1 bg-blue-600 hover:bg-blue-500 text-white rounded-full transition-colors">
                        Actualizar
                      </button>
                    </form>
                  ) : (
                    <span className="text-xs px-3 py-1 bg-slate-800 text-slate-300 rounded-full border border-slate-700">
                      {tarea.estado}
                    </span>
                  )}
                </div>
              ))
            )}
          </div>
        </div>

        {/* Acciones del Proyecto */}
        <div>
          <div className="glass p-6 rounded-2xl sticky top-24">
            <h2 className="text-xl font-semibold text-slate-200 mb-6">Acciones</h2>
            <CreateTaskModal 
              usuarios={usuarios} 
              addTaskAction={addTask} 
              isLoggedIn={!!session} 
            />
          </div>
        </div>
      </div>
    </div>
  );
}
