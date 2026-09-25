import os
import pytest
from fastapi.testclient import TestClient
from moto import mock_aws
import boto3

os.environ["AWS_ACCESS_KEY_ID"] = "testing"
os.environ["AWS_SECRET_ACCESS_KEY"] = "testing"
os.environ["AWS_SECURITY_TOKEN"] = "testing"
os.environ["AWS_SESSION_TOKEN"] = "testing"
os.environ["AWS_DEFAULT_REGION"] = "us-east-1"
os.environ["FOOD_TABLE"] = "MagicFridgeTestTable"
os.environ["API_KEY"] = "fake-key"
if "DYNAMODB_URL" in os.environ:
    del os.environ["DYNAMODB_URL"]

from app.main import app
from app.models import RecipeResponse

@pytest.fixture(autouse=True)
def mock_dynamo():
    with mock_aws():
        dynamodb = boto3.resource("dynamodb", region_name="us-east-1")
        dynamodb.create_table(
            TableName="MagicFridgeTestTable",
            KeySchema=[{'AttributeName': 'id', 'KeyType': 'HASH'}],
            AttributeDefinitions=[{'AttributeName': 'id', 'AttributeType': 'S'}],
            BillingMode='PAY_PER_REQUEST'
        )
        yield dynamodb

@pytest.fixture
def client():
    return TestClient(app)


def test_create_food_item(client):
    payload = {
        "nome": "Tomate",
        "categoria": "Legume",
        "quantidade": 5,
        "unidade": "unidades",
        "validade": "2026-12-31"
    }
    response = client.post("/food", json=payload)
    
    assert response.status_code == 201
    data = response.json()
    assert data["nome"] == "Tomate"
    assert "id" in data

def test_read_food_items_empty(client):
    response = client.get("/food")
    assert response.status_code == 200
    assert response.json() == []

def test_read_food_item_success(client):
    create_response = client.post("/food", json={
        "nome": "Cebola", "quantidade": 2, "unidade": "kg", "validade": "2026-10-10"
    })
    item_id = create_response.json()["id"]

    response = client.get(f"/food/{item_id}")
    assert response.status_code == 200
    assert response.json()["nome"] == "Cebola"

def test_read_food_item_not_found(client):
    response = client.get("/food/id-falso")
    assert response.status_code == 404
    assert response.json()["detail"] == "Ingrediente não encontrado"

def test_update_food_item(client):
    create_resp = client.post("/food", json={
        "nome": "Alface", "quantidade": 1, "unidade": "pe", "validade": "2026-10-10"
    })
    item_id = create_resp.json()["id"]

    update_resp = client.patch(f"/food/{item_id}", json={"quantidade": 10})
    assert update_resp.status_code == 200
    assert update_resp.json()["quantidade"] == 10

def test_delete_food_item(client):
    create_resp = client.post("/food", json={
        "nome": "Carne", "quantidade": 1, "unidade": "kg", "validade": "2026-10-10"
    })
    item_id = create_resp.json()["id"]

    delete_resp = client.delete(f"/food/{item_id}")
    assert delete_resp.status_code == 204

    get_resp = client.get(f"/food/{item_id}")
    assert get_resp.status_code == 404

def test_generate_recipe_empty_fridge(client):
    response = client.get("/recipes/generate")
    assert response.status_code == 400
    assert response.json()["detail"] == "Não há ingredientes para gerar uma receita"

def test_generate_recipe_success(client, mocker):
    client.post("/food", json={"nome": "Ovo", "quantidade": 6, "unidade": "unidades", "validade": "2026-10-10"})
    client.post("/food", json={"nome": "Bacon", "quantidade": 200, "unidade": "g", "validade": "2026-10-10"})

    mock_chatgpt_response = {
        "titulo": "Ovos com Bacon",
        "resumo": "Café da manhã dos campeões",
        "ingredientes": ["6 ovos", "200g de bacon"],
        "preparo": ["Frite o bacon", "Jogue os ovos"],
        "observacoes": ["Cuidado com o colesterol"]
    }
    
    mocker.patch(
        "app.main.generate_recipe_from_ingredients", 
        return_value=RecipeResponse(**mock_chatgpt_response)
    )

    response = client.get("/recipes/generate")
    assert response.status_code == 200
    assert response.json()["titulo"] == "Ovos com Bacon"

    empty_resp = client.get("/food")
    assert empty_resp.json() == []
