import Link from 'next/link';
import { cookies } from 'next/headers';
import { logoutAction } from '@/app/actions';

export async function Navigation() {
  const cookieStore = await cookies();
  const session = cookieStore.get('session');

  return (
    <nav className="glass sticky top-0 z-50 mb-8 border-b-0 border-slate-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center gap-8">
            <Link href="/" className="text-xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400">
              Innovatech
            </Link>
            {session && (
              <div className="hidden md:block">
                <div className="flex items-baseline space-x-4">
                  <Link href="/" className="text-slate-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Dashboard</Link>
                  <Link href="/projects/create" className="text-slate-300 hover:text-white px-3 py-2 rounded-md text-sm font-medium">Nuevo Proyecto</Link>
                </div>
              </div>
            )}
          </div>
          <div>
            {session ? (
              <div className="flex items-center gap-4">
                <span className="text-sm text-slate-400">Hola, {session.value}</span>
                <form action={logoutAction}>
                  <button type="submit" className="text-sm text-rose-400 hover:text-rose-300 font-medium">Salir</button>
                </form>
              </div>
            ) : (
              <div className="flex items-center gap-4">
                <Link href="/login" className="text-sm text-slate-300 hover:text-white font-medium">Ingresar</Link>
                <Link href="/register" className="text-sm bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-lg font-medium transition-colors">Registro</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
