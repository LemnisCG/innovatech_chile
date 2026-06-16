import { LoginForm } from '@/components/LoginForm';

export default function LoginPage() {
  return (
    <div className="flex justify-center items-center h-full pt-16">
      <div className="glass w-full max-w-md p-8 rounded-2xl relative overflow-hidden group">
        <div className="absolute -inset-0.5 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl opacity-0 group-hover:opacity-10 transition duration-500 blur"></div>
        <div className="relative z-10">
          <h2 className="text-3xl font-bold text-center text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-indigo-400 mb-6">Iniciar Sesión</h2>
          <LoginForm />
        </div>
      </div>
    </div>
  );
}
