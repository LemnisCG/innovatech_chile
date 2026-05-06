export interface ProductivityKpi {
  leadTimePromedioDias: number;
  tasaCompletitud: number;
  totalProyectosActivos: number;
}

export interface SystemHealthKpi {
  latenciaPromedioMs: number;
  tasaErroresPorcentaje: number;
}

export interface Tarea {
  id: number;
  nombre: string;
  descripcion: string;
  estado: string;
}

export interface Proyecto {
  id: number;
  nombre: string;
  descripcion: string;
  estado: string;
  fechaInicio: string;
  fechaFin: string;
  comentarios: string;
  tareasDelProyecto: Tarea[];
}

const API_GATEWAY_URL = 'http://localhost:8080';
const ANALYTICS_URL = `${API_GATEWAY_URL}/api/analytics/kpis`;

export const fetchProductivity = async (): Promise<ProductivityKpi> => {
  try {
    const res = await fetch(`${ANALYTICS_URL}/productivity`, { cache: 'no-store' });
    if (!res.ok) throw new Error('Error fetching productivity KPI');
    return res.json();
  } catch (error) {
    console.error(error);
    return { leadTimePromedioDias: 0, tasaCompletitud: 0, totalProyectosActivos: 0 };
  }
};

export const fetchSystemHealth = async (): Promise<SystemHealthKpi> => {
  try {
    const res = await fetch(`${ANALYTICS_URL}/system-health`, { cache: 'no-store' });
    if (!res.ok) throw new Error('Error fetching system health KPI');
    return res.json();
  } catch (error) {
    console.error(error);
    return { latenciaPromedioMs: 0, tasaErroresPorcentaje: 0 };
  }
};

export const fetchProjects = async (): Promise<Proyecto[]> => {
  try {
    const res = await fetch(`${API_GATEWAY_URL}/proyectos`, { cache: 'no-store' });
    if (!res.ok) throw new Error('Error fetching projects');
    return res.json();
  } catch (error) {
    console.error(error);
    return [];
  }
};

export const fetchProjectById = async (id: string): Promise<Proyecto | null> => {
  try {
    const res = await fetch(`${API_GATEWAY_URL}/proyectos/${id}`, { cache: 'no-store' });
    if (!res.ok) return null;
    return res.json();
  } catch (error) {
    console.error(error);
    return null;
  }
};
