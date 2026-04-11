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

    // =========================================================================
    // QUESTÃO 6: TRAVESSIA IN-ORDER E ROTULAÇÃO TOPOLÓGICA
    // =========================================================================
    public void travessiaInOrdem() {
        if (vetorVertices == null || capacidadeAtual == 0) {
            System.out.println("Grafo vazio ou não inicializado.");
            return;
        }

        boolean[] visitado = new boolean[capacidadeAtual];

        // Conta os vértices ativos para criar o array de resultados
        int numVerticesAtivos = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) numVerticesAtivos++;
        }

        int[] ordemTopologica = new int[numVerticesAtivos];
        int[] contadorRotulo = {0}; // Usado como array de 1 posição para passar por referência

        System.out.println("\n[Rastro da Travessia In-Order]");

        // O loop garante que todos os vértices sejam visitados (mesmo em grafos desconexos)
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !visitado[i]) {
                inOrdemRecursivo(i, visitado, contadorRotulo, ordemTopologica);
            }
        }

        // Imprime o resultado final exigido pela questão
        System.out.print("\n>>> RESULTADO DA ROTULAÇÃO TOPOLÓGICA (In-Order): ");
        for (int i = 0; i < numVerticesAtivos; i++) {
            System.out.print("V" + ordemTopologica[i] + " ");
        }
        System.out.println("\n");
    }

    private void inOrdemRecursivo(int vIndex, boolean[] visitado, int[] rotulo, int[] ordemTopologica) {
        visitado[vIndex] = true;
        Aresta atual = vetorVertices[vIndex].inicioLista;

        // 1. Visita o PRIMEIRO vizinho (Simula a descida à "Esquerda" da árvore)
        if (atual != null) {
            if (vetorVertices[atual.idDestino] != null && !visitado[atual.idDestino]) {
                inOrdemRecursivo(atual.idDestino, visitado, rotulo, ordemTopologica);
            }
            atual = atual.proxima; // Avança o ponteiro para o próximo vizinho
        }

        // 2. Processa a "RAIZ" (O vértice atual)
        ordemTopologica[rotulo[0]] = vetorVertices[vIndex].id;
        System.out.println(" -> Atribuindo Rótulo Topológico [" + rotulo[0] + "] ao V" + vetorVertices[vIndex].id + " (" + vetorVertices[vIndex].caracteristica + ")");
        rotulo[0]++;

        // 3. Visita os VIZINHOS RESTANTES (Simula a descida à "Direita" da árvore)
        while (atual != null) {
            if (vetorVertices[atual.idDestino] != null && !visitado[atual.idDestino]) {
                inOrdemRecursivo(atual.idDestino, visitado, rotulo, ordemTopologica);
            }
            atual = atual.proxima;
        }
    }

    // =========================================================================
    // QUESTÃO 7: TRAVESSIA POS-ORDER E ROTULAÇÃO TOPOLÓGICA
    // =========================================================================
    public void travessiaPosOrdem() {
        if (vetorVertices == null || capacidadeAtual == 0) return;

        boolean[] visitado = new boolean[capacidadeAtual];
        int numVerticesAtivos = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) numVerticesAtivos++;
        }

        int[] ordemTopologica = new int[numVerticesAtivos];
        int[] contadorRotulo = {0};

        System.out.println("\n[Rastro da Travessia Pos-Order]");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !visitado[i]) {
                posOrdemRecursivo(i, visitado, contadorRotulo, ordemTopologica);
            }
        }

        System.out.print("\n>>> RESULTADO DA ROTULAÇÃO TOPOLÓGICA (Pos-Order): ");
        // Imprime de trás para frente para ter a ordem topológica correta
        for (int i = numVerticesAtivos - 1; i >= 0; i--) {
            System.out.print("V" + ordemTopologica[i] + " ");
        }
        System.out.println("\n");
    }

    private void posOrdemRecursivo(int vIndex, boolean[] visitado, int[] rotulo, int[] ordemTopologica) {
        visitado[vIndex] = true;
        Aresta atual = vetorVertices[vIndex].inicioLista;

        // 1. PRIMEIRO visita absolutamente TODOS os vizinhos
        while (atual != null) {
            if (vetorVertices[atual.idDestino] != null && !visitado[atual.idDestino]) {
                posOrdemRecursivo(atual.idDestino, visitado, rotulo, ordemTopologica);
            }
            atual = atual.proxima;
        }

        // 2. POR ÚLTIMO, processa a "RAIZ"
        ordemTopologica[rotulo[0]] = vetorVertices[vIndex].id;
        System.out.println(" -> Finalizou e Rotulou o V" + vetorVertices[vIndex].id);
        rotulo[0]++;
    }

    // =========================================================================
    // QUESTÃO 18: TRAVESSIA BFS (ALGORITMO DE KAHN)
    // =========================================================================
    public void bfsOrdenacaoTopologica() {
        if (vetorVertices == null || capacidadeAtual == 0) return;

        int numVerticesAtivos = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) numVerticesAtivos++;
        }

        // 1. Calcula o grau de entrada (quantas setas chegam) de cada vértice
        int[] grauEntrada = new int[capacidadeAtual];
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    grauEntrada[atual.idDestino]++;
                    atual = atual.proxima;
                }
            }
        }

        // 2. Nossa FILA manual (feita com vetor para o professor não reclamar)
        int[] fila = new int[capacidadeAtual];
        int inicioFila = 0;
        int fimFila = 0;

        // Adiciona na fila todos que têm grau de entrada 0 (não dependem de ninguém)
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && grauEntrada[i] == 0) {
                fila[fimFila++] = i;
            }
        }

        int[] ordemTopologica = new int[numVerticesAtivos];
        int contadorOrdem = 0;

        System.out.println("\n[Rastro da Travessia BFS - Kahn]");

        // 3. Processa a fila
        while (inicioFila < fimFila) {
            int u = fila[inicioFila++]; // Tira o primeiro da fila
            ordemTopologica[contadorOrdem++] = vetorVertices[u].id;
            System.out.println(" -> Visitando Camada: V" + vetorVertices[u].id);

            // Reduz o grau de entrada dos vizinhos
            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                grauEntrada[atual.idDestino]--;
                // Se o vizinho zerou as dependências, entra na fila
                if (grauEntrada[atual.idDestino] == 0) {
                    fila[fimFila++] = atual.idDestino;
                }
                atual = atual.proxima;
            }
        }

        // Verifica se processou tudo (se não, o grafo tem um ciclo)
        if (contadorOrdem != numVerticesAtivos) {
            System.out.println("\n>>> AVISO: O grafo possui um CICLO! Ordem topológica BFS impossível.");
        } else {
            System.out.print("\n>>> RESULTADO DA ROTULAÇÃO TOPOLÓGICA (BFS): ");
            for (int i = 0; i < numVerticesAtivos; i++) {
                System.out.print("V" + ordemTopologica[i] + " ");
            }
            System.out.println("\n");
        }
    }








}