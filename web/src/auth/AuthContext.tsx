import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { getToken, setOnUnauthorized, setToken } from '../api/client';
import * as authApi from '../api/auth';

interface UsuarioLogado {
  nome: string;
  email: string;
}

interface AuthContextValue {
  token: string | null;
  usuario: UsuarioLogado | null;
  login: (email: string, senha: string) => Promise<void>;
  registrar: (nome: string, email: string, senha: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(getToken());
  const [usuario, setUsuario] = useState<UsuarioLogado | null>(() => {
    const nome = localStorage.getItem('nome');
    const email = localStorage.getItem('email');
    return nome && email ? { nome, email } : null;
  });

  useEffect(() => {
    setOnUnauthorized(() => logout());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  function persistir(resposta: { token: string; nome: string; email: string }) {
    setToken(resposta.token);
    localStorage.setItem('nome', resposta.nome);
    localStorage.setItem('email', resposta.email);
    setTokenState(resposta.token);
    setUsuario({ nome: resposta.nome, email: resposta.email });
  }

  async function login(email: string, senha: string) {
    const resposta = await authApi.login(email, senha);
    persistir(resposta);
  }

  async function registrar(nome: string, email: string, senha: string) {
    const resposta = await authApi.registrar(nome, email, senha);
    persistir(resposta);
  }

  function logout() {
    setToken(null);
    localStorage.removeItem('nome');
    localStorage.removeItem('email');
    setTokenState(null);
    setUsuario(null);
  }

  const value = useMemo(
    () => ({ token, usuario, login, registrar, logout }),
    [token, usuario],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth precisa estar dentro de um AuthProvider');
  return ctx;
}
