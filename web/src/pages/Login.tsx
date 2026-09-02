import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../auth/AuthContext';

export function Login() {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [entrando, setEntrando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setEntrando(true);
    setErro(null);
    try {
      await login(email.trim(), senha);
    } catch (e) {
      const mensagem = axios.isAxiosError(e) ? e.response?.data?.mensagem : null;
      setErro(mensagem ?? 'Falha ao entrar');
    } finally {
      setEntrando(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={onSubmit}>
        <h1>Minha carteira</h1>
        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Senha
          <input
            type="password"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            required
          />
        </label>
        {erro && <p className="erro">{erro}</p>}
        <button type="submit" disabled={entrando}>
          {entrando ? 'Entrando...' : 'Entrar'}
        </button>
        <Link to="/registro" className="link-secundario">
          Criar conta
        </Link>
      </form>
    </div>
  );
}
