import java.util.Random;

public class Grafo {
    private Vertice[] vetorVertices;
    private int capacidadeAtual;

    // 1. CONSTRUIR GRAFO
    public Grafo(int capacidadeInicial) {
        this.capacidadeAtual = capacidadeInicial;
        this.vetorVertices = new Vertice[capacidadeInicial];
    }

    // 2. DESTRUIR GRAFO (O Garbage Collector do Java limpa a memória quando removemos as referências)
    public void destruirGrafo() {
        this.vetorVertices = null;
        this.capacidadeAtual = 0;
    }

    // Método auxiliar para redimensionar o vetor na unha (sem usar ArrayList)
    private void garantirCapacidade(int idDesejado) {
        if (vetorVertices == null) return;
        if (idDesejado >= capacidadeAtual) {
            int novaCapacidade = idDesejado + 10;
            Vertice[] novoVetor = new Vertice[novaCapacidade];
            for (int i = 0; i < capacidadeAtual; i++) {
                novoVetor[i] = vetorVertices[i];
            }
            this.vetorVertices = novoVetor;
            this.capacidadeAtual = novaCapacidade;
        }
    }

    // 3. INCLUIR VÉRTICE
    public void incluirVertice(int id, String caracteristica) {
        garantirCapacidade(id);
        if (vetorVertices[id] == null) {
            vetorVertices[id] = new Vertice(id, caracteristica);
        }
    }

    // 4. REMOVER VÉRTICE
    public void removerVertice(int id) {
        if (id >= capacidadeAtual || vetorVertices[id] == null) return;

        // Primeiro: Remover o vértice do vetor
        vetorVertices[id] = null;

        // Segundo: Varre todos os outros vértices para remover arestas que apontavam para o ID excluído
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                removerTodasArestasParaDestino(i, id);
            }
        }
    }

    private void removerTodasArestasParaDestino(int origem, int destino) {
        Vertice v = vetorVertices[origem];
        if (v == null || v.inicioLista == null) return;

        // Remove do início da lista, se necessário (pode haver várias, é um multigrafo)
        while (v.inicioLista != null && v.inicioLista.idDestino == destino) {
            v.inicioLista = v.inicioLista.proxima;
        }

        // Remove do meio/fim da lista
        Aresta atual = v.inicioLista;
        while (atual != null && atual.proxima != null) {
            if (atual.proxima.idDestino == destino) {
                atual.proxima = atual.proxima.proxima;
            } else {
                atual = atual.proxima;
            }
        }
    }

    // 5. ALTERAR E BUSCAR VÉRTICE
    public void alterarVertice(int id, String novaCaracteristica) {
        if (id < capacidadeAtual && vetorVertices[id] != null) {
            vetorVertices[id].caracteristica = novaCaracteristica;
        }
    }

    public String buscarVertice(int id) {
        if (id < capacidadeAtual && vetorVertices[id] != null) {
            return vetorVertices[id].caracteristica;
        }
        return "Vértice não encontrado.";
    }

    // 6. INCLUIR ARESTA
    public void incluirAresta(int origem, int destino, double custo, String caracteristica) {
        garantirCapacidade(Math.max(origem, destino));
        if (vetorVertices[origem] == null) incluirVertice(origem, "Vértice " + origem);
        if (vetorVertices[destino] == null) incluirVertice(destino, "Vértice " + destino);

        Aresta nova = new Aresta(destino, custo, caracteristica);
        // Insere no início da lista encadeada (mais rápido: O(1))
        nova.proxima = vetorVertices[origem].inicioLista;
        vetorVertices[origem].inicioLista = nova;
    }

    // 7. REMOVER ARESTA (Remove apenas uma ocorrência, pois é multigrafo)
    public void removerAresta(int origem, int destino) {
        if (origem >= capacidadeAtual || vetorVertices[origem] == null) return;

        Aresta atual = vetorVertices[origem].inicioLista;
        Aresta anterior = null;

        while (atual != null) {
            if (atual.idDestino == destino) {
                if (anterior == null) {
                    vetorVertices[origem].inicioLista = atual.proxima; // Remove a cabeça
                } else {
                    anterior.proxima = atual.proxima; // Pula o nó atual
                }
                return; // Interrompe após remover uma ligação
            }
            anterior = atual;
            atual = atual.proxima;
        }
    }

    // 8. ALTERAR E BUSCAR ARESTA
    public void alterarAresta(int origem, int destino, double novoCusto, String novaCaracteristica) {
        Aresta a = buscarArestaReferencia(origem, destino);
        if (a != null) {
            a.custo = novoCusto;
            a.caracteristica = novaCaracteristica;
        }
    }

    public boolean buscarAresta(int origem, int destino) {
        return buscarArestaReferencia(origem, destino) != null;
    }

    private Aresta buscarArestaReferencia(int origem, int destino) {
        if (origem >= capacidadeAtual || vetorVertices[origem] == null) return null;
        Aresta atual = vetorVertices[origem].inicioLista;
        while (atual != null) {
            if (atual.idDestino == destino) return atual;
            atual = atual.proxima;
        }
        return null;
    }

    // 9. MOSTRAR GRAFO
    public void mostrarGrafo() {
        if (vetorVertices == null) {
            System.out.println("Grafo destruído ou não inicializado.");
            return;
        }
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                // Imprime a origem
                System.out.print("V" + vetorVertices[i].id + " (" + vetorVertices[i].caracteristica + ") -> ");

                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    // Busca o rótulo do destino acessando o vetor principal
                    String rotuloDestino = vetorVertices[atual.idDestino].caracteristica;

                    // Imprime a aresta com o rótulo incluído
                    System.out.print("[V" + atual.idDestino + " (" + rotuloDestino + ") | Custo: " + atual.custo + "] ");
                    atual = atual.proxima;
                }
                System.out.println();
            }
        }
        System.out.println("-------------------------------------------------");
    }

    // 10. PREENCHIMENTO AUTOMÁTICO
    public void preenchimentoAutomatico(int numVerticesDesejado, int numArestasDesejado) {
        Random rand = new Random();
        for (int i = 0; i < numVerticesDesejado; i++) {
            incluirVertice(i, "AutoGerado " + i);
        }
        for (int i = 0; i < numArestasDesejado; i++) {
            int origem = rand.nextInt(numVerticesDesejado);
            int destino = rand.nextInt(numVerticesDesejado);
            double custo = Math.round(rand.nextDouble() * 100.0) / 10.0;
            incluirAresta(origem, destino, custo, "LigacaoAuto");
        }
    }
}