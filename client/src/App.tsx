import { FormEvent, useEffect, useState } from "react";
import { ChefHat, Loader2, PackagePlus, RefreshCw, Sparkles, Trash2, Utensils } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select } from "@/components/ui/select";
import { createFood, deleteFood, FoodItem, generateRecipe, listFoods, type Categoria } from "@/lib/api";

const categorias: Categoria[] = ["VEGETAL", "FRUTA", "CARNE", "LATICINIO", "GORDURA", "DOCE", "BEBIDA", "GRAO"];

const categoryLabels: Record<Categoria, string> = {
  VEGETAL: "Vegetal",
  FRUTA: "Fruta",
  CARNE: "Carne",
  LATICINIO: "Laticinio",
  GORDURA: "Gordura",
  DOCE: "Doce",
  BEBIDA: "Bebida",
  GRAO: "Grao",
};

const DEFAULT_VALIDADE = "2099-12-31";

const initialForm: FoodItem = {
  nome: "",
  categoria: "VEGETAL",
  quantidade: 1,
  validade: DEFAULT_VALIDADE,
};


export default function App() {
  const [foods, setFoods] = useState<FoodItem[]>([]);
  const [form, setForm] = useState<FoodItem>(initialForm);
  const [recipe, setRecipe] = useState("");
  const [loadingFoods, setLoadingFoods] = useState(true);
  const [saving, setSaving] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [error, setError] = useState("");


  async function loadFoods() {
    setLoadingFoods(true);
    setError("");
    try {
      setFoods(await listFoods());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nao foi possivel carregar os ingredientes.");
    } finally {
      setLoadingFoods(false);
    }
  }

  useEffect(() => {
    void loadFoods();
  }, []);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSaving(true);
    setError("");

    try {
      await createFood({ ...form, quantidade: Number(form.quantidade), validade: DEFAULT_VALIDADE });
      setForm(initialForm);
      await loadFoods();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nao foi possivel salvar o ingrediente.");
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(id?: number) {
    if (!id) return;
    setError("");
    try {
      await deleteFood(id);
      setFoods((current) => current.filter((item) => item.id !== id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nao foi possivel remover o ingrediente.");
    }
  }

  async function handleGenerateRecipe() {
    setGenerating(true);
    setError("");
    setRecipe("");

    try {
      setRecipe(await generateRecipe());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nao foi possivel gerar a receita.");
    } finally {
      setGenerating(false);
    }
  }

  return (
    <main className="min-h-screen surface-grid bg-background">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 px-4 py-6 sm:px-6 lg:px-8">
        <header className="flex flex-col gap-4 border-b border-border/80 pb-6 lg:flex-row lg:items-end lg:justify-between">
          <div className="space-y-3">
            <Badge className="w-fit border-primary/30 bg-primary/10 text-primary">Magic Fridge AI</Badge>
            <div>
              <h1 className="text-3xl font-semibold tracking-normal text-foreground sm:text-4xl">Cozinha inteligente para o que voce ja tem</h1>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-muted-foreground">
                Cadastre os ingredientes da geladeira, organize quantidades e gere sugestoes de receita com IA.
              </p>
            </div>
          </div>
          <div className="grid grid-cols-3 gap-3 rounded-lg border bg-card/80 p-3 shadow-glow backdrop-blur">
            <Metric label="Itens" value={foods.length.toString()} />
            <Metric label="Categorias" value={new Set(foods.map((food) => food.categoria)).size.toString()} />
            <Metric label="Modo IA" value="ON" accent />
          </div>
        </header>

        {error ? <div className="rounded-lg border border-destructive/40 bg-destructive/10 px-4 py-3 text-sm text-destructive">{error}</div> : null}

        <section className="grid gap-6 lg:grid-cols-[380px_1fr]">
          <Card className="h-fit bg-card/90 backdrop-blur">
            <CardHeader>
              <CardTitle className="flex items-center gap-2"><PackagePlus /> Novo ingrediente</CardTitle>
              <CardDescription>Adicione os alimentos disponiveis para melhorar a sugestao da IA.</CardDescription>
            </CardHeader>
            <CardContent>
              <form className="space-y-4" onSubmit={handleSubmit}>
                <div className="space-y-2">
                  <Label htmlFor="nome">Nome</Label>
                  <Input id="nome" placeholder="Tomate, arroz, frango..." value={form.nome} onChange={(event) => setForm({ ...form, nome: event.target.value })} required />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div className="space-y-2">
                    <Label htmlFor="categoria">Categoria</Label>
                    <Select id="categoria" value={form.categoria} onChange={(event) => setForm({ ...form, categoria: event.target.value as Categoria })}>
                      {categorias.map((categoria) => <option key={categoria} value={categoria}>{categoryLabels[categoria]}</option>)}
                    </Select>
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="quantidade">Quantidade</Label>
                    <Input id="quantidade" min={1} type="number" value={form.quantidade} onChange={(event) => setForm({ ...form, quantidade: Number(event.target.value) })} required />
                  </div>
                </div>
                <Button className="w-full" disabled={saving} type="submit">
                  {saving ? <Loader2 className="animate-spin" /> : <PackagePlus />}
                  Salvar ingrediente
                </Button>
              </form>
            </CardContent>
          </Card>

          <div className="space-y-6">
            <Card className="bg-card/90 backdrop-blur">
              <CardHeader className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <CardTitle className="flex items-center gap-2"><Utensils /> Inventario</CardTitle>
                  <CardDescription>Ingredientes disponiveis para a proxima receita.</CardDescription>
                </div>
                <Button variant="outline" size="sm" onClick={() => void loadFoods()} disabled={loadingFoods}>
                  <RefreshCw className={loadingFoods ? "animate-spin" : ""} /> Atualizar
                </Button>
              </CardHeader>
              <CardContent>
                {loadingFoods ? (
                  <EmptyState icon={<Loader2 className="animate-spin" />} title="Carregando ingredientes" />
                ) : foods.length === 0 ? (
                  <EmptyState icon={<ChefHat />} title="Sua geladeira ainda esta vazia" />
                ) : (
                  <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
                    {foods.map((food) => {
                      return (
                        <article key={food.id ?? food.nome} className="rounded-lg border bg-background/70 p-4 transition-colors hover:border-primary/50">
                          <div className="flex items-start justify-between gap-3">
                            <div>
                              <h4 className="font-medium text-foreground">{food.nome}</h4>
                              <Badge className="mt-2 bg-secondary/10 text-secondary">{categoryLabels[food.categoria]}</Badge>
                            </div>
                            <Button aria-label="Remover ingrediente" variant="ghost" size="icon" onClick={() => void handleDelete(food.id)}>
                              <Trash2 />
                            </Button>
                          </div>
                          <div className="mt-4 flex items-center justify-between text-sm text-muted-foreground">
                            <span>Quantidade</span>
                            <span className="font-medium text-foreground">{food.quantidade}</span>
                          </div>
                        </article>
                      );
                    })}
                  </div>
                )}
              </CardContent>
            </Card>

            <Card className="border-primary/30 bg-card/90 backdrop-blur">
              <CardHeader className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <CardTitle className="flex items-center gap-2"><Sparkles /> Receita gerada por IA</CardTitle>
                  <CardDescription>A sugestao usa os ingredientes cadastrados no backend.</CardDescription>
                </div>
                <Button onClick={() => void handleGenerateRecipe()} disabled={generating || foods.length === 0}>
                  {generating ? <Loader2 className="animate-spin" /> : <Sparkles />}
                  Gerar receita
                </Button>
              </CardHeader>
              <CardContent>
                <div className="min-h-48 whitespace-pre-wrap rounded-lg border bg-background/70 p-4 text-sm leading-6 text-foreground">
                  {generating ? "Consultando a IA..." : recipe || "A receita vai aparecer aqui depois que voce gerar uma sugestao."}
                </div>
              </CardContent>
            </Card>
          </div>
        </section>
      </div>
    </main>
  );
}

function Metric({ label, value, accent = false }: { label: string; value: string; accent?: boolean }) {
  return (
    <div className="min-w-20 rounded-md bg-background/70 px-3 py-2 text-center">
      <div className={accent ? "text-xl font-semibold text-destructive" : "text-xl font-semibold text-primary"}>{value}</div>
      <div className="mt-1 text-xs text-muted-foreground">{label}</div>
    </div>
  );
}

function EmptyState({ icon, title }: { icon: React.ReactNode; title: string }) {
  return (
    <div className="flex min-h-40 flex-col items-center justify-center gap-3 rounded-lg border border-dashed bg-background/50 p-6 text-center text-muted-foreground">
      <div className="text-primary [&_svg]:size-6">{icon}</div>
      <p className="text-sm">{title}</p>
    </div>
  );
}
