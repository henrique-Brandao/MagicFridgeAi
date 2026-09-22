# MagicFridgeAI (Serverless AWS + DynamoDB)

MagicFridgeAI é uma API backend 100% Serverless em Python/FastAPI que usa AWS Lambda e DynamoDB para cadastrar ingredientes em uma geladeira e gerar sugestões de receitas usando Inteligência Artificial.

> Observação de autoria: O backend original era em Java/Spring Boot com PostgreSQL, mas foi totalmente refatorado para Python focado em arquitetura NoSQL (DynamoDB) com custo zero e zero de manutenção na AWS.

## Tecnologias utilizadas (Backend)
- Python 3.11+
- FastAPI (Pydantic para validação)
- Boto3 (AWS SDK para Python)
- AWS DynamoDB (Banco de Dados NoSQL)
- AWS Lambda / API Gateway (Hospedagem)
- Serverless Framework (Deploy)

## Como rodar o deploy na AWS

Este projeto utiliza o **Serverless Framework** para automação da infraestrutura. O arquivo `serverless.yml` criará automaticamente a tabela do DynamoDB e as permissões de rede.

### 1. Pré-requisitos
- Tenha uma conta na AWS.
- Instale a [AWS CLI](https://aws.amazon.com/pt/cli/) e configure suas credenciais (`aws configure`).
- Instale o Node.js e o Serverless Framework via npm:
  ```bash
  npm install -g serverless
  ```

### 2. Configurando o projeto localmente
Entre na raiz do projeto e instale o plugin do Python para o Serverless:
```bash
npm init -y
npm install --save-dev serverless-python-requirements
```

### 3. Deploy
Para subir o projeto na AWS, basta rodar:
```bash
serverless deploy --stage dev
```
O framework exibirá no terminal a URL (Endpoint) do seu API Gateway.

### Variáveis de Ambiente
O backend exige uma variável chamada `API_KEY` da OpenAI. O modo mais simples de passar para o deploy é rodar:
```bash
API_KEY="sk-suachave" serverless deploy
```

---

## Endpoints Principais
A base url será a que o Serverless Framework cuspir no terminal após o deploy.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/food` | Cadastra um ingrediente. |
| `GET` | `/food` | Lista todos os ingredientes. |
| `GET` | `/food/{id}` | Busca um ingrediente pelo ID gerado pelo DynamoDB. |
| `PATCH` | `/food/{id}` | Atualiza parcialmente um ingrediente. |
| `DELETE` | `/food/{id}` | Remove um ingrediente. |
| `GET` | `/recipes/generate` | Gera receita consumindo a API da OpenAI. Os ingredientes são removidos do banco em seguida. |
