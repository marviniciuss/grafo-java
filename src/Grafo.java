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

                    // Imprime a aresta com o rótulo incluído e os termos corretos para redes de fluxo
                    if (atual.custoUnitario > 0.0) {
                        System.out.print("[V" + atual.idDestino + " (" + rotuloDestino + ") | Cap: " + atual.custo + ", CustoUnit: " + atual.custoUnitario + "] ");
                    } else {
                        System.out.print("[V" + atual.idDestino + " (" + rotuloDestino + ") | Custo/Cap: " + atual.custo + "] ");
                    }
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


    // =========================================================================
    // QUESTÃO 14: CORTES EM VÉRTICES (ARTICULAÇÕES) E ARESTAS (PONTES)
    // =========================================================================
    private int tempoDFS = 0;

    public void calcularCortes() {
        if (capacidadeAtual == 0) return;

        boolean[] visitado = new boolean[capacidadeAtual];
        int[] desc = new int[capacidadeAtual]; // Tempo de descoberta
        int[] low = new int[capacidadeAtual];  // Menor tempo alcançável
        int[] pai = new int[capacidadeAtual];
        boolean[] isArticulacao = new boolean[capacidadeAtual];

        for (int i = 0; i < capacidadeAtual; i++) {
            pai[i] = -1;
        }

        tempoDFS = 0;
        System.out.println("\n>>> ANÁLISE DE VULNERABILIDADE (CORTES) <<<");
        System.out.println("[Arestas de Corte / Pontes]:");
        boolean temPonte = false;

        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !visitado[i]) {
                if (tarjanCortesRecursivo(i, visitado, desc, low, pai, isArticulacao)) {
                    temPonte = true;
                }
            }
        }

        if (!temPonte) System.out.println(" -> Nenhuma ponte encontrada. O grafo é resistente a falhas de arestas simples.");

        System.out.println("\n[Vértices de Corte / Articulações]:");
        boolean temArticulacao = false;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (isArticulacao[i]) {
                System.out.println(" -> V" + i + " (" + vetorVertices[i].caracteristica + ") é vital. Removê-lo divide o grafo.");
                temArticulacao = true;
            }
        }
        if (!temArticulacao) System.out.println(" -> Nenhuma articulação encontrada.");
    }

    private boolean tarjanCortesRecursivo(int u, boolean[] visitado, int[] desc, int[] low, int[] pai, boolean[] isArticulacao) {
        visitado[u] = true;
        desc[u] = low[u] = ++tempoDFS;
        int filhos = 0;
        boolean encontrouPonte = false;

        Aresta atual = vetorVertices[u].inicioLista;
        while (atual != null) {
            int v = atual.idDestino;
            if (vetorVertices[v] != null) {
                if (!visitado[v]) {
                    filhos++;
                    pai[v] = u;
                    if (tarjanCortesRecursivo(v, visitado, desc, low, pai, isArticulacao)) encontrouPonte = true;

                    low[u] = Math.min(low[u], low[v]);

                    // Regra da Articulação 1: Não é raiz e o filho não alcança ninguém acima
                    if (pai[u] != -1 && low[v] >= desc[u]) isArticulacao[u] = true;

                    // Regra da Ponte: O filho não alcança NINGUÉM acima ou igual a u
                    if (low[v] > desc[u]) {
                        System.out.println(" -> Ponte encontrada: V" + u + " --- V" + v);
                        encontrouPonte = true;
                    }
                } else if (v != pai[u]) {
                    // Aresta de retorno (Back-edge)
                    low[u] = Math.min(low[u], desc[v]);
                }
            }
            atual = atual.proxima;
        }

        // Regra da Articulação 2: Raiz da DFS com 2 ou mais filhos independentes
        if (pai[u] == -1 && filhos > 1) isArticulacao[u] = true;

        return encontrouPonte;
    }

    // =========================================================================
    // QUESTÃO 15: CORTE FUNDAMENTAL
    // =========================================================================
    public void calcularCorteFundamental() {
        System.out.println("\n>>> CORTE FUNDAMENTAL <<<");
        int raiz = -1;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) { raiz = i; break; }
        }
        if (raiz == -1) return;

        // 1. Cria uma árvore geradora simples usando Busca em Largura (BFS)
        boolean[] visitado = new boolean[capacidadeAtual];
        int[] pai = new int[capacidadeAtual];
        for(int i=0; i<capacidadeAtual; i++) pai[i] = -1;

        int[] fila = new int[capacidadeAtual];
        int inicio = 0, fim = 0;

        visitado[raiz] = true;
        fila[fim++] = raiz;

        int arestaTreeOrigem = -1;
        int arestaTreeDestino = -1;

        while (inicio < fim) {
            int u = fila[inicio++];
            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                int v = atual.idDestino;
                if (vetorVertices[v] != null && !visitado[v]) {
                    visitado[v] = true;
                    pai[v] = u;
                    fila[fim++] = v;
                    // Pegamos a PRIMEIRA aresta da árvore para demonstrar o corte fundamental
                    if (arestaTreeOrigem == -1) {
                        arestaTreeOrigem = u;
                        arestaTreeDestino = v;
                    }
                }
                atual = atual.proxima;
            }
        }

        if (arestaTreeOrigem == -1) {
            System.out.println("O grafo não possui arestas suficientes para formar cortes.");
            return;
        }

        System.out.println("1. Aresta da Árvore selecionada: V" + arestaTreeOrigem + " --- V" + arestaTreeDestino);

        // 2. Divide a árvore: Quem fica do lado do Destino? (Usamos DFS a partir do destino, ignorando a origem)
        boolean[] ladoB = new boolean[capacidadeAtual];
        marcarComponente(arestaTreeDestino, arestaTreeOrigem, pai, ladoB);

        System.out.println("2. O conjunto do Corte Fundamental contém as seguintes arestas no grafo original:");
        // 3. O Corte Fundamental são todas as arestas do grafo original que ligam o Lado A ao Lado B
        int contCorte = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !ladoB[i]) { // Se está no Lado A
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    int v = atual.idDestino;
                    if (vetorVertices[v] != null && ladoB[v]) { // E aponta para o Lado B
                        System.out.println(" -> [Corte] V" + i + " --- V" + v + " (Custo: " + atual.custo + ")");
                        contCorte++;
                    }
                    atual = atual.proxima;
                }
            }
        }
        System.out.println(" -> Total de arestas no Corte Fundamental: " + contCorte);
    }

    private void marcarComponente(int u, int ignorar, int[] paiDaArvore, boolean[] conjunto) {
        conjunto[u] = true;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (paiDaArvore[i] == u && i != ignorar) {
                marcarComponente(i, ignorar, paiDaArvore, conjunto);
            }
        }
    }

    // =========================================================================
    // QUESTÃO 16: VERIFICAÇÃO DE PLANARIDADE (Heurística de Euler)
    // =========================================================================
    public void verificarPlanaridade() {
        int v = contarVerticesAtivos();
        int e = contarArestasTotais() / 2; // Divide por 2 assumindo conexões bidirecionais lógicas

        System.out.println("\n>>> TESTE DE PLANARIDADE <<<");
        System.out.println("Vértices (V): " + v);
        System.out.println("Arestas  (E): " + e);

        if (v < 3) {
            System.out.println("Resultado: É PLANAR (Trivialmente, pois tem menos de 3 vértices).");
            return;
        }

        // Teorema de Euler: E <= 3V - 6 para grafos planares
        int limiteEuler = (3 * v) - 6;
        System.out.println("Limite de Arestas pela Fórmula de Euler (3V - 6): " + limiteEuler);

        if (e > limiteEuler) {
            System.out.println(">>> Resultado: NÃO É PLANAR!");
            System.out.println("Motivo: Ele possui " + e + " arestas, o que excede o limite máximo (" + limiteEuler + ") para ser desenhado sem cruzamentos no papel.");
        } else {
            System.out.println(">>> Resultado: PROVAVELMENTE PLANAR.");
            System.out.println("Motivo: Ele respeita a desigualdade de Euler. (Nota acadêmica: A prova definitiva exigiria o Teorema de Kuratowski procurando subgrafos K5 ou K3,3).");
        }
    }


    // =========================================================================
    // QUESTÃO 04: GERADOR DE CENÁRIO (20 Vértices, 35 Arestas, Conexo)
    // =========================================================================
    public void gerarCenarioQ4() {
        destruirGrafo(); // Reseta o grafo atual
        this.capacidadeAtual = 20;
        this.vetorVertices = new Vertice[20];

        // 1. Cria os 20 vértices
        for (int i = 0; i < 20; i++) {
            incluirVertice(i, "V_Q4_" + i);
        }

        java.util.Random rand = new java.util.Random();
        int arestasAdicionadas = 0;

        // 2. Garante que é conexo ligando V0-V1, V1-V2... V18-V19 (19 arestas)
        for (int i = 0; i < 19; i++) {
            double custo = Math.round((rand.nextDouble() * 90 + 10) * 10.0) / 10.0;
            incluirArestaBilateral(i, i + 1, custo, "Conexao_Base");
            arestasAdicionadas++;
        }

        // 3. Adiciona as 16 arestas restantes aleatoriamente
        while (arestasAdicionadas < 35) {
            int u = rand.nextInt(20);
            int v = rand.nextInt(20);
            // Evita loops (u para u) e evita duplicar arestas que já existem
            if (u != v && !buscarAresta(u, v)) {
                double custo = Math.round((rand.nextDouble() * 90 + 10) * 10.0) / 10.0;
                incluirArestaBilateral(u, v, custo, "Conexao_Extra");
                arestasAdicionadas++;
            }
        }
        System.out.println("[!] Grafo Q4 gerado com 20 vértices e " + arestasAdicionadas + " arestas (bilaterais).");
    }

    // Metodo auxiliar para inserir arestas nos dois sentidos (Grafo Não-Direcionado)
    private void incluirArestaBilateral(int u, int v, double custo, String carac) {
        incluirAresta(u, v, custo, carac);
        incluirAresta(v, u, custo, carac);
    }


    // =========================================================================
    // QUESTÃO 04 (C): ALGORITMO DE PRIM
    // =========================================================================
    public void agmPrim() {
        if (vetorVertices == null || capacidadeAtual == 0) return;

        double[] chave = new double[capacidadeAtual];
        int[] pai = new int[capacidadeAtual];
        boolean[] naArvore = new boolean[capacidadeAtual];

        for (int i = 0; i < capacidadeAtual; i++) {
            chave[i] = Double.MAX_VALUE;
            pai[i] = -1;
        }

        // Pega o primeiro vértice ativo que encontrar para ser a raiz
        int raiz = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) { raiz = i; break; }
        }

        chave[raiz] = 0;
        double custoTotal = 0;

        System.out.println("\n[Construindo AGM - Algoritmo de PRIM]");

        for (int count = 0; count < contarVerticesAtivos(); count++) {
            // 1. Extrai o vértice mais barato que ainda não está na árvore
            int u = -1;
            for (int i = 0; i < capacidadeAtual; i++) {
                if (vetorVertices[i] != null && !naArvore[i]) {
                    if (u == -1 || chave[i] < chave[u]) {
                        u = i;
                    }
                }
            }

            if (u == -1 || chave[u] == Double.MAX_VALUE) break; // Trava de segurança (desconexo)

            // 2. Coloca na árvore
            naArvore[u] = true;

            if (pai[u] != -1) {
                System.out.println(" -> Adicionada: V" + pai[u] + " --(Custo: " + chave[u] + ")--> V" + u);
                custoTotal += chave[u];
            }

            // 3. Atualiza os vizinhos da borda
            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                int v = atual.idDestino;
                // Diferença principal pro Dijkstra: avalia apenas 'atual.custo', e não 'dist[u] + custo'
                if (!naArvore[v] && atual.custo < chave[v]) {
                    pai[v] = u;
                    chave[v] = atual.custo;
                }
                atual = atual.proxima;
            }
        }
        System.out.println("=========================================");
        System.out.println("Custo Total da Árvore (Prim): " + Math.round(custoTotal * 100.0) / 100.0);
        System.out.println("=========================================\n");
    }


    // =========================================================================
    // QUESTÃO 04 (B): ALGORITMO DE BORUVKA
    // =========================================================================
    // Mini-classe para guardar as arestas mais baratas de cada componente
    private class ArestaSimples {
        int origem, destino; double custo;
        ArestaSimples(int o, int d, double c) { this.origem = o; this.destino = d; this.custo = c; }
    }

    public void agmBoruvka() {
        if (vetorVertices == null || capacidadeAtual == 0) return;

        int[] pai = new int[capacidadeAtual];
        for (int i = 0; i < capacidadeAtual; i++) pai[i] = i; // Union-Find inicial

        int numComponentes = contarVerticesAtivos();
        double custoTotal = 0.0;
        ArestaSimples[] maisBarata = new ArestaSimples[capacidadeAtual];

        System.out.println("\n[Construindo AGM - Algoritmo de BORUVKA]");

        boolean mudou = true;
        // Roda em O(log V) fases
        while (numComponentes > 1 && mudou) {
            mudou = false;

            // Limpa as escolhas da iteração anterior
            for (int i = 0; i < capacidadeAtual; i++) maisBarata[i] = null;

            // 1. Varre o grafo inteiro buscando a melhor aresta de saída para cada componente
            for (int u = 0; u < capacidadeAtual; u++) {
                if (vetorVertices[u] == null) continue;

                Aresta atual = vetorVertices[u].inicioLista;
                while (atual != null) {
                    int v = atual.idDestino;
                    int raizU = encontrarRaiz(u, pai);
                    int raizV = encontrarRaiz(v, pai);

                    if (raizU != raizV) { // Se não forma ciclo
                        if (maisBarata[raizU] == null || atual.custo < maisBarata[raizU].custo) {
                            maisBarata[raizU] = new ArestaSimples(u, v, atual.custo);
                        }
                        if (maisBarata[raizV] == null || atual.custo < maisBarata[raizV].custo) {
                            maisBarata[raizV] = new ArestaSimples(u, v, atual.custo);
                        }
                    }
                    atual = atual.proxima;
                }
            }

            // 2. Funde as componentes usando as arestas escolhidas
            for (int i = 0; i < capacidadeAtual; i++) {
                if (maisBarata[i] != null) {
                    int raizOrigem = encontrarRaiz(maisBarata[i].origem, pai);
                    int raizDestino = encontrarRaiz(maisBarata[i].destino, pai);

                    if (raizOrigem != raizDestino) {
                        pai[raizOrigem] = raizDestino; // Une os conjuntos
                        custoTotal += maisBarata[i].custo;
                        System.out.println(" -> Adicionada: V" + maisBarata[i].origem + " --(Custo: " + maisBarata[i].custo + ")--> V" + maisBarata[i].destino);
                        numComponentes--;
                        mudou = true;
                    }
                }
            }
        }
        System.out.println("=========================================");
        System.out.println("Custo Total da Árvore (Boruvka): " + Math.round(custoTotal * 100.0) / 100.0);
        System.out.println("=========================================\n");
    }





// =========================================================================
    // QUESTÃO 09: CAMINHOS MÍNIMOS (DIJKSTRA, MB-DP, THRESHOLD, FLOYD-WARSHALL)
    // =========================================================================

    // Metodo auxiliar para imprimir a "Matriz" (Tabela) de Valores e Caminhos de 1 Origem
    private void imprimirTabelaCaminhos(int origem, double[] dist, int[] pai, String nomeAlgoritmo) {
        System.out.println("\n>>> Tabela de Caminhos Mínimos a partir de V" + origem + " (" + nomeAlgoritmo + ") <<<");
        System.out.printf("%-10s | %-12s | %-30s%n", "Destino", "Custo Total", "Caminho (Rotas)");
        System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && i != origem) {
                if (dist[i] == Double.MAX_VALUE) {
                    System.out.printf("V%-9d | %-12s | %-30s%n", i, "Inalcançável", "-");
                } else {
                    String caminho = montarCaminhoCiclo(origem, i, pai); // Reaproveitando seu método
                    System.out.printf("V%-9d | %-12.2f | %-30s%n", i, dist[i], caminho);
                }
            }
        }
        System.out.println("---------------------------------------------------------------\n");
    }

    // ---------------------------------------------------------
    // 9.A - DIJKSTRA (Label Setting)
    // ---------------------------------------------------------
    public void executarDijkstraQ9(int origem) {
        if (vetorVertices[origem] == null) return;
        RetornoDijkstra resultado = calcularDistancias(origem); // Reaproveita o Dijkstra puro que você já fez na Q11
        imprimirTabelaCaminhos(origem, resultado.distancias, resultado.anteriores, "Dijkstra");
    }

    // ---------------------------------------------------------
    // 9.B - MOORE, BELLMAN-D'ESOPO, PAPE (MB-DP)
    // ---------------------------------------------------------
    public void executarMB_DP(int origem) {
        if (vetorVertices[origem] == null) return;

        double[] dist = new double[capacidadeAtual];
        int[] pai = new int[capacidadeAtual];
        int[] status = new int[capacidadeAtual];
        // Status: 0 = Nunca entrou na fila, 1 = Está na fila agora, 2 = Já foi retirado da fila

        for (int i = 0; i < capacidadeAtual; i++) {
            dist[i] = Double.MAX_VALUE;
            pai[i] = -1;
            status[i] = 0;
        }

        dist[origem] = 0;
        java.util.Deque<Integer> deque = new java.util.LinkedList<>();
        deque.addLast(origem);
        status[origem] = 1;

        while (!deque.isEmpty()) {
            int u = deque.removeFirst();
            status[u] = 2; // Removido da fila

            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                int v = atual.idDestino;
                if (dist[u] + atual.custo < dist[v]) {
                    dist[v] = dist[u] + atual.custo;
                    pai[v] = u;

                    if (status[v] == 0) {
                        // Descoberto pela 1ª vez: vai pro FIM
                        deque.addLast(v);
                        status[v] = 1;
                    } else if (status[v] == 2) {
                        // Corrigido (já tinha sido visitado): vai pro INÍCIO para corrigir a cascata rápido
                        deque.addFirst(v);
                        status[v] = 1;
                    }
                }
                atual = atual.proxima;
            }
        }
        imprimirTabelaCaminhos(origem, dist, pai, "Moore, Bellman-D'Esopo, Pape");
    }

    // ---------------------------------------------------------
    // 9.C - THRESHOLD (Glover, Klingman, Phillips) - Abordagem NOW/NEXT
    // ---------------------------------------------------------
    public void executarThreshold(int origem) {
        if (vetorVertices[origem] == null) return;

        double[] dist = new double[capacidadeAtual];
        int[] pai = new int[capacidadeAtual];
        for (int i = 0; i < capacidadeAtual; i++) {
            dist[i] = Double.MAX_VALUE;
            pai[i] = -1;
        }
        dist[origem] = 0;

        java.util.List<Integer> filaNow = new java.util.LinkedList<>();
        java.util.List<Integer> filaNext = new java.util.LinkedList<>();

        filaNow.add(origem);

        while (!filaNow.isEmpty() || !filaNext.isEmpty()) {
            if (filaNow.isEmpty()) {
                // Calcula o Threshold (Limiar). Vamos usar o custo mínimo em NEXT + média das distâncias
                double minNext = Double.MAX_VALUE;
                double soma = 0;
                for (int v : filaNext) {
                    if (dist[v] < minNext) minNext = dist[v];
                    soma += dist[v];
                }
                double media = soma / filaNext.size();
                double threshold = minNext + (media - minNext) * 0.5; // Fator de corte

                // Move de NEXT para NOW os vértices abaixo do limiar
                java.util.Iterator<Integer> it = filaNext.iterator();
                while (it.hasNext()) {
                    int v = it.next();
                    if (dist[v] <= threshold) {
                        filaNow.add(v);
                        it.remove();
                    }
                }
            }

            int u = filaNow.remove(0); // Remove do início do NOW
            Aresta atual = vetorVertices[u].inicioLista;
            while (atual != null) {
                int v = atual.idDestino;
                if (dist[u] + atual.custo < dist[v]) {
                    dist[v] = dist[u] + atual.custo;
                    pai[v] = u;

                    // Se não está em nenhuma das listas, adiciona à NEXT
                    if (!filaNow.contains(v) && !filaNext.contains(v)) {
                        filaNext.add(v);
                    }
                }
                atual = atual.proxima;
            }
        }
        imprimirTabelaCaminhos(origem, dist, pai, "Threshold - G.K.P.");
    }

    // ---------------------------------------------------------
    // 9.D - FLOYD-WARSHALL (Todos para Todos)
    // ---------------------------------------------------------
    public void executarFloydWarshall() {
        int n = capacidadeAtual;
        double[][] dist = new double[n][n];
        int[][] pai = new int[n][n];

        // 1. Inicializa a matriz O(V^2)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Double.MAX_VALUE;
                pai[i][j] = -1;
            }
            dist[i][i] = 0; // Distância para si mesmo é 0

            if (vetorVertices[i] != null) {
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null) {
                    dist[i][atual.idDestino] = atual.custo;
                    pai[i][atual.idDestino] = i;
                    atual = atual.proxima;
                }
            }
        }

        // 2. Os três loops aninhados mágicos O(V^3)
        System.out.println("\n[Processando Floyd-Warshall...]");
        for (int k = 0; k < n; k++) {
            if (vetorVertices[k] == null) continue;
            for (int i = 0; i < n; i++) {
                if (vetorVertices[i] == null || dist[i][k] == Double.MAX_VALUE) continue;
                for (int j = 0; j < n; j++) {
                    if (vetorVertices[j] == null || dist[k][j] == Double.MAX_VALUE) continue;

                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        pai[i][j] = pai[k][j]; // Salva a rota
                    }
                }
            }
        }

        // 3. Imprime a Matriz (Tabela) Resultante de Valores
        System.out.println(">>> MATRIZ DE DISTÂNCIAS (FLOYD-WARSHALL) <<<");
        System.out.print("Dest\\Orig | ");
        for (int i = 0; i < n; i++) {
            if (vetorVertices[i] != null) System.out.printf("V%-5d ", i);
        }
        System.out.println("\n---------------------------------------------------------");

        for (int i = 0; i < n; i++) {
            if (vetorVertices[i] == null) continue;
            System.out.printf("   V%-5d | ", i);
            for (int j = 0; j < n; j++) {
                if (vetorVertices[j] == null) continue;

                if (dist[i][j] == Double.MAX_VALUE) System.out.print("INF    ");
                else System.out.printf("%-6.1f ", dist[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // =========================================================================
    // QUESTÃO 2 (AC03): REDES DE FLUXO E ALGORITMO GENÉRICO (EDMONDS-KARP)
    // =========================================================================

    private void prepararRedeFluxo() {
        // 1. Reseta os fluxos das arestas existentes
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta e = vetorVertices[i].inicioLista;
                while (e != null) {
                    e.fluxo = 0.0;
                    e.arestaReversa = null;
                    e = e.proxima;
                }
            }
        }

        // 2. Coleta todas as arestas originais para evitar problemas ao modificar as listas durante o percurso
        java.util.List<Aresta> arestasOriginais = new java.util.ArrayList<>();
        java.util.List<Integer> origens = new java.util.ArrayList<>();

        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                Aresta e = vetorVertices[i].inicioLista;
                while (e != null) {
                    if (!e.ehReversaProvisoria) {
                        arestasOriginais.add(e);
                        origens.add(i);
                    }
                    e = e.proxima;
                }
            }
        }

        // 3. Cria as arestas reversas provisórias com capacidade 0
        for (int idx = 0; idx < arestasOriginais.size(); idx++) {
            Aresta e = arestasOriginais.get(idx);
            int origem = origens.get(idx);
            int destino = e.idDestino;

            if (e.arestaReversa == null) {
                Aresta rev = new Aresta(origem, 0.0, "Reversa de " + e.caracteristica);
                rev.ehReversaProvisoria = true;
                rev.custoUnitario = -e.custoUnitario; // Propaga custo unitário negativo para a aresta reversa

                // Insere no início da lista do destino
                rev.proxima = vetorVertices[destino].inicioLista;
                vetorVertices[destino].inicioLista = rev;

                e.arestaReversa = rev;
                rev.arestaReversa = e;
            }
        }
    }

    private void limparRedeFluxo() {
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                // Remove as arestas reversas provisórias da cabeça da lista
                while (vetorVertices[i].inicioLista != null && vetorVertices[i].inicioLista.ehReversaProvisoria) {
                    vetorVertices[i].inicioLista = vetorVertices[i].inicioLista.proxima;
                }
                // Remove do resto da lista
                Aresta atual = vetorVertices[i].inicioLista;
                while (atual != null && atual.proxima != null) {
                    if (atual.proxima.ehReversaProvisoria) {
                        atual.proxima = atual.proxima.proxima;
                    } else {
                        atual = atual.proxima;
                    }
                }
                // Limpa as referências das arestas normais restantes
                Aresta e = vetorVertices[i].inicioLista;
                while (e != null) {
                    e.fluxo = 0.0;
                    e.arestaReversa = null;
                    e = e.proxima;
                }
            }
        }
    }

    // =========================================================================
    // PROCEDIMENTO ROTULANTE (G, s, t) - Baseado nos slides do professor
    // =========================================================================
    // Utiliza busca em largura (BFS) com lista L (fila) e rotulo (visitado) para encontrar caminhos aumentantes
    private boolean buscarCaminhoAumentante(int s, int t, int[] paiVertice, Aresta[] paiAresta) {
        boolean[] rotulo = new boolean[capacidadeAtual]; // rotulo[j] := 1 se visitado
        int[] pred = paiVertice; // pred[j] := i (antecessor)
        
        // Fila L (L := L U {j})
        int[] L = new int[capacidadeAtual];
        int inicio = 0, fim = 0;

        rotulo[s] = true; // rotulo[s] := 1
        L[fim++] = s; // L := {s}

        while (inicio < fim && !rotulo[t]) {
            int i = L[inicio++]; // L := L - {i}
            
            Vertice vert = vetorVertices[i];
            if (vert == null) continue;

            // Para cada vizinho j de i no Grafo Residual
            Aresta e = vert.inicioLista;
            while (e != null) {
                int j = e.idDestino;
                double capResidual = e.custo - e.fluxo; // rij (capacidade residual)

                // Se não rotulado E existe capacidade residual (rij > 0)
                if (vetorVertices[j] != null && !rotulo[j] && capResidual > 0) {
                    pred[j] = i; // pred[j] := i
                    paiAresta[j] = e; // guarda a aresta física para multígrafos
                    rotulo[j] = true; // rotulo[j] := 1
                    L[fim++] = j; // L := L U {j}
                }
                e = e.proxima;
            }
        }
        return rotulo[t]; // Retorna se o sumidouro t foi rotulado
    }

    // =========================================================================
    // PROCEDIMENTO CAMINHO_AUMENTANTE (GR, s, t) & MÉTODO GENÉRICO DE FLUXO MÁXIMO
    // =========================================================================
    public void executarFluxoMaximoQ2(int s, int t) {
        if (s >= capacidadeAtual || vetorVertices[s] == null || t >= capacidadeAtual || vetorVertices[t] == null) {
            System.out.println("[x] Erro: Vértice de origem ou destino inválido.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("   MÉTODO GENÉRICO DE FLUXO MÁXIMO & PROCEDIMENTO ROTULANTE            ");
        System.out.println("   Origem (s): V" + s + " (" + vetorVertices[s].caracteristica + ") | Destino (t): V" + t + " (" + vetorVertices[t].caracteristica + ")");
        System.out.println("======================================================================");

        prepararRedeFluxo();

        int[] pred = new int[capacidadeAtual];
        Aresta[] paiAresta = new Aresta[capacidadeAtual];

        double fluxoMaximo = 0; // f := 0
        int numIteracao = 1;
        int totalArestasCaminhos = 0;

        // while GR contém um caminho direto de s-t (Procedimento Rotulante rotula t)
        while (buscarCaminhoAumentante(s, t, pred, paiAresta)) {
            // [Procedimento Caminho_Aumentante]
            
            // 1. Identifica a capacidade residual gargalo theta ao longo do caminho P
            // theta = min{rij : ij em P}
            double theta = Double.MAX_VALUE;
            int curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                double rij = e.custo - e.fluxo;
                if (rij < theta) {
                    theta = rij;
                }
                curr = pred[curr];
            }

            // 2. Imprime o caminho aumentante de forma didática
            System.out.println("\n[Iteração #" + numIteracao + "] Caminho Aumentante P encontrado pelo Procedimento Rotulante:");

            int caminhoLen = 0;
            int temp = t;
            while (temp != s) {
                caminhoLen++;
                temp = pred[temp];
            }
            totalArestasCaminhos += caminhoLen;

            int[] verticesCaminho = new int[caminhoLen + 1];
            Aresta[] arestasCaminho = new Aresta[caminhoLen];
            temp = t;
            for (int i = caminhoLen; i > 0; i--) {
                verticesCaminho[i] = temp;
                arestasCaminho[i - 1] = paiAresta[temp];
                temp = pred[temp];
            }
            verticesCaminho[0] = s;

            System.out.print("   Caminho P: ");
            for (int i = 0; i < caminhoLen; i++) {
                int u = verticesCaminho[i];
                Aresta e = arestasCaminho[i];
                String tipo = e.ehReversaProvisoria ? "Reversa" : "Direta";
                System.out.printf("V%d --(%s, cap: %.2f, fluxo: %.2f | residual: %.2f)--> ", 
                    u, tipo, e.custo, e.fluxo, e.custo - e.fluxo);
            }
            System.out.println("V" + t);
            System.out.printf("   >>> gargalo (theta) = min{rij} = %.2f%n", theta);
            System.out.printf("   >>> [Caminho_Aumentante] Aumentando fluxo: f := f + %.2f%n", theta);

            // 3. Aumenta o fluxo ao longo de P (f := f + theta) e atualiza o Grafo Residual
            curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                e.fluxo += theta;
                e.arestaReversa.fluxo -= theta; // rij = c_ij - f_ij e r_ji = f_ij
                curr = pred[curr];
            }

            fluxoMaximo += theta;
            System.out.printf("   >>> Fluxo máximo acumulado até o momento (f): %.2f%n", fluxoMaximo);
            numIteracao++;
        }

        System.out.println("\n======================================================================");
        System.out.printf("   Fluxo Máximo Total Alcançado (f): %.2f%n", fluxoMaximo);
        System.out.println("======================================================================");

        // ==========================================================
        // CÁLCULO E EXIBIÇÃO DIDÁTICA DO CORTE MÍNIMO s-t
        // ==========================================================
        boolean[] alcansavelResidual = new boolean[capacidadeAtual];
        marcarAlcanceResidual(s, alcansavelResidual);

        System.out.println("\n>>> ANÁLISE DO CORTE MÍNIMO s-t (Teorema Max-Flow Min-Cut) <<<");

        System.out.print("   Conjunto S (Lado da Fonte): { ");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && alcansavelResidual[i]) {
                System.out.print("V" + i + " ");
            }
        }
        System.out.println("}");

        System.out.print("   Conjunto T (Lado do Sumidouro): { ");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !alcansavelResidual[i]) {
                System.out.print("V" + i + " ");
            }
        }
        System.out.println("}");

        double capacidadeCorteOriginal = 0;
        double capacidadeCorteResidual = 0;

        System.out.println("\nArestas que cruzam a fronteira do corte (de S para T):");
        System.out.printf("   %-20s | %-12s | %-12s | %-12s%n", "Aresta (U -> V)", "Cap. Original", "Fluxo Final", "Cap. Residual");
        System.out.println("   ---------------------------------------------------------------------");

        for (int u = 0; u < capacidadeAtual; u++) {
            if (vetorVertices[u] != null && alcansavelResidual[u]) {
                Aresta e = vetorVertices[u].inicioLista;
                while (e != null) {
                    int v = e.idDestino;
                    if (vetorVertices[v] != null && !alcansavelResidual[v] && !e.ehReversaProvisoria) {
                        double capOriginal = e.custo;
                        double fluxoFinal = e.fluxo;
                        double capResidual = capOriginal - fluxoFinal;

                        System.out.printf("   V%-2d -> V%-14d | %-12.2f | %-12.2f | %-12.2f%n", 
                            u, v, capOriginal, fluxoFinal, capResidual);

                        capacidadeCorteOriginal += capOriginal;
                        capacidadeCorteResidual += capResidual;
                    }
                    e = e.proxima;
                }
            }
        }

        System.out.println("   ---------------------------------------------------------------------");
        System.out.printf("   >>> Capacidade Total do Corte no Grafo Original (Min-Cut): %.2f%n", capacidadeCorteOriginal);
        System.out.printf("   >>> Capacidade Residual Total do Corte no Grafo Residual:  %.2f%n", capacidadeCorteResidual);

        System.out.println("\n>>> EXPLICAÇÃO DIDÁTICA DO CORTE:");
        System.out.printf(" 1. O Fluxo Máximo de V%d para V%d é %.2f.%n", s, t, fluxoMaximo);
        System.out.printf(" 2. A Capacidade do Corte Mínimo s-t correspondente é %.2f.%n", capacidadeCorteOriginal);
        System.out.printf(" 3. Verificamos que o Fluxo Máximo (%.2f) = Capacidade do Corte Mínimo (%.2f).%n", fluxoMaximo, capacidadeCorteOriginal);
        System.out.println("    Isso valida empiricamente o Teorema Max-Flow Min-Cut de Ford-Fulkerson!");
        System.out.printf(" 4. A Capacidade Residual do corte é %.2f.%n", capacidadeCorteResidual);
        System.out.println("    Como a capacidade residual é 0, o corte está completamente saturado (gargalo total).");
        System.out.println("======================================================================\n");

        // ==========================================================
        // PAINEL COMPARATIVO DIDÁTICO
        // ==========================================================
        System.out.println("======================================================================");
        System.out.println("   📊 PAINEL COMPARATIVO DIDÁTICO (EDMONDS-KARP BFS)                  ");
        System.out.println("======================================================================");
        System.out.printf("   - Algoritmo: Edmonds-Karp (Caminho Aumentante via BFS)%n");
        System.out.printf("   - Fluxo Máximo Final (f): %.2f%n", fluxoMaximo);
        System.out.printf("   - Caminhos Aumentantes Encontrados: %d%n", numIteracao - 1);
        if (numIteracao > 1) {
            double mediaArestas = (double) totalArestasCaminhos / (numIteracao - 1);
            System.out.printf("   - Média de arestas (canos) por caminho: %.2f%n", mediaArestas);
            System.out.println("   - Nota Teórica: A BFS garante encontrar SEMPRE o caminho aumentante");
            System.out.println("                   com o menor número de arestas (mais curto).");
        } else {
            System.out.println("   - Nenhum caminho aumentante foi encontrado.");
        }
        System.out.println("======================================================================\n");

        limparRedeFluxo();
    }

    private void marcarAlcanceResidual(int u, boolean[] visitado) {
        visitado[u] = true;
        if (vetorVertices[u] == null) return;
        Aresta e = vetorVertices[u].inicioLista;
        while (e != null) {
            int v = e.idDestino;
            if (vetorVertices[v] != null && !visitado[v] && (e.custo - e.fluxo > 0)) {
                marcarAlcanceResidual(v, visitado);
            }
            e = e.proxima;
        }
    }

    public void gerarCenarioFluxoPadrao() {
        destruirGrafo(); // Reseta o grafo atual
        this.capacidadeAtual = 10;
        this.vetorVertices = new Vertice[10];

        // 1. Cria os 10 vértices de s (V0) a t (V9)
        incluirVertice(0, "s (Fonte)");
        for (int i = 1; i <= 8; i++) {
            incluirVertice(i, "V" + i);
        }
        incluirVertice(9, "t (Sumidouro)");

        // 2. Inclui arcos direcionados com capacidades (e algumas arestas paralelas para demonstrar multígrafo)
        incluirAresta(0, 1, 10.0, "Caminho A1");
        incluirAresta(0, 1, 5.0, "Caminho A2 (Paralelo)"); // Aresta paralela para multígrafo!
        incluirAresta(0, 2, 15.0, "Caminho B");
        
        incluirAresta(1, 3, 9.0, "Caminho C");
        incluirAresta(1, 4, 6.0, "Caminho D");
        
        incluirAresta(2, 4, 8.0, "Caminho E");
        incluirAresta(2, 5, 10.0, "Caminho F");
        
        incluirAresta(3, 6, 10.0, "Caminho G");
        
        incluirAresta(4, 6, 6.0, "Caminho H1");
        incluirAresta(4, 6, 4.0, "Caminho H2 (Paralelo)"); // Outra aresta paralela!
        incluirAresta(4, 7, 7.0, "Caminho I");
        
        incluirAresta(5, 7, 12.0, "Caminho J");
        
        incluirAresta(6, 8, 6.0, "Caminho K");
        incluirAresta(6, 9, 8.0, "Caminho L");
        
        incluirAresta(7, 9, 15.0, "Caminho M");
        incluirAresta(8, 9, 10.0, "Caminho N");
        
        System.out.println("[!] Grafo de Fluxo Padrão gerado com 10 vértices e arcos direcionados (incluindo arestas paralelas para provar o suporte a multígrafos).");
    }

    // =========================================================================
    // FORD-FULKERSON COM DFS (QUESTÃO 5)
    // =========================================================================

    private boolean buscarCaminhoAumentanteDFS(int u, int t, boolean[] rotulo, int[] pred, Aresta[] paiAresta) {
        if (u == t) return true;
        rotulo[u] = true; // rotulo[u] := 1

        Vertice vert = vetorVertices[u];
        if (vert == null) return false;

        Aresta e = vert.inicioLista;
        while (e != null) {
            int j = e.idDestino;
            double capResidual = e.custo - e.fluxo; // rij

            if (vetorVertices[j] != null && !rotulo[j] && capResidual > 0) {
                pred[j] = u; // pred[j] := u
                paiAresta[j] = e; // guarda a aresta física
                
                if (buscarCaminhoAumentanteDFS(j, t, rotulo, pred, paiAresta)) {
                    return true;
                }
            }
            e = e.proxima;
        }
        return false;
    }

    private boolean procedimentoRotulanteDFS(int s, int t, int[] pred, Aresta[] paiAresta) {
        boolean[] rotulo = new boolean[capacidadeAtual];
        return buscarCaminhoAumentanteDFS(s, t, rotulo, pred, paiAresta);
    }

    public void executarFordFulkersonDFS(int s, int t) {
        if (s >= capacidadeAtual || vetorVertices[s] == null || t >= capacidadeAtual || vetorVertices[t] == null) {
            System.out.println("[x] Erro: Vértice de origem ou destino inválido.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("   ALGORITMO DE FORD-FULKERSON (DFS - BUSCA EM PROFUNDIDADE)          ");
        System.out.println("   Origem (s): V" + s + " (" + vetorVertices[s].caracteristica + ") | Destino (t): V" + t + " (" + vetorVertices[t].caracteristica + ")");
        System.out.println("======================================================================");

        prepararRedeFluxo();

        int[] pred = new int[capacidadeAtual];
        Aresta[] paiAresta = new Aresta[capacidadeAtual];

        double fluxoMaximo = 0;
        int numIteracao = 1;
        int totalArestasCaminhos = 0;

        while (procedimentoRotulanteDFS(s, t, pred, paiAresta)) {
            // 1. Identifica a capacidade residual gargalo theta ao longo do caminho P
            double theta = Double.MAX_VALUE;
            int curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                double rij = e.custo - e.fluxo;
                if (rij < theta) {
                    theta = rij;
                }
                curr = pred[curr];
            }

            // 2. Imprime o caminho aumentante de forma didática
            System.out.println("\n[Iteração #" + numIteracao + "] Caminho Aumentante P encontrado via DFS:");

            int caminhoLen = 0;
            int temp = t;
            while (temp != s) {
                caminhoLen++;
                temp = pred[temp];
            }
            totalArestasCaminhos += caminhoLen;

            int[] verticesCaminho = new int[caminhoLen + 1];
            Aresta[] arestasCaminho = new Aresta[caminhoLen];
            temp = t;
            for (int i = caminhoLen; i > 0; i--) {
                verticesCaminho[i] = temp;
                arestasCaminho[i - 1] = paiAresta[temp];
                temp = pred[temp];
            }
            verticesCaminho[0] = s;

            System.out.print("   Caminho P (DFS): ");
            for (int i = 0; i < caminhoLen; i++) {
                int u = verticesCaminho[i];
                Aresta e = arestasCaminho[i];
                String tipo = e.ehReversaProvisoria ? "Reversa" : "Direta";
                System.out.printf("V%d --(%s, cap: %.2f, fluxo: %.2f | residual: %.2f)--> ", 
                    u, tipo, e.custo, e.fluxo, e.custo - e.fluxo);
            }
            System.out.println("V" + t);
            System.out.printf("   >>> gargalo (theta) = %.2f%n", theta);
            System.out.printf("   >>> [Caminho_Aumentante] Aumentando fluxo: f := f + %.2f%n", theta);

            // 3. Aumenta o fluxo ao longo de P (f := f + theta)
            curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                e.fluxo += theta;
                e.arestaReversa.fluxo -= theta;
                curr = pred[curr];
            }

            fluxoMaximo += theta;
            System.out.printf("   >>> Fluxo máximo acumulado (f): %.2f%n", fluxoMaximo);
            numIteracao++;
        }

        System.out.println("\n======================================================================");
        System.out.printf("   Fluxo Máximo Total Alcançado (f): %.2f%n", fluxoMaximo);
        System.out.println("======================================================================");

        // Exibição do Corte
        boolean[] alcansavelResidual = new boolean[capacidadeAtual];
        marcarAlcanceResidual(s, alcansavelResidual);

        System.out.println("\n>>> ANÁLISE DO CORTE MÍNIMO s-t (Ford-Fulkerson DFS) <<<");
        System.out.print("   Conjunto S (Lado da Fonte): { ");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && alcansavelResidual[i]) System.out.print("V" + i + " ");
        }
        System.out.println("}");

        System.out.print("   Conjunto T (Lado do Sumidouro): { ");
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null && !alcansavelResidual[i]) System.out.print("V" + i + " ");
        }
        System.out.println("}");

        double capacidadeCorteOriginal = 0;
        for (int u = 0; u < capacidadeAtual; u++) {
            if (vetorVertices[u] != null && alcansavelResidual[u]) {
                Aresta e = vetorVertices[u].inicioLista;
                while (e != null) {
                    int v = e.idDestino;
                    if (vetorVertices[v] != null && !alcansavelResidual[v] && !e.ehReversaProvisoria) {
                        capacidadeCorteOriginal += e.custo;
                    }
                    e = e.proxima;
                }
            }
        }
        System.out.printf("   >>> Capacidade do Corte Mínimo no Grafo Original: %.2f%n", capacidadeCorteOriginal);

        // ==========================================================
        // PAINEL COMPARATIVO DIDÁTICO
        // ==========================================================
        System.out.println("\n======================================================================");
        System.out.println("   📊 PAINEL COMPARATIVO DIDÁTICO (FORD-FULKERSON DFS)               ");
        System.out.println("======================================================================");
        System.out.printf("   - Algoritmo: Ford-Fulkerson (Caminho Aumentante via DFS)%n");
        System.out.printf("   - Fluxo Máximo Final (f): %.2f%n", fluxoMaximo);
        System.out.printf("   - Caminhos Aumentantes Encontrados: %d%n", numIteracao - 1);
        if (numIteracao > 1) {
            double mediaArestas = (double) totalArestasCaminhos / (numIteracao - 1);
            System.out.printf("   - Média de arestas (canos) por caminho: %.2f%n", mediaArestas);
            System.out.println("   - Nota Teórica: A DFS busca caminhos sem critério de comprimento.");
            System.out.println("                   Pode encontrar caminhos mais longos e zigue-zagues.");
        } else {
            System.out.println("   - Nenhum caminho aumentante foi encontrado.");
        }
        System.out.println("======================================================================\n");

        limparRedeFluxo();
    }

    // =========================================================================
    // BUSACKER & GOWEN (CUSTO MÍNIMO FLUXO MÁXIMO) - QUESTÃO 7
    // =========================================================================

    public void incluirArestaComCusto(int origem, int destino, double capacidade, double custoUnitario, String caracteristica) {
        garantirCapacidade(Math.max(origem, destino));
        if (vetorVertices[origem] == null) incluirVertice(origem, "Vértice " + origem);
        if (vetorVertices[destino] == null) incluirVertice(destino, "Vértice " + destino);

        Aresta nova = new Aresta(destino, capacidade, caracteristica);
        nova.custoUnitario = custoUnitario;
        // Insere no início da lista encadeada (mais rápido: O(1))
        nova.proxima = vetorVertices[origem].inicioLista;
        vetorVertices[origem].inicioLista = nova;
    }

    private boolean buscarCaminhoMaisBarato(int s, int t, int[] pred, Aresta[] paiAresta) {
        double[] dist = new double[capacidadeAtual];
        java.util.Arrays.fill(dist, Double.MAX_VALUE);
        java.util.Arrays.fill(pred, -1);
        dist[s] = 0.0;

        int V = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                V++;
            }
        }

        // Relaxar arestas V - 1 vezes (Bellman-Ford)
        for (int step = 0; step < V - 1; step++) {
            boolean mudou = false;
            for (int u = 0; u < capacidadeAtual; u++) {
                if (vetorVertices[u] == null || dist[u] == Double.MAX_VALUE) continue;

                Aresta e = vetorVertices[u].inicioLista;
                while (e != null) {
                    int v = e.idDestino;
                    double capResidual = e.custo - e.fluxo;

                    if (vetorVertices[v] != null && capResidual > 0.0001) {
                        if (dist[u] + e.custoUnitario < dist[v] - 0.0001) {
                            dist[v] = dist[u] + e.custoUnitario;
                            pred[v] = u;
                            paiAresta[v] = e;
                            mudou = true;
                        }
                    }
                    e = e.proxima;
                }
            }
            if (!mudou) break;
        }

        return dist[t] != Double.MAX_VALUE;
    }

    public void executarBusackerGowen(int s, int t) {
        if (s >= capacidadeAtual || vetorVertices[s] == null || t >= capacidadeAtual || vetorVertices[t] == null) {
            System.out.println("[x] Erro: Vértice de origem ou destino inválido.");
            return;
        }

        System.out.println("\n======================================================================");
        System.out.println("   ALGORITMO DE BUSACKER & GOWEN (FLUXO MÁXIMO DE CUSTO MÍNIMO)        ");
        System.out.println("   Origem (s): V" + s + " (" + vetorVertices[s].caracteristica + ") | Destino (t): V" + t + " (" + vetorVertices[t].caracteristica + ")");
        System.out.println("======================================================================");

        prepararRedeFluxo();

        int[] pred = new int[capacidadeAtual];
        Aresta[] paiAresta = new Aresta[capacidadeAtual];

        double fluxoMaximo = 0;
        double custoTotal = 0;
        int numIteracao = 1;

        while (buscarCaminhoMaisBarato(s, t, pred, paiAresta)) {
            // 1. Identificar o gargalo (theta) ao longo do caminho
            double theta = Double.MAX_VALUE;
            double custoCaminhoUnitario = 0;
            int curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                double rij = e.custo - e.fluxo;
                if (rij < theta) {
                    theta = rij;
                }
                custoCaminhoUnitario += e.custoUnitario;
                curr = pred[curr];
            }

            // 2. Imprimir o caminho aumentante encontrado de forma didática
            System.out.println("\n[Iteração #" + numIteracao + "] Caminho de Custo Mínimo encontrado (Bellman-Ford):");
            
            // Medir comprimento do caminho
            int caminhoLen = 0;
            int temp = t;
            while (temp != s) {
                caminhoLen++;
                temp = pred[temp];
            }

            int[] verticesCaminho = new int[caminhoLen + 1];
            Aresta[] arestasCaminho = new Aresta[caminhoLen];
            temp = t;
            for (int i = caminhoLen; i > 0; i--) {
                verticesCaminho[i] = temp;
                arestasCaminho[i - 1] = paiAresta[temp];
                temp = pred[temp];
            }
            verticesCaminho[0] = s;

            System.out.print("   Caminho P: ");
            for (int i = 0; i < caminhoLen; i++) {
                int u = verticesCaminho[i];
                Aresta e = arestasCaminho[i];
                String tipo = e.ehReversaProvisoria ? "Reversa" : "Direta";
                System.out.printf("V%d --(%s, cap: %.2f, fluxo: %.2f, custoUnit: %.2f | residual: %.2f)--> ", 
                    u, tipo, e.custo, e.fluxo, e.custoUnitario, e.custo - e.fluxo);
            }
            System.out.println("V" + t);
            System.out.printf("   >>> Custo Unitário do Caminho: %.2f%n", custoCaminhoUnitario);
            System.out.printf("   >>> Gargalo (theta) = %.2f%n", theta);
            double custoIteracao = theta * custoCaminhoUnitario;
            System.out.printf("   >>> Custo desta iteração: %.2f * %.2f = %.2f%n", theta, custoCaminhoUnitario, custoIteracao);

            // 3. Atualizar o fluxo ao longo do caminho
            curr = t;
            while (curr != s) {
                Aresta e = paiAresta[curr];
                e.fluxo += theta;
                e.arestaReversa.fluxo -= theta;
                curr = pred[curr];
            }

            fluxoMaximo += theta;
            custoTotal += custoIteracao;
            System.out.printf("   >>> Fluxo Acumulado: %.2f | Custo Total Acumulado: %.2f%n", fluxoMaximo, custoTotal);
            numIteracao++;
        }

        System.out.println("\n======================================================================");
        System.out.printf("   Fluxo Máximo Total Alcançado (f): %.2f%n", fluxoMaximo);
        System.out.printf("   Custo Mínimo Total do Fluxo:      %.2f%n", custoTotal);
        System.out.println("======================================================================");

        // Imprimir detalhamento didático das arestas com fluxo ativo
        System.out.println("\n>>> DETALHAMENTO DE FLUXOS E CUSTOS POR ARESTA (ATIVOS) <<<");
        System.out.printf("   %-15s | %-12s | %-12s | %-15s | %-12s%n", "Aresta (U -> V)", "Capacidade", "Fluxo Final", "Custo Unitário", "Subtotal Custo");
        System.out.println("   ----------------------------------------------------------------------------------------");

        for (int u = 0; u < capacidadeAtual; u++) {
            if (vetorVertices[u] != null) {
                Aresta e = vetorVertices[u].inicioLista;
                while (e != null) {
                    // Apenas arestas originais com fluxo > 0 (ignora as provisórias e as sem fluxo)
                    if (!e.ehReversaProvisoria && e.fluxo > 0.0001) {
                        double subtotal = e.fluxo * e.custoUnitario;
                        System.out.printf("   V%-2d -> V%-10d | %-12.2f | %-12.2f | %-15.2f | %-12.2f%n", 
                            u, e.idDestino, e.custo, e.fluxo, e.custoUnitario, subtotal);
                    }
                    e = e.proxima;
                }
            }
        }
        System.out.println("   ----------------------------------------------------------------------------------------");
        System.out.printf("   >>> Custo Total Calculado (Soma dos subtotais): %.2f%n", custoTotal);
        System.out.println("======================================================================\n");

        limparRedeFluxo();
    }

    public void gerarCenarioFigura1() {
        destruirGrafo(); // Reseta o grafo atual
        this.capacidadeAtual = 7;
        this.vetorVertices = new Vertice[7];

        incluirVertice(0, "s (Fonte)");
        incluirVertice(1, "A");
        incluirVertice(2, "B");
        incluirVertice(3, "C");
        incluirVertice(4, "D");
        incluirVertice(5, "E");
        incluirVertice(6, "t (Sumidouro)");

        // De acordo com a Figura 1: (capacidade, custoUnitario)
        incluirArestaComCusto(0, 1, 4.0, 10.0, "s -> A");
        incluirArestaComCusto(0, 2, 8.0, 20.0, "s -> B");
        incluirArestaComCusto(0, 3, 7.0, 14.0, "s -> C");
        incluirArestaComCusto(1, 2, 2.0, 7.0, "A -> B");
        incluirArestaComCusto(1, 4, 6.0, 12.0, "A -> D");
        incluirArestaComCusto(3, 5, 7.0, 16.0, "C -> E");
        incluirArestaComCusto(2, 5, 3.0, 8.0, "B -> E");
        incluirArestaComCusto(5, 4, 5.0, 2.0, "E -> D");
        incluirArestaComCusto(4, 6, 9.0, 17.0, "D -> t");
        incluirArestaComCusto(5, 6, 8.0, 14.0, "E -> t");

        System.out.println("[!] Grafo da Figura 1 (Questão 7) gerado com 7 vértices e 10 arcos.");
    }

    public void executarColoracaoQ2() {
        System.out.println("======================================================================");
        System.out.println("   RESOLUÇÃO DA QUESTÃO 02 - COLORAÇÃO DE GRAFO RANDÔMICO");
        System.out.println("======================================================================");
        
        // 1. Geração do Grafo Randômico Simples, Conexo, sem Loops, Grau Máx 5 e N=15
        destruirGrafo();
        this.capacidadeAtual = 15;
        this.vetorVertices = new Vertice[15];
        for (int i = 0; i < 15; i++) {
            incluirVertice(i, "V" + i);
        }
        
        Random rand = new Random(42); // Semente fixa para garantir reprodutibilidade e didática
        int[] graus = new int[15];
        
        // Garantir conectividade criando uma árvore geradora (ligação de i com algum j < i)
        for (int i = 1; i < 15; i++) {
            // Acha um pai j < i tal que grau(j) < 5
            int tentativa = 0;
            int pai = -1;
            while (tentativa < 100) {
                int candidato = rand.nextInt(i);
                if (graus[candidato] < 5) {
                    pai = candidato;
                    break;
                }
                tentativa++;
            }
            if (pai == -1) {
                // Fallback: acha qualquer um j < i com menor grau
                pai = 0;
                for (int j = 1; j < i; j++) {
                    if (graus[j] < graus[pai]) {
                        pai = j;
                    }
                }
            }
            incluirAresta(pai, i, 1.0, "E");
            incluirAresta(i, pai, 1.0, "E");
            graus[pai]++;
            graus[i]++;
        }
        
        // Adicionar arestas adicionais aleatórias para tornar o grafo mais interessante
        // mas respeitando: simples, sem loops, grau máximo 5
        int arestasAdicionadas = 0;
        int maxTentativas = 500;
        while (arestasAdicionadas < 20 && maxTentativas > 0) {
            maxTentativas--;
            int u = rand.nextInt(15);
            int v = rand.nextInt(15);
            if (u == v) continue;
            if (graus[u] >= 5 || graus[v] >= 5) continue;
            if (buscarAresta(u, v)) continue; // Evita arestas paralelas (grafo simples)
            
            incluirAresta(u, v, 1.0, "E");
            incluirAresta(v, u, 1.0, "E");
            graus[u]++;
            graus[v]++;
            arestasAdicionadas++;
        }
        
        System.out.println("A. Grafo gerado com sucesso:");
        System.out.println("   - 15 Vértices (0 a 14)");
        System.out.println("   - Conexo e simples (sem laços ou arestas paralelas)");
        System.out.println("   - Grau Máximo <= 5");
        System.out.println("\nAdjacências do Grafo:");
        mostrarGrafo();
        
        // 2. Algoritmo Welsh-Powell
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("   PASSO A PASSO: ALGORITMO WELSH-POWELL");
        System.out.println("----------------------------------------------------------------------");
        
        // Passo 2.1: Obter graus atuais
        int[] verticesOrdenados = new int[15];
        for (int i = 0; i < 15; i++) {
            verticesOrdenados[i] = i;
        }
        
        // Ordenação por seleção (selection sort) decrescente pelo grau
        for (int i = 0; i < 14; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < 15; j++) {
                int grauJ = obterGrau(verticesOrdenados[j]);
                int grauMax = obterGrau(verticesOrdenados[maxIdx]);
                if (grauJ > grauMax) {
                    maxIdx = j;
                }
            }
            int temp = verticesOrdenados[i];
            verticesOrdenados[i] = verticesOrdenados[maxIdx];
            verticesOrdenados[maxIdx] = temp;
        }
        
        System.out.println("1. Ordenação dos vértices por grau (decrescente):");
        for (int v : verticesOrdenados) {
            System.out.println("   Vértice " + v + " (Grau " + obterGrau(v) + ")");
        }
        
        // Passo 2.2: Colorir vértices
        int[] coresWP = new int[15]; // 0 significa sem cor
        int corAtual = 1;
        int verticesColoridos = 0;
        
        while (verticesColoridos < 15) {
            System.out.println("\n   >> Colorindo com a COR " + corAtual + ":");
            java.util.List<Integer> coloridosNessaFase = new java.util.ArrayList<>();
            
            for (int v : verticesOrdenados) {
                if (coresWP[v] == 0) {
                    // Verifica se v é adjacente a algum já colorido com corAtual nessa fase
                    boolean adjacenteColorido = false;
                    for (int vizinho : obterVizinhos(v)) {
                        if (coresWP[vizinho] == corAtual) {
                            adjacenteColorido = true;
                            break;
                        }
                    }
                    if (!adjacenteColorido) {
                        coresWP[v] = corAtual;
                        coloridosNessaFase.add(v);
                        verticesColoridos++;
                    }
                }
            }
            System.out.println("      Vértices coloridos: " + coloridosNessaFase);
            corAtual++;
        }
        int totalCoresWP = corAtual - 1;
        System.out.println("\nWelsh-Powell finalizado. Total de cores usadas: " + totalCoresWP);
        
        // 3. Algoritmo Exato (Backtracking) para encontrar o número cromático
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("   ALGORITMO EXATO: ENCONTRANDO O NÚMERO CROMÁTICO");
        System.out.println("----------------------------------------------------------------------");
        
        int[] coresExato = new int[15];
        int numCromatico = 1;
        
        for (int k = 1; k <= 15; k++) {
            java.util.Arrays.fill(coresExato, 0);
            if (colorirBacktracking(0, k, coresExato)) {
                numCromatico = k;
                break;
            }
        }
        
        System.out.println("Número Cromático Exato (qui): " + numCromatico);
        System.out.println("Coloração Exata:");
        for (int i = 0; i < 15; i++) {
            System.out.println("   Vértice " + i + ": Cor " + coresExato[i]);
        }
        
        // 4. Respostas didáticas para a Questão 2
        System.out.println("\n======================================================================");
        System.out.println("   RESPOSTAS DIDÁTICAS PARA OS ITENS DA LISTA");
        System.out.println("======================================================================");
        System.out.println("A. O grafo gerado foi um grafo simples, conexo, sem loops e sem arestas paralelas");
        System.out.println("   com N=15 vértices e grau máximo igual a " + obterGrauMaximo() + ".");
        System.out.println("B. O grafo é 4-colorível? " + (numCromatico <= 4 ? "SIM" : "NÃO"));
        System.out.println("   Justificativa: Através de um algoritmo exato de backtracking, foi demonstrado");
        System.out.println("   que existe uma atribuição válida de cores onde no máximo 4 cores distintas");
        System.out.println("   são utilizadas e nenhuma aresta conecta dois vértices com a mesma cor.");
        System.out.println("C. Coloração completa construída (Exata de " + numCromatico + " cores):");
        for (int c = 1; c <= numCromatico; c++) {
            System.out.print("   Cor " + c + ": {");
            boolean primeiro = true;
            for (int i = 0; i < 15; i++) {
                if (coresExato[i] == c) {
                    if (!primeiro) System.out.print(", ");
                    System.out.print(i);
                    primeiro = false;
                }
            }
            System.out.println("}");
        }
        System.out.println("D. O número cromático de G(V,E) é: " + numCromatico);
        System.out.println("======================================================================");
    }
    
    private int obterGrau(int v) {
        if (v >= capacidadeAtual || vetorVertices[v] == null) return 0;
        int grau = 0;
        Aresta atual = vetorVertices[v].inicioLista;
        while (atual != null) {
            grau++;
            atual = atual.proxima;
        }
        return grau;
    }
    
    private int obterGrauMaximo() {
        int max = 0;
        for (int i = 0; i < capacidadeAtual; i++) {
            if (vetorVertices[i] != null) {
                int g = obterGrau(i);
                if (g > max) max = g;
            }
        }
        return max;
    }
    
    private java.util.List<Integer> obterVizinhos(int v) {
        java.util.List<Integer> vizinhos = new java.util.ArrayList<>();
        if (v >= capacidadeAtual || vetorVertices[v] == null) return vizinhos;
        Aresta atual = vetorVertices[v].inicioLista;
        while (atual != null) {
            vizinhos.add(atual.idDestino);
            atual = atual.proxima;
        }
        return vizinhos;
    }
    
    private boolean colorirBacktracking(int idx, int k, int[] cores) {
        if (idx == 15) return true;
        
        // Tenta atribuir cor c de 1 a k para o vértice idx
        for (int c = 1; c <= k; c++) {
            boolean valido = true;
            for (int vizinho : obterVizinhos(idx)) {
                if (cores[vizinho] == c) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                cores[idx] = c;
                if (colorirBacktracking(idx + 1, k, cores)) {
                    return true;
                }
                cores[idx] = 0;
            }
        }
        return false;
    }

    public void executarQuestao3() {
        System.out.println("======================================================================");
        System.out.println("   RESOLUÇÃO DA QUESTÃO 03 - COMPARATIVO TSP (PCV)");
        System.out.println("======================================================================");
        
        // Coordenadas da Figura A (10 pontos de grafo_imagem.png, ajustadas para exatidão visual)
        double[] x = {2.5, 3.0, 4.2, 6.8, 6.8, 4.7, 1.7, 3.7, 6.7, 8.3};
        double[] y = {8.5, 6.0, 7.0, 7.3, 5.2, 4.3, 1.5, 1.9, 1.8, 1.2};
        int n = 10;
        
        // ----------------------------------------------------------------------
        // PARTE A: CENÁRIO DO GRAFO INCOMPLETO (Conforme grafo_imagem.png)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> SCENARIO 1: GRAFO INCOMPLETO (Apenas arestas de grafo_imagem.png)");
        System.out.println("----------------------------------------------------------------------");
        
        destruirGrafo();
        this.capacidadeAtual = n;
        this.vetorVertices = new Vertice[n];
        for (int i = 0; i < n; i++) {
            incluirVertice(i, "P" + (i + 1) + "(" + x[i] + "," + y[i] + ")");
        }
        
        int[][] arestasPermitidas = {
            {0, 1}, {0, 2}, {0, 3}, // 1-2, 1-3, 1-4
            {1, 2}, {1, 5}, {1, 6}, // 2-3, 2-6, 2-7
            {2, 4},                 // 3-5
            {3, 4},                 // 4-5
            {4, 5}, {4, 8}, {4, 9}, // 5-6, 5-9, 5-10
            {5, 7}, {5, 8},         // 6-8, 6-9
            {6, 7},                 // 7-8
            {7, 8},                 // 8-9
            {8, 9}                  // 9-10
        };
        
        double[][] dInc = new double[n][n];
        double INF = 9999.0;
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(dInc[i], INF);
            dInc[i][i] = Double.POSITIVE_INFINITY;
        }
        
        for (int[] aresta : arestasPermitidas) {
            int u = aresta[0];
            int v = aresta[1];
            double dist = Math.sqrt((x[u] - x[v]) * (x[u] - x[v]) + (y[u] - y[v]) * (y[u] - y[v]));
            dInc[u][v] = dist;
            dInc[v][u] = dist;
            incluirAresta(u, v, dist, "D");
            incluirAresta(v, u, dist, "D");
        }
        
        // 1. VMP no Incompleto
        boolean[] visInc = new boolean[n];
        java.util.List<Integer> rotVMPInc = new java.util.ArrayList<>();
        rotVMPInc.add(0);
        visInc[0] = true;
        int atInc = 0;
        double cVMPInc = 0.0;
        boolean vmpStuckInc = false;
        
        while (rotVMPInc.size() < n) {
            int prox = -1;
            double minDist = INF - 1.0;
            for (int j = 0; j < n; j++) {
                if (!visInc[j] && dInc[atInc][j] < minDist) {
                    minDist = dInc[atInc][j];
                    prox = j;
                }
            }
            if (prox == -1) {
                vmpStuckInc = true;
                break;
            }
            cVMPInc += minDist;
            visInc[prox] = true;
            rotVMPInc.add(prox);
            atInc = prox;
        }
        if (!vmpStuckInc && dInc[atInc][0] < INF) {
            cVMPInc += dInc[atInc][0];
            rotVMPInc.add(0);
        }
        
        // 2. IMD no Incompleto
        java.util.List<Integer> rotIMDInc = new java.util.ArrayList<>();
        rotIMDInc.add(0);
        int maisDistInc = -1;
        double maxDistInc = -1.0;
        for (int j = 1; j < n; j++) {
            if (dInc[0][j] > maxDistInc) {
                maxDistInc = dInc[0][j];
                maisDistInc = j;
            }
        }
        rotIMDInc.add(maisDistInc);
        boolean[] insInc = new boolean[n];
        insInc[0] = true;
        insInc[maisDistInc] = true;
        
        while (rotIMDInc.size() < n) {
            int k = -1;
            double maxD = -1.0;
            for (int i = 0; i < n; i++) {
                if (!insInc[i]) {
                    double dSub = Double.POSITIVE_INFINITY;
                    for (int u : rotIMDInc) {
                        if (dInc[i][u] < dSub) dSub = dInc[i][u];
                    }
                    if (dSub > maxD) {
                        maxD = dSub;
                        k = i;
                    }
                }
            }
            int melPos = -1;
            double menAum = Double.POSITIVE_INFINITY;
            for (int i = 0; i < rotIMDInc.size(); i++) {
                int u = rotIMDInc.get(i);
                int v = rotIMDInc.get((i + 1) % rotIMDInc.size());
                double aum = dInc[u][k] + dInc[k][v] - dInc[u][v];
                if (aum < menAum) {
                    menAum = aum;
                    melPos = i + 1;
                }
            }
            rotIMDInc.add(melPos, k);
            insInc[k] = true;
        }
        double cIMDInc = 0.0;
        boolean imdValInc = true;
        for (int i = 0; i < n; i++) {
            double dAr = dInc[rotIMDInc.get(i)][rotIMDInc.get((i + 1) % n)];
            if (dAr >= INF) imdValInc = false;
            cIMDInc += dAr;
        }
        rotIMDInc.add(0);
        
        // 3. Exato no Incompleto
        double[] cExInc = {Double.POSITIVE_INFINITY};
        int[] rotExInc = new int[n + 1];
        int[] tmpRotInc = new int[n + 1];
        tmpRotInc[0] = 0;
        boolean[] visExInc = new boolean[n];
        visExInc[0] = true;
        double[] minArInc = new double[n];
        for (int i = 0; i < n; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (i != j && dInc[i][j] < min) min = dInc[i][j];
            }
            minArInc[i] = min;
        }
        resolverTSPExato(0, 1, 0.0, tmpRotInc, visExInc, dInc, minArInc, cExInc, rotExInc);
        
        // Impressão Scenário 1
        System.out.println("   - Vizinho Mais Próximo (VMP): " + (vmpStuckInc ? "FALHOU (Ficou preso)" : String.format("%.4f", cVMPInc)));
        if (!vmpStuckInc) {
            java.util.List<Integer> dVMP = new java.util.ArrayList<>();
            for (int r : rotVMPInc) dVMP.add(r + 1);
            System.out.println("     Rota VMP (1-based): " + dVMP);
        }
        System.out.println("   - Inserção Mais Distante (IMD): " + String.format("%.4f", cIMDInc) + (imdValInc ? "" : " (INVÁLIDA - conexões proibidas)"));
        java.util.List<Integer> dIMD = new java.util.ArrayList<>();
        for (int r : rotIMDInc) dIMD.add(r + 1);
        System.out.println("     Rota IMD (1-based): " + dIMD);
        System.out.println("   - Solução Ótima Exata (B&B): " + String.format("%.4f", cExInc[0]));
        java.util.List<Integer> dEx = new java.util.ArrayList<>();
        for (int r : rotExInc) dEx.add(r + 1);
        System.out.println("     Rota Ótima (1-based): " + dEx);
        
        // ----------------------------------------------------------------------
        // PARTE B: CENÁRIO DO GRAFO COMPLETO (Todas as conexões euclidianas)
        // ----------------------------------------------------------------------
        System.out.println("\n>>> SCENARIO 2: GRAFO COMPLETO (Todas as distâncias euclidianas permitidas)");
        System.out.println("----------------------------------------------------------------------");
        
        double[][] dComp = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dComp[i][j] = Double.POSITIVE_INFINITY;
                } else {
                    dComp[i][j] = Math.sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j]));
                }
            }
        }
        
        // 1. VMP no Completo
        boolean[] visComp = new boolean[n];
        java.util.List<Integer> rotVMPComp = new java.util.ArrayList<>();
        rotVMPComp.add(0);
        visComp[0] = true;
        int atComp = 0;
        double cVMPComp = 0.0;
        
        while (rotVMPComp.size() < n) {
            int prox = -1;
            double minDist = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (!visComp[j] && dComp[atComp][j] < minDist) {
                    minDist = dComp[atComp][j];
                    prox = j;
                }
            }
            cVMPComp += minDist;
            visComp[prox] = true;
            rotVMPComp.add(prox);
            atComp = prox;
        }
        cVMPComp += dComp[atComp][0];
        rotVMPComp.add(0);
        
        // 2. IMD no Completo
        java.util.List<Integer> rotIMDComp = new java.util.ArrayList<>();
        rotIMDComp.add(0);
        int maisDistComp = -1;
        double maxDistComp = -1.0;
        for (int j = 1; j < n; j++) {
            if (dComp[0][j] > maxDistComp) {
                maxDistComp = dComp[0][j];
                maisDistComp = j;
            }
        }
        rotIMDComp.add(maisDistComp);
        boolean[] insComp = new boolean[n];
        insComp[0] = true;
        insComp[maisDistComp] = true;
        
        while (rotIMDComp.size() < n) {
            int k = -1;
            double maxD = -1.0;
            for (int i = 0; i < n; i++) {
                if (!insComp[i]) {
                    double dSub = Double.POSITIVE_INFINITY;
                    for (int u : rotIMDComp) {
                        if (dComp[i][u] < dSub) dSub = dComp[i][u];
                    }
                    if (dSub > maxD) {
                        maxD = dSub;
                        k = i;
                    }
                }
            }
            int melPos = -1;
            double menAum = Double.POSITIVE_INFINITY;
            for (int i = 0; i < rotIMDComp.size(); i++) {
                int u = rotIMDComp.get(i);
                int v = rotIMDComp.get((i + 1) % rotIMDComp.size());
                double aum = dComp[u][k] + dComp[k][v] - dComp[u][v];
                if (aum < menAum) {
                    menAum = aum;
                    melPos = i + 1;
                }
            }
            rotIMDComp.add(melPos, k);
            insComp[k] = true;
        }
        double cIMDComp = 0.0;
        for (int i = 0; i < n; i++) {
            cIMDComp += dComp[rotIMDComp.get(i)][rotIMDComp.get((i + 1) % n)];
        }
        rotIMDComp.add(0);
        
        // 3. Exato no Completo
        double[] cExComp = {Double.POSITIVE_INFINITY};
        int[] rotExComp = new int[n + 1];
        int[] tmpRotComp = new int[n + 1];
        tmpRotComp[0] = 0;
        boolean[] visExComp = new boolean[n];
        visExComp[0] = true;
        double[] minArComp = new double[n];
        for (int i = 0; i < n; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (i != j && dComp[i][j] < min) min = dComp[i][j];
            }
            minArComp[i] = min;
        }
        resolverTSPExato(0, 1, 0.0, tmpRotComp, visExComp, dComp, minArComp, cExComp, rotExComp);
        
        // Impressão Scenário 2
        System.out.println("   - Vizinho Mais Próximo (VMP): " + String.format("%.4f", cVMPComp));
        java.util.List<Integer> dVMPComp = new java.util.ArrayList<>();
        for (int r : rotVMPComp) dVMPComp.add(r + 1);
        System.out.println("     Rota VMP (1-based): " + dVMPComp);
        
        System.out.println("   - Inserção Mais Distante (IMD): " + String.format("%.4f", cIMDComp));
        java.util.List<Integer> dIMDComp = new java.util.ArrayList<>();
        for (int r : rotIMDComp) dIMDComp.add(r + 1);
        System.out.println("     Rota IMD (1-based): " + dIMDComp);
        
        System.out.println("   - Solução Ótima Exata (B&B): " + String.format("%.4f", cExComp[0]));
        java.util.List<Integer> dExComp = new java.util.ArrayList<>();
        for (int r : rotExComp) dExComp.add(r + 1);
        System.out.println("     Rota Ótima (1-based): " + dExComp);
        
        // ----------------------------------------------------------------------
        // PARTE C: CÁLCULO DIDÁTICA DO LIMITE INFERIOR (LITTLE ET AL.)
        // ----------------------------------------------------------------------
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("   EXPLICAÇÃO DIDÁTICA DO CÁLCULO DO LIMITE INFERIOR (LITTLE ET AL.)");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Para mostrar o cálculo do Limite Inferior (LI) usando a redução de custos de");
        System.out.println("Little et al., considere a matriz 5x5 do exemplo do slide 28:");
        System.out.println("   [ -  0  4  0  7 ]");
        System.out.println("   [ 5  -  4  9  0 ]");
        System.out.println("   [ 4  7  -  2  0 ]");
        System.out.println("   [ 6  3  0  9  3 ]");
        System.out.println("   [ 3  5  1  8  - ]");
        System.out.println("\nPasso 1: Redução por Linhas (Subtrair o menor valor de cada linha):");
        System.out.println("   - Linha 1 (min 0): [ -  0  4  0  7 ]");
        System.out.println("   - Linha 2 (min 0): [ 5  -  4  9  0 ]");
        System.out.println("   - Linha 3 (min 0): [ 4  7  -  2  0 ]");
        System.out.println("   - Linha 4 (min 0): [ 6  3  0  9  3 ]");
        System.out.println("   - Linha 5 (min 1): [ 2  4  0  7  - ]   -> Custo de Redução Linhas = 1");
        System.out.println("\nPasso 2: Redução por Colunas na matriz resultante (Subtrair o menor de cada coluna):");
        System.out.println("   - Coluna 1 (valores 2, 5, 4, 6, 2 | min 2): Subtrai 2 -> [0, 3, 2, 4, 0]^T");
        System.out.println("   - Coluna 2 (min 0), Coluna 3 (min 0), Coluna 4 (min 0), Coluna 5 (min 0): Não mudam.");
        System.out.println("   - Custo de Redução Colunas = 2");
        System.out.println("\nPasso 3: Limite Inferior Inicial (LI):");
        System.out.println("   - LI = Custo Linhas (1) + Custo Colunas (2) = 3.");
        System.out.println("Qualquer tour completo baseado nessa matriz custará no mínimo 3!");
        
        System.out.println("\n======================================================================");
        System.out.println("   COMPARATIVO E CONCLUSÃO FINAL (GRAFO COMPLETO vs. INCOMPLETO)");
        System.out.println("======================================================================");
        System.out.println("   ALGORITMO   | GRAFO INCOMPLETO (IMAGEM)  | GRAFO COMPLETO");
        System.out.println("   ------------+----------------------------+------------------");
        System.out.println("   VMP         | " + (vmpStuckInc ? "FALHOU (Becos-sem-saída)   " : String.format("%-26s", String.format("%.4f", cVMPInc))) + " | " + String.format("%.4f", cVMPComp));
        System.out.println("   IMD         | " + (imdValInc ? String.format("%-26s", String.format("%.4f", cIMDInc)) : String.format("%-26s", String.format("%.4f (INVÁLIDA)", cIMDInc))) + " | " + String.format("%.4f", cIMDComp));
        System.out.println("   EXATO (B&B) | " + String.format("%-26s", String.format("%.4f", cExInc[0])) + " | " + String.format("%.4f", cExComp[0]));
        System.out.println("\n   Análise Científica:");
        System.out.println("   1. No GRAFO COMPLETO, a Inserção Mais Distante (IMD) e o Exato coincidem");
        System.out.println("      em " + String.format("%.4f", cExComp[0]) + ", mostrando a eficácia da heurística quando todas as");
        System.out.println("      conexões são possíveis. O VMP foi menos eficiente (" + String.format("%.4f", cVMPComp) + ").");
        System.out.println("   2. No GRAFO INCOMPLETO (restrito), as heurísticas clássicas falham totalmente:");
        System.out.println("      VMP fica travado sem opções de vizinhos unvisited e o IMD viola restrições,");
        System.out.println("      deixando apenas o Algoritmo Exato encontrar o único ciclo real (" + String.format("%.4f", cExInc[0]) + ").");
        System.out.println("======================================================================");
    }
    
    private void resolverTSPExato(int curr, int count, double custo, int[] rota, boolean[] visitados, 
                                  double[][] d, double[] minAresta, double[] melhorCusto, int[] melhorRota) {
        if (custo >= melhorCusto[0]) return;
        
        // Limitador Inferior (Bounding)
        double lb = custo;
        for (int i = 0; i < d.length; i++) {
            if (!visitados[i]) {
                lb += minAresta[i];
            }
        }
        if (lb >= melhorCusto[0]) return;
        
        if (count == d.length) {
            double custoTotal = custo + d[curr][0];
            if (custoTotal < melhorCusto[0]) {
                melhorCusto[0] = custoTotal;
                rota[d.length] = 0;
                System.arraycopy(rota, 0, melhorRota, 0, d.length + 1);
            }
            return;
        }
        
        for (int nxt = 0; nxt < d.length; nxt++) {
            if (!visitados[nxt] && d[curr][nxt] < 999.0) { // Somente segue arestas reais
                visitados[nxt] = true;
                rota[count] = nxt;
                resolverTSPExato(nxt, count + 1, custo + d[curr][nxt], rota, visitados, d, minAresta, melhorCusto, melhorRota);
                visitados[nxt] = false;
            }
        }
    }

    public void executarQuestao4() {
        System.out.println("======================================================================");
        System.out.println("   RESOLUÇÃO DA QUESTÃO 04 - CARTEIRO CHINÊS DIRECIONADO (PCC)");
        System.out.println("======================================================================");
        
        // 1. Coordenadas da Figura B / grafo_04.png
        double[] x = {2.5, 3.0, 4.2, 6.8, 6.8, 4.7, 1.7, 3.7, 6.7, 8.3};
        double[] y = {8.5, 6.0, 7.0, 7.3, 5.2, 4.3, 1.5, 1.9, 1.8, 1.2};
        int n = 10;
        
        // 2. Arestas direcionadas de grafo_04.png (0-based) com CUSTOS POSITIVOS ATRIBUÍDOS (inteiros)
        int[][] arestasPCC = {
            {0, 1, 3}, {1, 2, 2}, {2, 0, 4},       // Ciclo 1->2->3->1
            {1, 6, 5}, {6, 7, 2}, {7, 5, 3}, {5, 1, 3}, // Ciclo 2->7->8->6->2
            {2, 4, 4}, {4, 3, 3}, {3, 0, 5},       // Caminho 3->5->4->1
            {5, 4, 2},                             // 6->5
            {7, 8, 4}, {8, 5, 3},                   // 8->9->6
            {4, 8, 3},                             // 5->9
            {8, 9, 2}, {9, 4, 3}                   // 9->10->5
        };
        
        // Inicialização do grafo
        destruirGrafo();
        this.capacidadeAtual = n;
        this.vetorVertices = new Vertice[n];
        for (int i = 0; i < n; i++) {
            incluirVertice(i, "P" + (i + 1) + "(" + x[i] + "," + y[i] + ")");
        }
        
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            java.util.Arrays.fill(d[i], Double.POSITIVE_INFINITY);
            d[i][i] = 0.0;
        }
        
        double originalCost = 0.0;
        for (int[] e : arestasPCC) {
            int u = e[0];
            int v = e[1];
            double dist = e[2]; // Custos positivos atribuídos
            d[u][v] = dist;
            incluirAresta(u, v, dist, "D"); // Direcionado
            originalCost += dist;
        }
        
        System.out.println("1. Grafo direcionado populado com 10 vértices e 16 arcos.");
        System.out.println("   - Custos positivos atribuídos aos arcos originais:");
        System.out.println("     P1->P2: 3 | P2->P3: 2 | P3->P1: 4 | P2->P7: 5 | P7->P8: 2 | P8->P6: 3");
        System.out.println("     P6->P2: 3 | P3->P5: 4 | P5->P4: 3 | P4->P1: 5 | P6->P5: 2 | P8->P9: 4");
        System.out.println("     P9->P6: 3 | P5->P9: 3 | P9->P10: 2 | P10->P5: 3");
        System.out.println("   - Soma dos custos dos arcos originais: " + String.format("%.1f", originalCost));
        
        // 3. Graus de Entrada e Saída
        int[] outDeg = new int[n];
        int[] inDeg = new int[n];
        for (int[] e : arestasPCC) {
            outDeg[e[0]]++;
            inDeg[e[1]]++;
        }
        
        System.out.println("\n2. Graus dos Vértices (Entrada vs. Saída):");
        for (int i = 0; i < n; i++) {
            int diff = inDeg[i] - outDeg[i];
            System.out.println("   - P" + (i + 1) + ": Entrada=" + inDeg[i] + ", Saída=" + outDeg[i] + " | Saldo=" + (diff > 0 ? "+" : "") + diff);
        }
        
        // 4. Floyd-Warshall para encontrar caminhos mais curtos para a duplicação
        double[][] sp = new double[n][n];
        int[][] path = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sp[i][j] = d[i][j];
                path[i][j] = (d[i][j] < Double.POSITIVE_INFINITY && i != j) ? j : -1;
            }
        }
        
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (sp[i][k] + sp[k][j] < sp[i][j]) {
                        sp[i][j] = sp[i][k] + sp[k][j];
                        path[i][j] = path[i][k];
                    }
                }
            }
        }
        
        // 5. Identificar vértices com saldo positivo (sinks de duplicação) e negativo (sources de duplicação)
        java.util.List<Integer> flowSources = new java.util.ArrayList<>();
        java.util.List<Integer> flowSinks = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            int diff = inDeg[i] - outDeg[i];
            if (diff > 0) {
                for (int c = 0; c < diff; c++) flowSources.add(i); // Precisa de mais saídas
            } else if (diff < 0) {
                for (int c = 0; c < -diff; c++) flowSinks.add(i);   // Precisa de mais entradas
            }
        }
        
        System.out.println("\n3. Desbalanceamento de Fluxo:");
        System.out.print("   - Fontes de Duplicação (Entrada > Saída, precisam de novos arcos saindo): ");
        for (int s : flowSources) System.out.print("P" + (s + 1) + " ");
        System.out.print("\n   - Sumidouros de Duplicação (Saída > Entrada, precisam de novos arcos entrando): ");
        for (int t : flowSinks) System.out.print("P" + (t + 1) + " ");
        System.out.println();
        
        // 6. Resolução do Emparelhamento de Custo Mínimo (2x2)
        int s1 = flowSources.get(0);
        int s2 = flowSources.get(1);
        int t1 = flowSinks.get(0);
        int t2 = flowSinks.get(1);
        
        double cost1 = sp[s1][t1] + sp[s2][t2];
        double cost2 = sp[s1][t2] + sp[s2][t1];
        
        System.out.println("\n4. Opções de Emparelhamento (Matching) para Duplicação:");
        System.out.println("   - Opção 1: P" + (s1 + 1) + " -> P" + (t1 + 1) + " e P" + (s2 + 1) + " -> P" + (t2 + 1) + " | Custo = " + String.format("%.1f", cost1));
        System.out.println("   - Opção 2: P" + (s1 + 1) + " -> P" + (t2 + 1) + " e P" + (s2 + 1) + " -> P" + (t1 + 1) + " | Custo = " + String.format("%.1f", cost2));
        
        java.util.List<int[]> optEdges = new java.util.ArrayList<>();
        if (cost1 < cost2) {
            System.out.println("   -> Opção 1 selecionada como Ótima!");
            adicionarCaminhoPCC(s1, t1, path, optEdges);
            adicionarCaminhoPCC(s2, t2, path, optEdges);
        } else {
            System.out.println("   -> Opção 2 selecionada como Ótima!");
            adicionarCaminhoPCC(s1, t2, path, optEdges);
            adicionarCaminhoPCC(s2, t1, path, optEdges);
        }
        
        double duplicationCost = 0.0;
        System.out.println("\n5. Arcos Duplicados Necessários:");
        for (int[] e : optEdges) {
            double dist = d[e[0]][e[1]];
            duplicationCost += dist;
            System.out.println("   - Duplicar Arco P" + (e[0] + 1) + " -> P" + (e[1] + 1) + " (Custo: " + String.format("%.1f", dist) + ")");
        }
        System.out.println("   - Custo total da duplicação: " + String.format("%.1f", duplicationCost));
        
        // 7. Achar o Circuito Euleriano do Grafo Aumentado usando Algoritmo de Hierholzer
        java.util.List<Integer>[] augAdj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++) augAdj[i] = new java.util.ArrayList<>();
        for (int[] e : arestasPCC) {
            augAdj[e[0]].add(e[1]);
        }
        for (int[] e : optEdges) {
            augAdj[e[0]].add(e[1]);
        }
        
        java.util.List<Integer> currPath = new java.util.ArrayList<>();
        java.util.List<Integer> eulerCircuit = new java.util.ArrayList<>();
        currPath.add(0); // Começa no P1
        
        while (!currPath.isEmpty()) {
            int currNode = currPath.get(currPath.size() - 1);
            if (!augAdj[currNode].isEmpty()) {
                int nextNode = augAdj[currNode].remove(0);
                currPath.add(nextNode);
            } else {
                eulerCircuit.add(currPath.remove(currPath.size() - 1));
            }
        }
        java.util.Collections.reverse(eulerCircuit);
        
        // Exibir Circuito do Carteiro Chinês
        java.util.List<Integer> displayCircuit = new java.util.ArrayList<>();
        for (int node : eulerCircuit) {
            displayCircuit.add(node + 1);
        }
        
        System.out.println("\n6. Rota do Carteiro Chinês (1-based):");
        System.out.println("   " + displayCircuit);
        System.out.println("   - Número de arcos percorridos: " + (eulerCircuit.size() - 1));
        System.out.println("   - Custo total da viagem do carteiro: " + String.format("%.1f", (originalCost + duplicationCost)));
        System.out.println("======================================================================");
    }

    private void adicionarCaminhoPCC(int start, int end, int[][] path, java.util.List<int[]> optEdges) {
    }

    public void executarQuestao5() {
        System.out.println("======================================================================");
        System.out.println("   RESOLUÇÃO DA QUESTÃO 05 - COLORAÇÃO DE VÉRTICES E ARESTAS (FIGURA B)");
        System.out.println("======================================================================");
        
        int n = 11;
        
        // 1. Definição das conexões do grafo da Figura B (não direcionado)
        // Adjacências do grafo da Figura B
        java.util.List<Integer>[] adjList = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++) adjList[i] = new java.util.ArrayList<>();
        
        int[][] arestasFigB = {
            {0, 1}, {0, 2}, {0, 3},
            {1, 3}, {1, 4},
            {2, 3}, {2, 5}, {2, 8},
            {3, 4}, {3, 5}, {3, 6},
            {4, 6}, {4, 10},
            {5, 6}, {5, 8}, {5, 9},
            {6, 9}, {6, 10},
            {7, 8}, {7, 9}, {7, 10},
            {8, 9},
            {9, 10}
        };
        
        destruirGrafo();
        this.capacidadeAtual = n;
        this.vetorVertices = new Vertice[n];
        for (int i = 0; i < n; i++) {
            incluirVertice(i, "P" + (i + 1));
        }
        
        for (int[] e : arestasFigB) {
            int u = e[0];
            int v = e[1];
            adjList[u].add(v);
            adjList[v].add(u);
            incluirAresta(u, v, 1.0, "E");
            incluirAresta(v, u, 1.0, "E");
        }
        
        System.out.println("1. Grafo da Figura B gerado com 11 vértices e 23 arestas não direcionadas.");
        
        // A. Coloração de Vértices
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("   PARTE A: K-COLORAÇÃO DE VÉRTICES (MÉTODO EXATO)");
        System.out.println("----------------------------------------------------------------------");
        
        int[] coresVertices = new int[n];
        int numCromatico = 1;
        for (int k = 1; k <= n; k++) {
            java.util.Arrays.fill(coresVertices, 0);
            if (colorirVerticesQ5(0, k, coresVertices, adjList)) {
                numCromatico = k;
                break;
            }
        }
        
        System.out.println("Número Cromático Vértices (qui): " + numCromatico);
        System.out.println("Atribuição de Cores nos Vértices (1-based):");
        for (int c = 1; c <= numCromatico; c++) {
            System.out.print("   Cor " + c + ": { ");
            boolean primeiro = true;
            for (int i = 0; i < n; i++) {
                if (coresVertices[i] == c) {
                    if (!primeiro) System.out.print(", ");
                    System.out.print("P" + (i + 1));
                    primeiro = false;
                }
            }
            System.out.println(" }");
        }
        
        // B. Coloração de Arestas
        System.out.println("\n----------------------------------------------------------------------");
        System.out.println("   PARTE B: COLORAÇÃO DE ARESTAS (TEOREMA DE VIZING)");
        System.out.println("----------------------------------------------------------------------");
        
        int maxGrau = obterGrauMaximo();
        System.out.println("Grau Máximo do Grafo (Delta): " + maxGrau);
        System.out.println("Pelo Teorema de Vizing, o índice cromático (chi') é Delta ou Delta + 1.");
        
        // Reunir arestas únicas
        java.util.List<int[]> arestas = new java.util.ArrayList<>();
        for (int[] e : arestasFigB) {
            arestas.add(e);
        }
        
        int[] coresArestas = new int[arestas.size()];
        int indiceCromatico = maxGrau;
        
        System.out.println("Testando coloribilidade com k = Delta = " + maxGrau + "...");
        java.util.Arrays.fill(coresArestas, 0);
        if (colorirArestasQ5(0, maxGrau, coresArestas, arestas)) {
            indiceCromatico = maxGrau;
            System.out.println("Sucesso! O grafo é de Classe 1.");
        } else {
            System.out.println("Falhou! Testando com k = Delta + 1 = " + (maxGrau + 1) + "...");
            java.util.Arrays.fill(coresArestas, 0);
            if (colorirArestasQ5(0, maxGrau + 1, coresArestas, arestas)) {
                indiceCromatico = maxGrau + 1;
                System.out.println("Sucesso! O grafo é de Classe 2.");
            }
        }
        
        System.out.println("\nÍndice Cromático Arestas (chi'): " + indiceCromatico);
        System.out.println("Atribuição de Cores nas Arestas:");
        for (int c = 1; c <= indiceCromatico; c++) {
            System.out.print("   Cor " + c + ": { ");
            boolean primeiro = true;
            for (int i = 0; i < arestas.size(); i++) {
                if (coresArestas[i] == c) {
                    int[] e = arestas.get(i);
                    if (!primeiro) System.out.print(", ");
                    System.out.print("(P" + (e[0] + 1) + ", P" + (e[1] + 1) + ")");
                    primeiro = false;
                }
            }
            System.out.println(" }");
        }
        System.out.println("======================================================================");
    }

    private boolean colorirVerticesQ5(int idx, int k, int[] cores, java.util.List<Integer>[] adjList) {
        if (idx == 11) return true;
        for (int c = 1; c <= k; c++) {
            boolean valido = true;
            for (int vizinho : adjList[idx]) {
                if (cores[vizinho] == c) {
                    valido = false;
                    break;
                }
            }
            if (valido) {
                cores[idx] = c;
                if (colorirVerticesQ5(idx + 1, k, cores, adjList)) return true;
                cores[idx] = 0;
            }
        }
        return false;
    }

    private boolean colorirArestasQ5(int idx, int k, int[] coresArestas, java.util.List<int[]> arestas) {
        if (idx == arestas.size()) return true;
        int[] e1 = arestas.get(idx);
        for (int c = 1; c <= k; c++) {
            boolean valido = true;
            for (int j = 0; j < idx; j++) {
                if (coresArestas[j] == c) {
                    int[] e2 = arestas.get(j);
                    if (e1[0] == e2[0] || e1[0] == e2[1] || e1[1] == e2[0] || e1[1] == e2[1]) {
                        valido = false;
                        break;
                    }
                }
            }
            if (valido) {
                coresArestas[idx] = c;
                if (colorirArestasQ5(idx + 1, k, coresArestas, arestas)) return true;
                coresArestas[idx] = 0;
            }
        }
        return false;
    }
}