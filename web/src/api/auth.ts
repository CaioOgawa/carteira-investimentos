import { api } from './client';
import type { AuthResponse } from './types';

export function registrar(nome: string, email: string, senha: string) {
  return api
    .post<AuthResponse>('/auth/registro', { nome, email, senha })
    .then((r) => r.data);
}

export function login(email: string, senha: string) {
  return api
    .post<AuthResponse>('/auth/login', { email, senha })
    .then((r) => r.data);
}
