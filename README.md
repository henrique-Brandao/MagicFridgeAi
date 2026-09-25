# MagicFridge AI

Um projeto de estudo criado para praticar o desenvolvimento em cloud (AWS), a integração de APIs com modelos de Inteligência Artificial (OpenAI) e práticas de DevOps (Docker e CI/CD) utilizando o ecossistema Python (FastAPI + Serverless). 

A aplicação é um gerenciador simples de ingredientes de geladeira que se conecta à API da OpenAI para sugerir receitas com base no que está disponível.

## Tecnologias Utilizadas

- **Backend:** Python 3.11, FastAPI
- **Banco de Dados:** Amazon DynamoDB
- **Frontend:** React, Vite
- **Infraestrutura:** Docker (desenvolvimento) e AWS Lambda via Serverless Framework (produção)
- **Testes:** Pytest, Moto (para simular o banco localmente)
- **CI/CD:** Github Actions

## Estrutura do Projeto

Optei por uma estrutura plana (Flat Architecture), que é o padrão recomendado para aplicações em AWS Lambda, visando manter o código simples e focado no domínio da aplicação.

## Como rodar localmente

Utilizamos o Docker Compose para subir a API, o banco de dados local e o frontend sem precisar conectar na nuvem.

1. Clone o repositório.
2. Copie o arquivo `.env.example` para `.env` e adicione sua chave do ChatGPT:
   ```bash
   cp .env.example .env
   ```
3. Suba os containers:
   ```bash
   docker-compose up --build
   ```

- A API e a documentação (Swagger) ficarão disponíveis na porta `8080`.
- O Frontend ficará disponível na porta `5174`.
- O DynamoDB local rodará na porta `8000`.

## Rodando os Testes

Para testar a aplicação sem gastar créditos na AWS ou na OpenAI:

```bash
# Crie e ative seu ambiente virtual (se ainda não tiver)
python3 -m venv .venv
source .venv/bin/activate

# Instale as dependências normais e as de teste
pip install -r requirements.txt -r requirements-dev.txt

# Execute o pytest
pytest tests/
```

## Deploy

O deploy na AWS é feito automaticamente pelo Github Actions toda vez que um commit é feito na branch `main`. É necessário configurar as chaves da AWS e da OpenAI nas *Secrets* do repositório para a pipeline funcionar.
