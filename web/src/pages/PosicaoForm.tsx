import { useState, type FormEvent } from 'react';
import * as posicoesApi from '../api/posicoes';

export function PosicaoForm({
  onSalvo,
  onCancelar,
}: {
  onSalvo: () => void;
  onCancelar: () => void;
}) {
  const [ativo, setAtivo] = useState('');
  const [quantidade, setQuantidade] = useState('');
  const [precoCompra, setPrecoCompra] = useState('');
  const [dataCompra, setDataCompra] = useState(() => new Date().toISOString().slice(0, 10));
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const quantidadeNum = Number(quantidade.replace(',', '.'));
    const precoNum = Number(precoCompra.replace(',', '.'));
    if (!ativo.trim() || !(quantidadeNum > 0) || !(precoNum >= 0)) {
      setErro('Preencha ativo, quantidade e preço válidos');
      return;
    }

    setSalvando(true);
    setErro(null);
    try {
      await posicoesApi.criar({
        ativo: ativo.trim().toUpperCase(),
        quantidade: quantidadeNum,
        precoCompra: precoNum,
        dataCompra,
      });
      onSalvo();
    } catch {
      setErro('Erro ao salvar a posição');
    } finally {
      setSalvando(false);
    }
  }

  return (
    <form className="posicao-form" onSubmit={onSubmit}>
      <label>
        Ativo (ex: PETR4)
        <input
          value={ativo}
          onChange={(e) => setAtivo(e.target.value)}
          style={{ textTransform: 'uppercase' }}
        />
      </label>
      <label>
        Quantidade
        <input
          inputMode="decimal"
          value={quantidade}
          onChange={(e) => setQuantidade(e.target.value)}
        />
      </label>
      <label>
        Preço de compra
        <input
          inputMode="decimal"
          value={precoCompra}
          onChange={(e) => setPrecoCompra(e.target.value)}
        />
      </label>
      <label>
        Data de compra
        <input
          type="date"
          value={dataCompra}
          onChange={(e) => setDataCompra(e.target.value)}
        />
      </label>
      {erro && <p className="erro">{erro}</p>}
      <div className="posicao-form-acoes">
        <button type="button" className="secundario" onClick={onCancelar}>
          Cancelar
        </button>
        <button type="submit" disabled={salvando}>
          {salvando ? 'Salvando...' : 'Salvar'}
        </button>
      </div>
    </form>
  );
}
