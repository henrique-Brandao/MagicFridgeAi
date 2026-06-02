export type FoodItem = {
  id?: number;
  nome: string;
  categoria?: string;
  quantidade: number;
  unidade: string;
  validade: string;
};

export type Recipe = {
  titulo?: string;
  resumo?: string;
  ingredientes?: string[];
  preparo?: string[];
  observacoes?: string[];
};

const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
  });

  if (response.status === 204) {
    return [] as T;
  }

  const text = await response.text();

  if (!response.ok) {
    throw new Error(text || "A API retornou um erro inesperado.");
  }

  return text ? (JSON.parse(text) as T) : ({} as T);
}

export function listFoods() {
  return request<FoodItem[]>("/food");
}

export function createFood(food: FoodItem) {
  return request<FoodItem>("/food", {
    method: "POST",
    body: JSON.stringify(food),
  });
}

export function deleteFood(id: number) {
  return request<void>(`/food/${id}`, {
    method: "DELETE",
  });
}

export async function generateRecipe() {
  const response = await fetch(`${API_URL}/recipes/generate`);
  const text = await response.text();

  if (!response.ok) {
    let message = text;
    try {
      const recipe = JSON.parse(text) as Recipe;
      message = recipe.resumo || recipe.titulo || message;
    } catch {
      message = text;
    }
    throw new Error(message || "Nao foi possivel gerar a receita.");
  }

  return text ? (JSON.parse(text) as Recipe) : {};
}
