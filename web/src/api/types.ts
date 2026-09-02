export interface AuthResponse {
  token: string;
  nome: string;
  email: string;
}

export interface Posicao {
  id?: number;
  ativo: string;
  quantidade: number;
  precoCompra: number;
  dataCompra: string; // yyyy-MM-dd
}

export interface ResumoAtivo {
  ativo: string;
  quantidade: number;
  precoCompra: number;
  precoAtual: number;
  valorInvestido: number;
  valorAtual: number;
  ganhoPerda: number;
  percentual: number;
}

export interface ResumoCarteira {
  ativos: ResumoAtivo[];
  valorTotalInvestido: number;
  valorTotalAtual: number;
  ganhoPerdaTotal: number;
  percentualTotal: number;
}
