import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MetricCard } from '../MetricCard';

describe('MetricCard', () => {
  it('debe renderizar el título, el valor y el subtítulo', () => {
    render(<MetricCard title="Proyectos Activos" value={12} subtitle="Últimos 30 días" />);

    expect(screen.getByText('Proyectos Activos')).toBeInTheDocument();
    expect(screen.getByText('12')).toBeInTheDocument();
    expect(screen.getByText('Últimos 30 días')).toBeInTheDocument();
  });

  it('debe mostrar la indicación de tendencia positiva cuando trend es "up"', () => {
    render(<MetricCard title="Tasa de Completitud" value="85%" trend="up" />);

    expect(screen.getByText('Positivo')).toBeInTheDocument();
  });

  it('debe mostrar la indicación de tendencia negativa cuando trend es "down"', () => {
    render(<MetricCard title="Errores" value="4%" trend="down" />);

    expect(screen.getByText('Atención')).toBeInTheDocument();
  });

  it('no debe mostrar indicación de tendencia ni subtítulo cuando no se proveen', () => {
    render(<MetricCard title="Latencia" value="120ms" />);

    expect(screen.queryByText('Positivo')).not.toBeInTheDocument();
    expect(screen.queryByText('Atención')).not.toBeInTheDocument();
  });
});
