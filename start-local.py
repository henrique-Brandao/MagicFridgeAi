import os
import boto3

DYNAMODB_URL = os.environ.get("DYNAMODB_URL", "http://dynamodb-local:8000")
TABLE_NAME = os.environ.get("FOOD_TABLE", "MagicFridgeTable")
REGION = os.environ.get("AWS_REGION", "us-east-1")

dynamodb = boto3.client(
    "dynamodb",
    region_name=REGION,
    endpoint_url=DYNAMODB_URL,
    aws_access_key_id="dummy",
    aws_secret_access_key="dummy"
)

try:
    print(f"Verificando se a tabela '{TABLE_NAME}' existe em {DYNAMODB_URL}...")
    dynamodb.describe_table(TableName=TABLE_NAME)
    print("A tabela já existe. Iniciando o servidor...")
except dynamodb.exceptions.ResourceNotFoundException:
    print(f"Tabela '{TABLE_NAME}' não encontrada. Criando agora...")
    dynamodb.create_table(
        TableName=TABLE_NAME,
        KeySchema=[
            {'AttributeName': 'id', 'KeyType': 'HASH'}
        ],
        AttributeDefinitions=[
            {'AttributeName': 'id', 'AttributeType': 'S'}
        ],
        BillingMode='PAY_PER_REQUEST'
    )
    print("Tabela criada com sucesso! Iniciando o servidor...")
except Exception as e:
    print(f"Atenção: não foi possível conectar ao DynamoDB Local. Erro: {e}")

os.execvp("uvicorn", ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080", "--reload"])
