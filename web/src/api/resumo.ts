import { api } from './client';
import type { ResumoCarteira } from './types';

export function buscarResumo() {
  return api.get<ResumoCarteira>('/carteira/resumo').then((r) => r.data);
}
