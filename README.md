# MagicFridgeAi

Aplicacao com backend Spring Boot e frontend React/Vite para cadastrar ingredientes da geladeira e gerar sugestoes de receita com IA.

## Requisitos

- Java 17
- Node.js 18+
- PostgreSQL 16+ ou Docker
- Uma chave da OpenAI para usar a geracao de receitas

## Variaveis de ambiente

O projeto usa PostgreSQL. Para Docker Compose, copie `.env.example` para `.env` e preencha `API_KEY`.

Variaveis principais:

```bash
export API_KEY='sua-chave-da-openai'
export DATABASE_URL='jdbc:postgresql://localhost:5432/magicfridge'
export DATABASE_USERNAME='magicfridge'
export DATABASE_PASSWORD='magicfridge'
```

No frontend, a API padrao e `http://localhost:8080`. Para trocar:

```bash
export VITE_API_URL='http://localhost:8080'
```

## Rodando com Docker

Crie seu `.env` a partir do exemplo e preencha a chave da OpenAI:

```bash
cp .env.example .env
```

Suba PostgreSQL, backend e frontend:

```bash
docker compose up --build
```

Enderecos padrao:

```text
Frontend: http://localhost:5173
Backend:  http://localhost:8080
Postgres: localhost:5432
```

O banco PostgreSQL fica persistido em um volume Docker chamado `postgres-data`.

## Rodando localmente sem Docker

Primeiro suba um PostgreSQL local e crie um banco chamado `magicfridge`, ou use apenas o servico `db` do Compose:

```bash
docker compose up db
```

Backend local:

```bash
set -a
source .env
set +a
./mvnw spring-boot:run
```

Se estiver usando o Postgres do Docker, ajuste a URL no `.env` para `localhost`:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/magicfridge
```

Frontend local:

```bash
cd client
npm install
npm run dev
```

Depois abra o endereco mostrado pelo Vite, normalmente `http://localhost:5173`.

## Verificacao

```bash
./mvnw test
cd client
npm run build
```
