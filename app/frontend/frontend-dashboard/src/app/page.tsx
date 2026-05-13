import { fetchProductivity, fetchSystemHealth, fetchProjects } from '@/services/api';
import { MetricCard } from '@/components/MetricCard';
import { Activity, Clock, CheckCircle, AlertTriangle, Server, BarChart3 } from 'lucide-react';

export default async function DashboardPage() {
  // Fetch data on the server side (Next.js Server Components)
  const [productivity, systemHealth, projects] = await Promise.all([
    fetchProductivity(),
    fetchSystemHealth(),
    fetchProjects()
  ]);

  return (
    <div className="min-h-screen p-8 md:p-12 lg:p-24 max-w-7xl mx-auto">
      <header className="mb-12 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-4xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 via-purple-400 to-indigo-400 mb-2">
            Innovatech Analytics
          </h1>
          <p className="text-slate-400 text-lg">Monitoreo ROLAP & Salud del Sistema en Tiempo Real</p>
        </div>
        <div className="flex items-center gap-3 glass px-5 py-3 rounded-full">
          <div className="w-3 h-3 rounded-full bg-emerald-400 animate-pulse"></div>
          <span className="text-sm font-medium text-slate-300">Sistema Operativo</span>
        </div>
      </header>

      <main className="space-y-12">
        {/* Sección Productividad */}
        <section>
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-blue-500/20 rounded-lg text-blue-400">
              <BarChart3 className="w-6 h-6" />
            </div>
            <h2 className="text-2xl font-semibold text-slate-200">Gestión de Proyectos</h2>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <MetricCard
              title="Proyectos Activos"
              value={productivity.totalProyectosActivos}
              subtitle="Total de proyectos en curso"
              icon={<Activity className="w-6 h-6" />}
              trend="neutral"
            />
            <MetricCard
              title="Lead Time Promedio"
              value={`${productivity.leadTimePromedioDias.toFixed(1)} d`}
              subtitle="Días promedio para completar tareas"
              icon={<Clock className="w-6 h-6" />}
              trend="up"
            />
            <MetricCard
              title="Tasa de Completitud"
              value={`${productivity.tasaCompletitud.toFixed(1)}%`}
              subtitle="Porcentaje de tareas finalizadas"
              icon={<CheckCircle className="w-6 h-6" />}
              trend="up"
            />
          </div>
        </section>

        {/* Sección Salud Técnica */}
        <section>
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-purple-500/20 rounded-lg text-purple-400">
              <Server className="w-6 h-6" />
            </div>
            <h2 className="text-2xl font-semibold text-slate-200">Salud Técnica (API Gateway)</h2>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <MetricCard
              title="Latencia Promedio"
              value={`${systemHealth.latenciaPromedioMs.toFixed(0)} ms`}
              subtitle="Tiempo de respuesta de los microservicios"
              icon={<Activity className="w-6 h-6" />}
              trend={systemHealth.latenciaPromedioMs > 500 ? 'down' : 'up'}
            />
            <MetricCard
              title="Tasa de Errores (5xx)"
              value={`${systemHealth.tasaErroresPorcentaje.toFixed(2)}%`}
              subtitle="Peticiones fallidas vs exitosas"
              icon={<AlertTriangle className="w-6 h-6" />}
              trend={systemHealth.tasaErroresPorcentaje > 1 ? 'down' : 'up'}
            />
          </div>
        </section>

        {/* Sección Proyectos en Vivo */}
        <section className="mt-12">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-pink-500/20 rounded-lg text-pink-400">
                <Activity className="w-6 h-6" />
              </div>
              <h2 className="text-2xl font-semibold text-slate-200">Proyectos Recientes (En Vivo)</h2>
            </div>
          </div>
          
          <div className="glass rounded-2xl overflow-hidden border border-slate-800">
            <table className="w-full text-left text-sm text-slate-400">
              <thead className="bg-slate-900/50 text-xs uppercase text-slate-300 border-b border-slate-800">
                <tr>
                  <th className="px-6 py-4">Nombre del Proyecto</th>
                  <th className="px-6 py-4">Estado</th>
                  <th className="px-6 py-4">Fecha Inicio</th>
                  <th className="px-6 py-4 text-right">Acciones</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800">
                {projects.length === 0 ? (
                  <tr>
                    <td colSpan={4} className="px-6 py-8 text-center">No hay proyectos registrados aún.</td>
                  </tr>
                ) : (
                  projects.map((p) => (
                    <tr key={p.id} className="hover:bg-slate-800/30 transition-colors">
                      <td className="px-6 py-4 font-medium text-slate-200">{p.nombre}</td>
                      <td className="px-6 py-4">
                        <span className="bg-slate-800 text-slate-300 py-1 px-3 rounded-full text-xs">
                          {p.estado}
                        </span>
                      </td>
                      <td className="px-6 py-4">{p.fechaInicio}</td>
                      <td className="px-6 py-4 text-right">
                        <a href={`/projects/${p.id}`} className="text-blue-400 hover:text-blue-300 font-medium">Ver detalles &rarr;</a>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
      
      <footer className="mt-20 border-t border-slate-800 pt-8 text-center text-slate-500 text-sm">
        &copy; {new Date().getFullYear()} Innovatech Chile. Arquitectura Microservicios + Next.js App Router.
      </footer>
    </div>
  );
}
