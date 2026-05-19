export type Categoria = "VEGETAL" | "FRUTA" | "CARNE" | "LATICINIO" | "GORDURA" | "DOCE" | "BEBIDA" | "GRAO";

export type FoodItem = {
  id?: number;
  nome: string;
  categoria: Categoria;
  quantidade: number;
  validade: string;
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
    throw new Error(text || "Nao foi possivel gerar a receita.");
  }

  return text;
}
