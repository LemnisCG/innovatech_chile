'use client';

import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X, Plus } from 'lucide-react';
import { Usuario } from '@/services/api';

interface CreateTaskModalProps {
  usuarios: Usuario[];
  addTaskAction: (formData: FormData) => void;
  isLoggedIn: boolean;
}

export function CreateTaskModal({ usuarios, addTaskAction, isLoggedIn }: CreateTaskModalProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!isLoggedIn) {
    return (
      <div className="text-center p-4 bg-slate-900/50 rounded-xl border border-slate-700">
        <p className="text-slate-400 text-sm mb-3">Debes iniciar sesión para agregar tareas.</p>
        <a href="/login" className="inline-block bg-slate-800 hover:bg-slate-700 text-white px-4 py-2 rounded-lg text-sm transition-colors">
          Ir al Login
        </a>
      </div>
    );
  }

  const modalContent = (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm" style={{ position: 'fixed' }}>
      <div className="bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden flex flex-col max-h-[90vh]">
        <div className="flex items-center justify-between p-6 border-b border-slate-800">
          <h2 className="text-xl font-bold text-white">Crear Nueva Tarea</h2>
          <button
            onClick={() => setIsOpen(false)}
            className="p-2 rounded-lg hover:bg-slate-800 text-slate-400 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 overflow-y-auto">
          <form
            action={(formData) => {
              addTaskAction(formData);
              setIsOpen(false);
            }}
            className="space-y-5"
          >
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Nombre de la Tarea</label>
              <input type="text" name="nombre" required className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow" placeholder="Ej. Diseño de la base de datos" />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Descripción</label>
              <textarea name="descripcion" rows={3} required className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow" placeholder="Detalla los requerimientos de la tarea..."></textarea>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Estado</label>
                <select name="estado" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow">
                  <option value="PENDIENTE">Pendiente</option>
                  <option value="EN_PROGRESO">En Progreso</option>
                  <option value="COMPLETADO">Completado</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Responsable</label>
                <select name="idProfesionalAsignado" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow">
                  <option value="">-- Sin asignar --</option>
                  {usuarios.map(u => (
                    <option key={u.id} value={u.id}>{u.username} ({u.especialidad})</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Fecha de Inicio</label>
                <input type="date" name="fechaInicio" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow style-color-scheme-dark" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1.5">Fecha de Fin</label>
                <input type="date" name="fechaFin" className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow style-color-scheme-dark" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">Comentarios Adicionales</label>
              <textarea name="comentarios" rows={2} className="w-full bg-slate-950 border border-slate-800 rounded-lg px-4 py-2.5 text-white focus:outline-none focus:ring-2 focus:ring-blue-500 transition-shadow" placeholder="Notas, enlaces útiles..."></textarea>
            </div>

            <div className="pt-4 border-t border-slate-800 flex gap-3 justify-end">
              <button
                type="button"
                onClick={() => setIsOpen(false)}
                className="px-5 py-2.5 rounded-lg text-slate-300 hover:bg-slate-800 font-medium transition-colors"
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="px-5 py-2.5 rounded-lg bg-blue-600 hover:bg-blue-500 text-white font-medium shadow-lg shadow-blue-500/20 transition-all"
              >
                Crear Tarea
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );

  return (
    <>
      <button
        onClick={() => setIsOpen(true)}
        className="w-full bg-blue-600 hover:bg-blue-500 text-white font-medium py-3 rounded-xl transition-colors flex items-center justify-center gap-2 shadow-lg shadow-blue-500/20"
      >
        <Plus className="w-5 h-5" />
        Agregar Nueva Tarea
      </button>

      {mounted && isOpen && createPortal(modalContent, document.body)}
    </>
  );
}
