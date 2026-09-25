import os
import boto3

TABLE_NAME = os.environ.get("FOOD_TABLE", "MagicFridgeTable")
REGION = os.environ.get("AWS_REGION", "us-east-1")
DYNAMODB_URL = os.environ.get("DYNAMODB_URL")

def get_dynamodb_table():
    if DYNAMODB_URL:
        dynamodb = boto3.resource("dynamodb", region_name=REGION, endpoint_url=DYNAMODB_URL)
    else:
        dynamodb = boto3.resource("dynamodb", region_name=REGION)
    return dynamodb.Table(TABLE_NAME)
