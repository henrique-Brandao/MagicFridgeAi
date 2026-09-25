import uuid
from typing import List
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from mangum import Mangum
from boto3.dynamodb.conditions import Key

from app.models import FoodItemCreate, FoodItemRead, FoodItemUpdate, RecipeResponse
from app.database import get_dynamodb_table
from app.services.chatgpt_service import generate_recipe_from_ingredients

app = FastAPI(title="MagicFridgeAI API", description="Serverless Python Backend for AWS Lambda + DynamoDB", version="1.0.0")

# CORS setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5174",
        "http://127.0.0.1:5174",
        "https://magicfridge.vercel.app"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.post("/food", response_model=FoodItemRead, status_code=status.HTTP_201_CREATED)
def create_food_item(food: FoodItemCreate):
    table = get_dynamodb_table()
    
    item_id = str(uuid.uuid4())    
    item_dict = food.model_dump(mode="json")
    item_dict["id"] = item_id
    
    table.put_item(Item=item_dict)
    
    return FoodItemRead(**item_dict)

@app.get("/food", response_model=List[FoodItemRead])
def read_food_items():
    table = get_dynamodb_table()
    response = table.scan()
    items = response.get("Items", [])
    
    return [FoodItemRead(**item) for item in items]

@app.get("/food/{item_id}", response_model=FoodItemRead)
def read_food_item(item_id: str):
    table = get_dynamodb_table()
    response = table.get_item(Key={"id": item_id})
    item = response.get("Item")
    
    if not item:
        raise HTTPException(status_code=404, detail="Ingrediente não encontrado")
    
    return FoodItemRead(**item)

@app.patch("/food/{item_id}", response_model=FoodItemRead)
def update_food_item(item_id: str, food: FoodItemUpdate):
    table = get_dynamodb_table()
    
    existing_response = table.get_item(Key={"id": item_id})
    existing_item = existing_response.get("Item")
    
    if not existing_item:
        raise HTTPException(status_code=404, detail="Ingrediente não encontrado")
    
    update_data = food.model_dump(exclude_unset=True, mode="json")
    for key, value in update_data.items():
        existing_item[key] = value
        
    table.put_item(Item=existing_item)
    return FoodItemRead(**existing_item)

@app.delete("/food/{item_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_food_item(item_id: str):
    table = get_dynamodb_table()
    
    existing_response = table.get_item(Key={"id": item_id})
    if not existing_response.get("Item"):
        raise HTTPException(status_code=404, detail="Ingrediente não encontrado")
    
    table.delete_item(Key={"id": item_id})
    return {"ok": True}

@app.get("/recipes/generate", response_model=RecipeResponse)
def generate_recipe():
    table = get_dynamodb_table()
    response = table.scan()
    raw_items = response.get("Items", [])
    
    if not raw_items:
        raise HTTPException(status_code=400, detail="Não há ingredientes para gerar uma receita")
    
    items = [FoodItemRead(**item) for item in raw_items]
    
    try:
        recipe = generate_recipe_from_ingredients(items)
        
        with table.batch_writer() as batch:
            for item in raw_items:
                batch.delete_item(Key={"id": item["id"]})
        
        return recipe
    except ValueError as e:
        raise HTTPException(status_code=500, detail=str(e))

handler = Mangum(app)
