export interface Beneficio {
  id: number;
  nome: string;
  descricao: string;
  valor: number;
  ativo: boolean;
}

export interface BeneficioRequest {
  nome: string;
  descricao: string;
  valor: number;
}

export interface TransferenciaRequest {
  fromId: number;
  toId: number;
  amount: number;
}