import axios from 'axios';

// localhost funciona em dev porque o backend também roda local. Pra apontar
// pra outro lugar, defina VITE_API_BASE_URL num .env.
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

export const api = axios.create({ baseURL: apiBaseUrl });

let currentToken: string | null = localStorage.getItem('token');

export function setToken(token: string | null) {
  currentToken = token;
  if (token) {
    localStorage.setItem('token', token);
  } else {
    localStorage.removeItem('token');
  }
}

export function getToken() {
  return currentToken;
}

api.interceptors.request.use((config) => {
  if (currentToken) {
    config.headers.Authorization = `Bearer ${currentToken}`;
  }
  return config;
});

let onUnauthorized: (() => void) | null = null;

export function setOnUnauthorized(handler: () => void) {
  onUnauthorized = handler;
}

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      onUnauthorized?.();
    }
    return Promise.reject(error);
  },
);
