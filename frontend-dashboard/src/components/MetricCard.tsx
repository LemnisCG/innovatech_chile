import React, { ReactNode } from 'react';

interface MetricCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon?: ReactNode;
  trend?: 'up' | 'down' | 'neutral';
}

export function MetricCard({ title, value, subtitle, icon, trend }: MetricCardProps) {
  return (
    <div className="glass glass-hover rounded-2xl p-6 flex flex-col justify-between h-full relative overflow-hidden group">
      {/* Background glow effect */}
      <div className="absolute -inset-0.5 bg-gradient-to-r from-blue-500 to-purple-600 rounded-2xl opacity-0 group-hover:opacity-20 transition duration-500 blur"></div>
      
      <div className="relative z-10">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-slate-300 font-medium text-sm tracking-wider uppercase">{title}</h3>
          {icon && <div className="text-blue-400 p-2 bg-blue-500/10 rounded-lg">{icon}</div>}
        </div>
        
        <div className="flex items-end gap-3">
          <span className="text-4xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-white to-slate-400">
            {value}
          </span>
          
          {trend === 'up' && (
            <span className="text-emerald-400 text-sm font-medium mb-1 flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
              </svg>
              Positivo
            </span>
          )}
          {trend === 'down' && (
            <span className="text-rose-400 text-sm font-medium mb-1 flex items-center">
              <svg className="w-4 h-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 17h8m0 0V9m0 8l-8-8-4 4-6-6" />
              </svg>
              Atención
            </span>
          )}
        </div>
        
        {subtitle && (
          <p className="mt-2 text-slate-400 text-sm">{subtitle}</p>
        )}
      </div>
    </div>
  );
}
