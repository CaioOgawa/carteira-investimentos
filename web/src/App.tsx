import { Navigate, Route, BrowserRouter, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './auth/AuthContext';
import { Login } from './pages/Login';
import { Registro } from './pages/Registro';
import { Posicoes } from './pages/Posicoes';
import { Resumo } from './pages/Resumo';

function RotaPrivada({ children }: { children: React.ReactNode }) {
  const { token } = useAuth();
  return token ? <>{children}</> : <Navigate to="/login" replace />;
}

function RotaPublica({ children }: { children: React.ReactNode }) {
  const { token } = useAuth();
  return token ? <Navigate to="/" replace /> : <>{children}</>;
}

function AppRoutes() {
  return (
    <Routes>
      <Route
        path="/login"
        element={
          <RotaPublica>
            <Login />
          </RotaPublica>
        }
      />
      <Route
        path="/registro"
        element={
          <RotaPublica>
            <Registro />
          </RotaPublica>
        }
      />
      <Route
        path="/"
        element={
          <RotaPrivada>
            <Posicoes />
          </RotaPrivada>
        }
      />
      <Route
        path="/resumo"
        element={
          <RotaPrivada>
            <Resumo />
          </RotaPrivada>
        }
      />
    </Routes>
  );
}

export function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  );
}
