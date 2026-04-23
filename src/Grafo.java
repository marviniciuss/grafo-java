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

    // Metodo auxiliar para redimensionar o vetor na unha (sem usar ArrayList)
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

// =========================================================================
    // QUESTÃO 8: SUBGRAFO MAXIMAL ÁRVORE (ALGORITMO DE KRUSKAL COM UNION-FIND)
    // =========================================================================

    // Classe auxiliar interna apenas para organizar as arestas em um vetor linear
    private class ArestaKruskal {
        int origem, destino;
        double custo;
        ArestaKruskal(int o, int d, double c) {
            this.origem = o; this.destino = d; this.custo = c;
        }
    }

    public void subgrafoMaximalArvore() {
        if (vetorVertices == null || capacidadeAtual == 0) {
            System.out.println("Grafo vazio.");
            return;
        }

        // 1. Contar total de arestas e vértices ativos
        int totalArestas = 0;
        int numVerticesAtivos = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                numVerticesAtivos++;
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    totalArestas++;
                    atual = atual.proxima;
                }
            }
        }

        if (numVerticesAtivos <= 1 || totalArestas == 0) {
            System.out.println("Não há arestas suficientes para formar uma árvore.");
            return;
        }

        // 2. Extrair todas as arestas do grafo para um vetor simples
        ArestaKruskal[] todasArestas = new ArestaKruskal[totalArestas];
        int indexAresta = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    todasArestas[indexAresta++] = new ArestaKruskal(i, atual.idDestino, atual.custo);
                    atual = atual.proxima;
                }
            }
        }

        // 3. Ordenar as arestas pelo menor custo (Bubble Sort manual)
        for (int i = 0; i < totalArestas - 1; i++) {
            for (int j = 0; j < totalArestas - i - 1; j++) {
                if (todasArestas[j].custo > todasArestas[j + 1].custo) {
                    ArestaKruskal temp = todasArestas[j];
                    todasArestas[j] = todasArestas[j + 1];
                    todasArestas[j + 1] = temp;
                }
            }
        }

        // 4. Estrutura UNION-FIND (Conjuntos Disjuntos)
        int[] pai = new int[capacidadeAtual];
        for (int i = 0; i < capacidadeAtual; i++) {
            pai[i] = i; // Inicialmente, cada vértice é "pai" de si mesmo (isolado)
        }

        System.out.println("\n[Construindo Subgrafo Maximal Árvore - Algoritmo de Kruskal]");
        double custoTotal = 0.0;
        int arestasNaArvore = 0;

        System.out.println(">>> ARESTAS DA ÁRVORE GERADORA:");

        // 5. Varredura do Algoritmo de Kruskal
        for (int i = 0; i < totalArestas; i++) {
            ArestaKruskal aresta = todasArestas[i];

            // Verifica em qual conjunto as pontas da aresta estão
            int raizOrigem = encontrarRaiz(aresta.origem, pai);
            int raizDestino = encontrarRaiz(aresta.destino, pai);

            // Se as raízes são diferentes, conectar não forma ciclo. Incluímos na árvore!
            if (raizOrigem != raizDestino) {
                pai[raizOrigem] = raizDestino; // Une os dois conjuntos
                custoTotal += aresta.custo;
                arestasNaArvore++;

                System.out.println(" -> Adicionada: V" + aresta.origem + " --(Custo: " + aresta.custo + ")--> V" + aresta.destino);

                // Condição de parada: Uma árvore tem sempre (Vértices - 1) arestas
                if (arestasNaArvore == numVerticesAtivos - 1) {
                    break;
                }
            }
        }

        System.out.println("=========================================");
        System.out.println("Custo Total da Árvore: " + custoTotal);
        if (arestasNaArvore < numVerticesAtivos - 1) {
            System.out.println("AVISO: O grafo original possui partes desconectadas. Foi gerada uma FLORESTA em vez de uma Árvore única.");
        }
        System.out.println("=========================================\n");
    }

    // Metodo auxiliar matemático para o Union-Find (com Compressão de Caminho)
    private int encontrarRaiz(int i, int[] pai) {
        if (pai[i] == i) {
            return i;
        }
        // Ao voltar da recursão, atualiza o pai direto para a raiz (otimização extrema)
        pai[i] = encontrarRaiz(pai[i], pai);
        return pai[i];
    }

// =========================================================================
    // QUESTÃO 9: VERIFICAÇÃO DE ISOMORFISMO
    // =========================================================================

    public boolean verificarIsomorfismo(Grafo outroGrafo) {
        if (this.vetorVertices == null || outroGrafo.vetorVertices == null) return false;

        // Filtro 1: Mesma quantidade de vértices ativos?
        int v1 = this.contarVerticesAtivos();
        int v2 = outroGrafo.contarVerticesAtivos();
        if (v1 != v2) return false;
        if (v1 == 0) return true; // Dois grafos vazios são isomorfos

        // Filtro 2: Mesma quantidade de arestas totais?
        int e1 = this.contarArestasTotais();
        int e2 = outroGrafo.contarArestasTotais();
        if (e1 != e2) return false;

        // Extrai os IDs exatos que estão sendo usados (pois podemos ter apagado alguns no meio do caminho)
        int[] ids1 = this.obterIdsAtivos(v1);
        int[] ids2 = outroGrafo.obterIdsAtivos(v2);

        // Transforma as Listas em Matrizes de Adjacência apenas para a comparação matemática
        int[][] mat1 = this.construirMatriz(ids1, v1);
        int[][] mat2 = outroGrafo.construirMatriz(ids2, v2);

        boolean[] visitado = new boolean[v1];
        int[] mapeamento = new int[v1]; // Tenta mapear o vértice X do G1 para o Y do G2

        // Chama a força bruta (Backtracking)
        return backtrackIsomorfismo(0, v1, mat1, mat2, visitado, mapeamento);
    }

    // Métodos Auxiliares para o Isomorfismo
    private int contarVerticesAtivos() {
        int cont = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) cont++;
        }
        return cont;
    }

    private int contarArestasTotais() {
        int cont = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    cont++;
                    atual = atual.proxima;
                }
            }
        }
        return cont;
    }

    private int[] obterIdsAtivos(int qtd) {
        int[] ids = new int[qtd];
        int index = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                ids[index++] = vetorVertices[i].id;
            }
        }
        return ids;
    }

    private int[][] construirMatriz(int[] ids, int n) {
        int[][] mat = new int[n][n]; // Guarda a QUANTIDADE de arestas (pois é multigrafo)
        for (int i = 0; i < n; i++) {
            Aresta atual = vetorVertices[ids[i]].inicioLista;
            while (atual != null) {
                int j = encontrarIndiceNoVetor(ids, atual.idDestino);
                if (j != -1) mat[i][j]++;
                atual = atual.proxima;
            }
        }
        return mat;
    }

    private int encontrarIndiceNoVetor(int[] vetor, int valor) {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == valor) return i;
        }
        return -1;
    }

    // O motor matemático: Testa todas as permutações possíveis
    private boolean backtrackIsomorfismo(int indexA, int n, int[][] mat1, int[][] mat2, boolean[] visitado, int[] mapeamento) {
        // Se conseguimos mapear todos os vértices, verificamos se as arestas batem
        if (indexA == n) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // O número de arestas entre i->j no G1 tem que ser igual ao mapeado no G2
                    if (mat1[i][j] != mat2[mapeamento[i]][mapeamento[j]]) {
                        return false;
                    }
                }
            }
            return true; // Encontrou um mapeamento perfeito!
        }

        // Tenta mapear o vértice 'indexA' do G1 para algum vértice 'i' do G2
        for (int i = 0; i < n; i++) {
            if (!visitado[i]) {
                visitado[i] = true;
                mapeamento[indexA] = i;

                if (backtrackIsomorfismo(indexA + 1, n, mat1, mat2, visitado, mapeamento)) {
                    return true;
                }

                visitado[i] = false; // Desfaz e tenta a próxima combinação
            }
        }
        return false;
    }


// =========================================================================
    // QUESTÃO 10: SIMILARIDADE (JACCARD, COSSENO E SOBREPOSIÇÃO)
    // =========================================================================

    public void calcularSimilaridade(Grafo outroGrafo) {
        if (this.vetorVertices == null || outroGrafo.vetorVertices == null) {
            System.out.println("Erro: Um dos grafos não está inicializado.");
            return;
        }

        // 1. Calcula o tamanho dos conjuntos (Arestas distintas de G1 e G2)
        int arestasG1 = this.contarArestasDistintas();
        int arestasG2 = outroGrafo.contarArestasDistintas();

        // 2. Calcula a Interseção (Arestas presentes em ambos)
        int intersecao = this.contarIntersecaoDistinta(outroGrafo);

        // 3. Calcula a União (Soma de tudo menos a interseção)
        int uniao = arestasG1 + arestasG2 - intersecao;

        System.out.println("\n[Dados dos Conjuntos de Arestas]");
        System.out.println(" |E1| (Arestas G1) : " + arestasG1);
        System.out.println(" |E2| (Arestas G2) : " + arestasG2);
        System.out.println(" Interseção (Em ambos): " + intersecao);
        System.out.println(" União (Total único)  : " + uniao);

        System.out.println("\n>>> RESULTADOS DA QUESTÃO 10 <<<");

        // Tratamento de segurança para grafos vazios (divisão por zero)
        if (uniao == 0 || arestasG1 == 0 || arestasG2 == 0) {
            System.out.println("Não é possível calcular similaridade em grafos sem arestas válidas.");
            return;
        }

        // JACCARD: Interseção / União
        double jaccard = (double) intersecao / uniao;

        // COSSENO: Interseção / Raiz(E1 * E2)
        double cosseno = (double) intersecao / Math.sqrt(arestasG1 * arestasG2);

        // SOBREPOSIÇÃO (OVERLAP): Interseção / Minimo(E1, E2)
        double minElementos = Math.min(arestasG1, arestasG2);
        double sobreposicao = (double) intersecao / minElementos;

        System.out.printf(" 1. Jaccard      : %.4f (%.2f%%)%n", jaccard, jaccard * 100);
        System.out.printf(" 2. Cosseno      : %.4f (%.2f%%)%n", cosseno, cosseno * 100);
        System.out.printf(" 3. Sobreposição : %.4f (%.2f%%)%n", sobreposicao, sobreposicao * 100);
    }

    // Métodos Auxiliares para a Matemática de Conjuntos
    private int contarArestasDistintas() {
        int cont = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                boolean[] visitadoDestino = new boolean[capacidadeAtual];
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    // Garante que só conta 1 vez no conjunto, mesmo sendo multigrafo
                    if (atual.idDestino < capacidadeAtual && !visitadoDestino[atual.idDestino]) {
                        visitadoDestino[atual.idDestino] = true;
                        cont++;
                    }
                    atual = atual.proxima;
                }
            }
        }
        return cont;
    }

    private int contarIntersecaoDistinta(Grafo outro) {
        int intersecao = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            // Só faz sentido buscar se a origem existe nos dois grafos
            if (this.vetorVertices[i] != null && i < outro.capacidadeAtual && outro.vetorVertices[i] != null) {
                boolean[] visitadoDestino = new boolean[this.capacidadeAtual];
                Aresta atual = this.vetorVertices[i].inicioLista;

                while (atual != null) {
                    if (atual.idDestino < this.capacidadeAtual && !visitadoDestino[atual.idDestino]) {
                        visitadoDestino[atual.idDestino] = true;

                        // Verifica se o outro grafo também possui essa aresta
                        if (outro.buscarAresta(i, atual.idDestino)) {
                            intersecao++;
                        }
                    }
                    atual = atual.proxima;
                }
            }
        }
        return intersecao;
    }

// =========================================================================
    // QUESTÕES 11, 12 e 13: MÉTRICAS DE DISTÂNCIA E CICLOS (COM RASTREAMENTO)
    // =========================================================================

    // Classe auxiliar para o Dijkstra retornar as distâncias e o caminho rastreado
    private class RetornoDijkstra {
        double[] distancias;
        int[] anteriores; // Guarda o "pai" de cada vértice para desenhar a rota
        public RetornoDijkstra(double[] d, int[] a) { this.distancias = d; this.anteriores = a; }
    }

    // Motor Base: Algoritmo de Dijkstra com rastreamento de rota
    private RetornoDijkstra calcularDistancias(int origem) {
        double[] dist = new double[capacidadeAtual];
        int[] anterior = new int[capacidadeAtual];
        boolean[] fixo = new boolean[capacidadeAtual];

        for (int i = 0; i < capacidadeAtual; i++) {
            dist[i] = Double.MAX_VALUE;
            anterior[i] = -1; // -1 significa que ainda não tem "pai"
        }
        dist[origem] = 0;

        for (int i = 0; i < capacidadeAtual; i++) {
            int u = -1;
            for (int j = 0; j < capacidadeAtual; j++) {
                if (vetorVertices[j] != null && !fixo[j] && (u == -1 || dist[j] < dist[u])) {
                    u = j;
                }
            }

            if (u == -1 || dist[u] == Double.MAX_VALUE) break;
            fixo[u] = true;

            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                if (dist[u] + atual.custo < dist[atual.idDestino]) {
                    dist[atual.idDestino] = dist[u] + atual.custo;
                    anterior[atual.idDestino] = u; // ANOTAÇÃO DA ROTA: Salva de onde viemos!
                }
                atual = atual.proxima;
            }
        }
        return new RetornoDijkstra(dist, anterior);
    }

    // QUESTÃO 12: Excentricidade de um vértice v
    public double calcularExcentricidade(int v) {
        if (v >= capacidadeAtual || vetorVertices[v] == null) return -1;
        RetornoDijkstra dijkstra = calcularDistancias(v);
        double max = 0;
        for (double d : dijkstra.distancias) {
            if (d != Double.MAX_VALUE && d > max) max = d;
        }
        return max;
    }

    // QUESTÃO 13: Raio, Diâmetro e Centro
    public void calcularMetricasGlobais() {
        int n = contarVerticesAtivos();
        if (n == 0) return;

        double[] excentricidades = new double[capacidadeAtual];
        double raio = Double.MAX_VALUE;
        double diametro = 0;

        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                excentricidades[i] = calcularExcentricidade(i);
                if (excentricidades[i] < raio) raio = excentricidades[i];
                if (excentricidades[i] > diametro) diametro = excentricidades[i];
            }
        }

        System.out.println("\n>>> MÉTRICAS TOTAIS DO GRAFO <<<");
        System.out.println("Raio: " + raio);
        System.out.println("Diâmetro: " + diametro);
        System.out.print("Centro (Vértices com excentricidade = Raio): ");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && excentricidades[i] == raio) {
                System.out.print("V" + i + " ");
            }
        }
        System.out.println("\n");
    }

    // QUESTÃO 11: Cintura e Circunferência (Com visualização de caminho)
    public void calcularCinturaECircunferencia() {
        double cintura = Double.MAX_VALUE;
        double circunferencia = 0;
        String caminhoCintura = "Nenhum";
        String caminhoCircunferencia = "Nenhum";

        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    double custoOriginal = atual.custo;
                    int destino = atual.idDestino;

                    // ---> ADICIONE ESTAS 4 LINHAS AQUI <---
                    // Ignora "Self-loops" (Vértice apontando para ele mesmo)
                    // para forçar a busca por um ciclo real no mapa.
                    if (i == destino) {
                        atual = atual.proxima;
                        continue;
                    }

                    // "Remove" temporariamente a aresta e recalcula rotas
                    atual.custo = Double.MAX_VALUE;
                    RetornoDijkstra dijkstra = calcularDistancias(i);

                    // Se ainda existe um caminho para voltar, formamos um ciclo!
                    if (dijkstra.distancias[destino] != Double.MAX_VALUE) {
                        double ciclo = dijkstra.distancias[destino] + custoOriginal;

                        // Monta o texto do caminho rastreando os "pais"
                        String caminhoAtual = montarCaminhoCiclo(i, destino, dijkstra.anteriores);

                        if (ciclo < cintura) {
                            cintura = ciclo;
                            caminhoCintura = caminhoAtual;
                        }
                        if (ciclo > circunferencia) {
                            circunferencia = ciclo;
                            caminhoCircunferencia = caminhoAtual;
                        }
                    }

                    atual.custo = custoOriginal; // Restaura a aresta
                    atual = atual.proxima;
                }
            }
        }

        System.out.println("\n>>> CICLOS DO GRAFO <<<");
        if (cintura == Double.MAX_VALUE) {
            System.out.println("O grafo não possui ciclos válidos (é um grafo acíclico).");
        } else {
            System.out.println("Cintura (Menor Ciclo): " + cintura);
            System.out.println(" -> Caminho da Cintura: " + caminhoCintura);
            System.out.println("Circunferência (Maior Ciclo): " + circunferencia);
            System.out.println(" -> Caminho da Circunferência: " + caminhoCircunferencia);
        }
    }

    // Metodo auxiliar para transformar o vetor de "pais" em uma String legível
    private String montarCaminhoCiclo(int origem, int destino, int[] anteriores) {
        String caminho = "V" + origem;
        int atual = destino;
        String caminhoInvertido = "";

        // Rastreia de trás para frente, do destino até a origem
        while (atual != -1 && atual != origem) {
            caminhoInvertido = " -> V" + atual + caminhoInvertido;
            atual = anteriores[atual];
        }

        // Retorna a rota completa fechando o círculo
        return caminho + caminhoInvertido + " -> V" + origem;
    }









}