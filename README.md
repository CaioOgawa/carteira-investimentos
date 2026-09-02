# Carteira de Investimentos

Tracks a stock portfolio: an API that holds positions and pulls quotes from
brapi.dev, plus a Flutter app and a React dashboard on top of it.

## Layout

- `backend/` - Spring Boot 4 API (Java 17, Postgres, JWT auth, scheduled quote updates)
- `mobile/` - Flutter app (iOS/Android/macOS)
- `web/` - React + Vite dashboard (same features as the mobile app, browser-only)

## Running the backend

```
cd backend
cp .env.example .env
# edit .env and add a brapi.dev token - free tier at brapi.dev/dashboard
docker compose up -d
```

That starts Postgres on `5432` and the API on `8080`. Main routes: `/auth/registro`,
`/auth/login`, `/posicoes`, `/posicoes/importar` (CSV upload), `/carteira/resumo`,
`/cotacoes/atualizar`.

The quote update hits brapi.dev's free plan, which allows one ticker per request,
so `BrapiClient` fetches quotes one at a time instead of batching them.

## Running the mobile app

```
cd mobile
flutter pub get
flutter run
```

It points at `http://localhost:8080` (see `lib/api_config.dart`), which works
against the iOS simulator and the macOS build without changes since both share
the Mac's network. A physical device needs the machine's LAN IP instead.

## Running the web dashboard

```
cd web
npm install
npm run dev
```

Opens on `http://localhost:5173`, also pointed at `http://localhost:8080` by
default (override with `VITE_API_BASE_URL`). The backend's CORS config only
allows that origin, so a different dev port needs a matching change in
`SecurityConfig.corsConfigurationSource`.

## CSV import format

```
ativo,quantidade,precoCompra,dataCompra
PETR4,100,32.50,2026-01-15
```

`backend/exemplos/` has a couple of sample files, including one built from a
real BTG Pactual statement.
