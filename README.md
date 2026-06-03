# MagicFridgeAI

MagicFridgeAI e uma API backend em Java/Spring Boot para cadastrar ingredientes disponiveis em uma geladeira e gerar uma sugestao de receita usando IA. O projeto tambem possui um frontend React/Vite simples para demonstrar o consumo da API.

> Observacao de autoria: neste projeto eu desenvolvi o backend. O frontend foi gerado 100% com IA e foi mantido no repositorio apenas como interface de demonstracao para a API.

## Objetivo do projeto

O projeto nasceu como um exercicio pratico de backend para resolver um problema comum: decidir o que cozinhar com os ingredientes que ja existem em casa. A aplicacao permite registrar alimentos, consultar o inventario e enviar esses dados para a API da OpenAI, retornando uma receita estruturada em JSON.

## Problemas que resolve

- Organiza ingredientes disponiveis em uma base persistente.
- Reduz desperdicio ao sugerir receitas com o que ja esta cadastrado.
- Centraliza quantidade, unidade, categoria e validade dos alimentos.
- Demonstra uma integracao real entre backend, banco de dados relacional e API externa de IA.

## Funcionalidades

- Cadastro de ingredientes.
- Listagem de ingredientes cadastrados.
- Consulta de ingrediente por ID.
- Edicao parcial de ingrediente.
- Remocao de ingrediente.
- Geracao de receita com base nos ingredientes cadastrados.
- Persistencia em PostgreSQL.
- Versionamento de schema com Flyway.
- Validacao de entrada com Bean Validation.
- Ambiente com Docker Compose para subir banco, backend e frontend.

Comportamento importante: quando uma receita e gerada com sucesso, o backend remove todos os ingredientes cadastrados (`repository.deleteAll()`). Isso representa a ideia de que os itens foram consumidos na receita.

## Tecnologias utilizadas

### Backend

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- Spring WebFlux/WebClient
- Bean Validation
- PostgreSQL
- Flyway
- Maven
- Docker

### Frontend de demonstracao

- React 18
- Vite
- TypeScript
- Tailwind CSS
- lucide-react

## Arquitetura e camadas

O backend segue uma separacao simples em camadas:

- `controller`: expoe os endpoints REST (`FoodItemController` e `RecipeController`).
- `service`: concentra regras de negocio e integracao externa (`FoodItemService` e `ChatGptService`).
- `repository`: acesso ao banco com Spring Data JPA (`FoodItemRepository`).
- `model`: entidade JPA persistida no banco (`FoodItem`).
- `dto`: objetos usados na entrada e saida da API (`FoodItemDTO` e `RecipeDTO`).
- `mapper`: conversao entre entidade e DTO (`FoodItemMapper`).
- `config`: configuracao do `WebClient` usado para chamar a API da OpenAI.
- `db/migration`: migrations Flyway para criacao e evolucao da tabela `Food_item`.

Fluxo principal:

1. O cliente cadastra ingredientes pela API REST.
2. O backend valida os dados e persiste no PostgreSQL.
3. Ao solicitar uma receita, o backend busca todos os ingredientes cadastrados.
4. O `ChatGptService` monta um prompt e chama a API da OpenAI.
5. A resposta e convertida para `RecipeDTO` e retornada ao cliente.
6. Apos sucesso na geracao, os ingredientes sao removidos do banco.

## Autenticacao e autorizacao

O projeto nao possui autenticacao ou autorizacao implementada. Os endpoints estao abertos e aceitam requisicoes do frontend local via `@CrossOrigin` para:

- `http://localhost:5173`
- `http://127.0.0.1:5173`

A unica credencial usada pelo sistema e a chave da OpenAI, configurada no backend pela variavel de ambiente `API_KEY`.

## Como rodar localmente

### Requisitos

- Java 17
- Maven Wrapper ja incluido no projeto (`./mvnw`)
- Node.js 18+ para rodar o frontend
- PostgreSQL 16+ ou Docker
- Chave da OpenAI para gerar receitas

### Variaveis de ambiente

Crie um arquivo `.env` na raiz do projeto com base em `.env.example`:

```bash
cp .env.example .env
```

Variaveis principais:

| Variavel | Obrigatoria | Descricao |
| --- | --- | --- |
| `API_KEY` | Sim, para gerar receitas | Chave da OpenAI usada pelo backend. |
| `DATABASE_URL` | Sim | URL JDBC do PostgreSQL. |
| `DATABASE_USERNAME` | Sim | Usuario do banco usado pelo Spring. |
| `DATABASE_PASSWORD` | Sim | Senha do banco usado pelo Spring. |
| `POSTGRES_DB` | Docker | Nome do banco criado no container. |
| `POSTGRES_USER` | Docker | Usuario do PostgreSQL no container. |
| `POSTGRES_PASSWORD` | Docker | Senha do PostgreSQL no container. |
| `POSTGRES_PORT` | Docker | Porta exposta do PostgreSQL. |
| `BACKEND_PORT` | Docker | Porta exposta do backend. |
| `FRONTEND_PORT` | Docker | Porta exposta do frontend. |
| `VITE_API_URL` | Frontend | URL da API consumida pelo React/Vite. |

Exemplo para rodar backend local acessando um PostgreSQL local:

```bash
API_KEY=sua-chave-da-openai
DATABASE_URL=jdbc:postgresql://localhost:5432/magicfridge
DATABASE_USERNAME=magicfridge
DATABASE_PASSWORD=magicfridge
```

## Rodando com Docker Compose

Com o `.env` configurado:

```bash
docker compose up --build
```

Enderecos padrao:

```text
Frontend: http://localhost:5173
Backend:  http://localhost:8080
Postgres: localhost:5432
```

O banco e persistido no volume Docker `postgres-data`.

## Rodando sem Docker

Suba um PostgreSQL local com o banco `magicfridge` ou use apenas o servico de banco do Compose:

```bash
docker compose up db
```

Carregue as variaveis de ambiente e inicie o backend:

```bash
set -a
source .env
set +a
./mvnw spring-boot:run
```

Se estiver usando o banco do Docker, ajuste no `.env`:

```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/magicfridge
```

Para rodar o frontend de demonstracao:

```bash
cd client
npm install
npm run dev
```

O Vite normalmente abre em `http://localhost:5173`.

## Endpoints principais

Base URL local do backend:

```text
http://localhost:8080
```

### Ingredientes

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `POST` | `/food` | Cadastra um ingrediente. |
| `GET` | `/food` | Lista todos os ingredientes. Retorna `204 No Content` quando nao ha itens. |
| `GET` | `/food/{id}` | Busca um ingrediente pelo ID. |
| `PATCH` | `/food/{id}` | Atualiza parcialmente um ingrediente. |
| `DELETE` | `/food/{id}` | Remove um ingrediente. |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/food \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Tomate",
    "categoria": "Vegetal",
    "quantidade": 3,
    "unidade": "unidade",
    "validade": "2026-12-31"
  }'
```

### Receitas

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| `GET` | `/recipes/generate` | Gera uma receita usando os ingredientes cadastrados. |

A geracao de receita depende da variavel `API_KEY`. Em caso de sucesso, a resposta segue o formato:

```json
{
  "titulo": "nome da receita",
  "resumo": "breve descricao",
  "ingredientes": ["ingrediente usado"],
  "preparo": ["passo 1", "passo 2"],
  "observacoes": ["dica ou alerta"]
}
```

## Validacoes atuais

No cadastro/edicao de ingredientes, o DTO valida:

- `nome`: obrigatorio.
- `quantidade`: obrigatoria e com valor minimo 1.
- `unidade`: obrigatoria.
- `validade`: obrigatoria.
- `categoria`: opcional no DTO e no banco apos a segunda migration.

## Testes e verificacao

O projeto possui um teste inicial de contexto do Spring Boot. Para executar:

```bash
./mvnw test
```

Para verificar o build do frontend:

```bash
cd client
npm run build
```

## Aprendizados do projeto

- Criacao de uma API REST com Spring Boot.
- Organizacao de codigo em controller, service, repository, DTO, mapper e model.
- Uso de Spring Data JPA para persistencia.
- Versionamento de banco com Flyway.
- Configuracao de PostgreSQL com Docker Compose.
- Validacao de dados de entrada com Bean Validation.
- Consumo de API externa com `WebClient`.
- Tratamento de resposta de IA em formato JSON.
- Separacao entre backend real e frontend apenas demonstrativo.

## Proximos passos

- Adicionar autenticacao e autorizacao, por exemplo com Spring Security.
- Criar tratamento global de excecoes com respostas padronizadas.
- Melhorar a cobertura de testes para services e controllers.
- Evitar apagar automaticamente todos os ingredientes apos gerar receita, ou tornar esse comportamento configuravel.
- Registrar historico de receitas geradas.
- Adicionar filtros por categoria, validade ou nome.
- Melhorar observabilidade com logs mais estruturados.
- Revisar o frontend manualmente, ja que ele foi gerado por IA e nao foi o foco principal do projeto.
