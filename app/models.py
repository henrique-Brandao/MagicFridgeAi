from typing import Optional
from datetime import date
from pydantic import BaseModel, Field

class FoodItemBase(BaseModel):
    nome: str
    categoria: Optional[str] = None
    quantidade: int = Field(ge=1)  # Validação: >= 1
    unidade: str
    validade: date

class FoodItemCreate(FoodItemBase):
    pass

class FoodItemUpdate(BaseModel):
    nome: Optional[str] = None
    categoria: Optional[str] = None
    quantidade: Optional[int] = Field(default=None, ge=1)
    unidade: Optional[str] = None
    validade: Optional[date] = None

class FoodItemRead(FoodItemBase):
    id: str

class RecipeResponse(BaseModel):
    titulo: str
    resumo: str
    ingredientes: list[str]
    preparo: list[str]
    observacoes: list[str]
