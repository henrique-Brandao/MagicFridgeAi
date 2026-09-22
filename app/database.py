import os
import boto3

# O nome da tabela será injetado via variável de ambiente (prática comum em Serverless)
TABLE_NAME = os.environ.get("FOOD_TABLE", "MagicFridgeTable")
REGION = os.environ.get("AWS_REGION", "us-east-1")

def get_dynamodb_table():
    # Para rodar localmente com credenciais falsas se necessário, ou usar as reais da AWS
    dynamodb = boto3.resource("dynamodb", region_name=REGION)
    return dynamodb.Table(TABLE_NAME)
