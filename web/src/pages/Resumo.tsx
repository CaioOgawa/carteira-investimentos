import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Bar,
  BarChart,
  Cell,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { useAuth } from '../auth/AuthContext';
import * as resumoApi from '../api/resumo';
import type { ResumoCarteira } from '../api/types';

const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

const paleta = [
  '#3f51b5',
  '#009688',
  '#ff9800',
  '#9c27b0',
  '#e91e63',
  '#607d8b',
  '#ffc107',
  '#00bcd4',
];

export function Resumo() {
  const { token } = useAuth();
  const [resumo, setResumo] = useState<ResumoCarteira | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  const carregar = useCallback(() => {
    setErro(null);
    resumoApi
      .buscarResumo()
      .then(setResumo)
      .catch(() => setErro('Não foi possível carregar o resumo.'));
  }, []);

  useEffect(() => {
    setResumo(null);
    carregar();
  }, [token, carregar]);

  return (
    <div className="pagina">
      <header className="topo">
        <h1>Resumo da carteira</h1>
        <Link to="/">Voltar</Link>
      </header>

      {erro && (
        <div className="aviso">
          <p>{erro}</p>
          <button onClick={carregar}>Tentar novamente</button>
        </div>
      )}

      {!erro && resumo === null && <p>Carregando...</p>}

      {!erro && resumo !== null && resumo.ativos.length === 0 && (
        <p>Nenhuma posição para resumir ainda.</p>
      )}

      {!erro && resumo !== null && resumo.ativos.length > 0 && (
        <>
          <div className="card-resumo">
            <div className="linha-resumo">
              <span>Valor investido</span>
              <strong>{moeda.format(resumo.valorTotalInvestido)}</strong>
            </div>
            <div className="linha-resumo">
              <span>Valor atual</span>
              <strong>{moeda.format(resumo.valorTotalAtual)}</strong>
            </div>
            <hr />
            <div className="linha-resumo">
              <span>Ganho/perda</span>
              <strong className={resumo.ganhoPerdaTotal >= 0 ? 'positivo' : 'negativo'}>
                {moeda.format(resumo.ganhoPerdaTotal)} (
                {resumo.ganhoPerdaTotal >= 0 ? '+' : ''}
                {resumo.percentualTotal.toFixed(2)}%)
              </strong>
            </div>
          </div>

          <h2>Composição por ativo</h2>
          <div className="grafico-composicao">
            <ResponsiveContainer width={200} height={200}>
              <PieChart>
                <Pie
                  data={resumo.ativos}
                  dataKey="valorAtual"
                  nameKey="ativo"
                  innerRadius={45}
                  outerRadius={90}
                  paddingAngle={2}
                >
                  {resumo.ativos.map((_, i) => (
                    <Cell key={i} fill={paleta[i % paleta.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => moeda.format(Number(value))} />
              </PieChart>
            </ResponsiveContainer>
            <ul className="legenda">
              {resumo.ativos.map((a, i) => (
                <li key={a.ativo}>
                  <span
                    className="marcador"
                    style={{ background: paleta[i % paleta.length] }}
                  />
                  {a.ativo}
                  <span className="percentual">
                    {((a.valorAtual / resumo.valorTotalAtual) * 100).toFixed(1)}%
                  </span>
                </li>
              ))}
            </ul>
          </div>

          <h2>Ganho/perda por ativo (%)</h2>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={resumo.ativos}>
              <XAxis dataKey="ativo" fontSize={12} />
              <YAxis fontSize={12} />
              <Tooltip formatter={(value) => `${Number(value).toFixed(2)}%`} />
              <Bar dataKey="percentual" radius={[4, 4, 0, 0]}>
                {resumo.ativos.map((a, i) => (
                  <Cell key={i} fill={a.percentual >= 0 ? '#2e7d32' : '#c62828'} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </>
      )}
    </div>
  );
}
