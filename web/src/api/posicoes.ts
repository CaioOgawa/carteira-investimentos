import { api } from './client';
import type { Posicao } from './types';

export function listar() {
  return api.get<Posicao[]>('/posicoes').then((r) => r.data);
}

export function criar(posicao: Posicao) {
  return api.post<Posicao>('/posicoes', posicao).then((r) => r.data);
}

export function remover(id: number) {
  return api.delete(`/posicoes/${id}`);
}
