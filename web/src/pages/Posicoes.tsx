import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import * as posicoesApi from '../api/posicoes';
import type { Posicao } from '../api/types';
import { PosicaoForm } from './PosicaoForm';

const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const data = new Intl.DateTimeFormat('pt-BR', { timeZone: 'UTC' });

export function Posicoes() {
  const { usuario, logout, token } = useAuth();
  const [posicoes, setPosicoes] = useState<Posicao[] | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [mostrarForm, setMostrarForm] = useState(false);

  const carregar = useCallback(() => {
    setErro(null);
    posicoesApi
      .listar()
      .then(setPosicoes)
      .catch(() => setErro('Não foi possível carregar as posições.'));
  }, []);

  // Refaz a busca sempre que o token mudar (login/logout/troca de conta).
  useEffect(() => {
    setPosicoes(null);
    carregar();
  }, [token, carregar]);

  async function remover(id: number) {
    await posicoesApi.remover(id);
    carregar();
  }

  return (
    <div className="pagina">
      <header className="topo">
        <h1>Minha carteira</h1>
        <div className="topo-acoes">
          <span className="usuario">{usuario?.nome}</span>
          <Link to="/resumo">Resumo</Link>
          <button className="link-botao" onClick={logout}>
            Sair
          </button>
        </div>
      </header>

      {erro && (
        <div className="aviso">
          <p>{erro}</p>
          <button onClick={carregar}>Tentar novamente</button>
        </div>
      )}

      {!erro && posicoes === null && <p>Carregando...</p>}

      {!erro && posicoes !== null && posicoes.length === 0 && !mostrarForm && (
        <p>Nenhuma posição cadastrada ainda.</p>
      )}

      {!erro && posicoes !== null && posicoes.length > 0 && (
        <table className="tabela-posicoes">
          <thead>
            <tr>
              <th>Ativo</th>
              <th>Quantidade</th>
              <th>Preço de compra</th>
              <th>Data</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {posicoes.map((p) => (
              <tr key={p.id}>
                <td>{p.ativo}</td>
                <td>{p.quantidade}</td>
                <td>{moeda.format(p.precoCompra)}</td>
                <td>{data.format(new Date(p.dataCompra))}</td>
                <td>
                  <button className="link-botao" onClick={() => remover(p.id!)}>
                    Remover
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {mostrarForm ? (
        <PosicaoForm
          onSalvo={() => {
            setMostrarForm(false);
            carregar();
          }}
          onCancelar={() => setMostrarForm(false)}
        />
      ) : (
        <button className="botao-adicionar" onClick={() => setMostrarForm(true)}>
          + Adicionar posição
        </button>
      )}
    </div>
  );
}
