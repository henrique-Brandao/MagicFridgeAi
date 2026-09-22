import os
import json
from openai import OpenAI
from app.models import FoodItemRead, RecipeResponse

# Configura o client do OpenAI
client = OpenAI(api_key=os.environ.get("API_KEY"))

def generate_recipe_from_ingredients(ingredients: list[FoodItemRead]) -> RecipeResponse:
    if not ingredients:
        raise ValueError("Não há ingredientes cadastrados para gerar uma receita.")

    # Constrói a lista de ingredientes em string para o prompt
    ingredientes_str = "\n".join([f"- {item.quantidade} {item.unidade} de {item.nome}" for item in ingredients])
    
    system_prompt = """
    Você é um chef de cozinha criativo. 
    Vou te passar uma lista de ingredientes disponíveis. 
    Sua tarefa é sugerir uma receita deliciosa utilizando o máximo desses ingredientes possíveis (não precisa usar todos, e você pode assumir que tenho sal, pimenta, óleo e água em casa).
    Você deve responder OBRIGATORIAMENTE em formato JSON válido seguindo a estrutura abaixo:
    {
      "titulo": "Nome da Receita",
      "resumo": "Breve descrição",
      "ingredientes": ["ingrediente 1", "ingrediente 2"],
      "preparo": ["passo 1", "passo 2"],
      "observacoes": ["dica ou alerta"]
    }
    Não retorne nada além do JSON.
    """

    user_prompt = f"Meus ingredientes são:\n{ingredientes_str}\n\nGere uma receita no formato JSON solicitado."

    response = client.chat.completions.create(
        model="gpt-4o-mini", 
        response_format={ "type": "json_object" },
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt}
        ],
        temperature=0.7
    )
    
    # Parse do JSON retornado pela OpenAI
    content = response.choices[0].message.content
    try:
        data = json.loads(content)
        return RecipeResponse(**data)
    except Exception as e:
        raise ValueError(f"Erro ao processar resposta do ChatGPT: {e}")
