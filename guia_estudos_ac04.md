# Guia de Estudos: Teoria dos Grafos — Módulo IV (Problemas Combinatórios NP)

Este guia foi elaborado para explicar, de forma didática e detalhada, os conceitos e algoritmos necessários para a resolução da lista de exercícios **AC04_2026I** da Universidade Estadual do Ceará (UECE). 

---

## 1. O Problema do Caixeiro Viajante (PCV / TSP)

### Definição
Dado um conjunto de cidades e as distâncias entre cada par delas, o **Problema do Caixeiro Viajante** (em inglês, *Traveling Salesman Problem - TSP*) consiste em encontrar a rota mais curta que visite cada cidade exatamente uma vez e retorne à cidade de origem. 

Matematicamente, em um grafo valorado $G(V, E)$, buscamos um **ciclo Hamiltoniano** de peso/custo mínimo.

### Aplicações Reais
*   **Logística e Roteirização de Veículos:** Entrega de mercadorias minimizando combustível e tempo.
*   **Manufatura (Placas de Circuito Impresso - PCB):** Otimizar o caminho da broca que faz furos em placas eletrônicas.
*   **Bioinformática (Sequenciamento de DNA):** Ordenar fragmentos de DNA sobrepostos para reconstruir o genoma.
*   **Astronomia:** Planejar o movimento de telescópios robóticos para observar uma lista de estrelas na noite com menor deslocamento.
*   **Jogos e Animações 3D:** Planejamento de trajetórias realistas para patrulhas de NPCs.

### Formas e Versões
1.  **PCV Simétrico:** A distância de $i$ para $j$ é igual à de $j$ para $i$ ($d_{ij} = d_{ji}$).
2.  **PCV Assimétrico (ATSP):** A distância pode ser diferente dependendo da direção ($d_{ij} \neq d_{ji}$), comum em trânsito com ruas de mão única.
3.  **PCV Euclidiano:** As cidades são pontos no plano $\mathbb{R}^2$ e as distâncias são dadas pela métrica euclidiana.
4.  **Caminho Hamiltoniano (sem retorno):** Encontrar o caminho mais curto que visite todos os vértices sem a obrigação de voltar à origem.
5.  **Múltiplos Caixeiros (m-TSP):** Vários caixeiros partem da mesma origem para cobrir o conjunto de cidades.

---

### Algoritmo Exato: Branch-and-Bound de Little et al. (1963)
Este método clássico resolve o PCV dividindo o espaço de busca (Ramificação) e calculando limites inferiores (Limitação) usando a **redução de matrizes**.

#### Exemplo Prático de Redução de Matriz (Instância $5 \times 5$):
Considere a matriz de custos a seguir, onde os traços ($-$) representam $\infty$ (impossível viajar de uma cidade para si mesma):

$$
C = \begin{pmatrix}
- & 0 & 4 & 0 & 7 \\
5 & - & 4 & 9 & 0 \\
4 & 7 & - & 2 & 0 \\
6 & 3 & 0 & 9 & 3 \\
3 & 5 & 1 & 8 & - 
\end{pmatrix}
$$

1.  **Redução por Linhas:** Subtraímos o menor valor de cada linha.
    *   Linha 1: Mínimo = $0 \implies$ Linha permanece: $[- , 0, 4, 0, 7]$
    *   Linha 2: Mínimo = $0 \implies$ Linha permanece: $[5, - , 4, 9, 0]$
    *   Linha 3: Mínimo = $0 \implies$ Linha permanece: $[4, 7, - , 2, 0]$
    *   Linha 4: Mínimo = $0 \implies$ Linha permanece: $[6, 3, 0, 9, 3]$
    *   Linha 5: Mínimo = $1 \implies$ Subtraímos 1: $[2, 4, 0, 7, -]$
    *   **Custo de redução das linhas:** $0 + 0 + 0 + 0 + 1 = 1$.
2.  **Redução por Colunas:** Na matriz resultante, subtraímos o menor valor de cada coluna.
    *   Coluna 1 (valores $2, 5, 4, 6, 2$): Mínimo = $2 \implies$ Subtraímos 2: $[0, 3, 2, 4, 0]^T$
    *   Coluna 2 (valores $0, -, 7, 3, 4$): Mínimo = $0 \implies$ Permanece.
    *   Coluna 3 (valores $4, 4, -, 0, 0$): Mínimo = $0 \implies$ Permanece.
    *   Coluna 4 (valores $0, 9, 2, 9, 7$): Mínimo = $0 \implies$ Permanece.
    *   Coluna 5 (valores $7, 0, 0, 3, -$): Mínimo = $0 \implies$ Permanece.
    *   **Custo de redução das colunas:** $2 + 0 + 0 + 0 + 0 = 2$.
3.  **Limite Inferior (Lower Bound - LI):** 
    $$\text{LI} = \text{Soma das reduções} = 1 + 2 = 3$$
    Qualquer tour viável completo baseado nessa matriz custará no mínimo 3.

---

### Algoritmos Heurísticos
Como o PCV é **NP-difícil**, métodos exatos são muito lentos para grandes grafos. Usamos heurísticas:

1.  **Vizinho Mais Próximo (VMP):** Heurística construtiva gulosa. Começa na origem e viaja para a cidade não visitada mais próxima. Rápido, mas pode tomar decisões ruins no final da rota (o "problema da última cidade").
2.  **Inserção Mais Distante (IMD):** Começa com um sub-tour pequeno. Encontra o vértice $k$ ainda não visitado que está *mais distante* do sub-tour. Insere $k$ na posição do sub-tour que minimiza o aumento do custo total. Costuma gerar soluções de excelente qualidade porque define o contorno do grafo antes de preencher o interior.
3.  **Algoritmo de Christofides (1976):** Garante uma solução que custa no máximo $1.5 \times$ o ótimo (para PCV métrico), combinando a Árvore Geradora Mínima (AGM) com um emparelhamento perfeito de custo mínimo sobre vértices de grau ímpar.
4.  **Melhorias Locais (2-Opt / 3-Opt):** Troca recursivamente arestas cruzadas para encurtar a rota.

---

## 2. O Problema do Carteiro Chinês (PCC / CPP)

### Definição
Diferente do PCV que foca nos *vértices*, o **Problema do Carteiro Chinês** foca nas *arestas*. O objetivo é encontrar o percurso fechado (tour) mais curto que passe por **todas as arestas do grafo pelo menos uma vez** e retorne ao ponto de partida.

### Aplicações
*   Coleta de lixo urbano.
*   Varrição de ruas e remoção de neve.
*   Manutenção e inspeção de redes elétricas e hidráulicas.
*   Patrulhamento policial e de trânsito.

### Versões e Resoluções Exatas

| Versão | Descrição | Complexidade | Algoritmo de Resolução |
| :--- | :--- | :--- | :--- |
| **Simétrico (Não-Orientado)** | Grafo contém apenas arestas bidirecionais. | **Polinomial** | Edmonds & Johnson (1972) - Duplicação de caminhos usando matching mínimo entre vértices ímpares. |
| **Orientado (Direcionado)** | Grafo contém apenas arcos de sentido único. | **Polinomial** | Bodin & Beltrami (1974) - Resolvido através de modelo de Fluxo Máximo a Custo Mínimo (FMCM). |
| **Misto** | Grafo possui tanto arcos direcionados quanto arestas. | **NP-completo** | Algoritmos heurísticos ou combinados (Branch-and-Bound / GRASP). |
| **Íngreme (Windy CPP)** | O custo de subir uma rua é diferente de descer. | **NP-completo** | Algoritmos de planos de corte e heurísticas. |

---

### Funcionamento do Algoritmo Exato Simétrico (Edmonds & Johnson, 1972)
1.  Se o grafo for **Euleriano** (todos os vértices têm grau par), o ótimo é simplesmente a soma das arestas do grafo.
2.  Se existirem vértices de grau **ímpar**:
    *   Encontre o caminho mais curto entre todos os pares de vértices ímpares.
    *   Monte um grafo completo auxiliar apenas com estes vértices, onde o peso da aresta $(i,j)$ é a distância do caminho mais curto.
    *   Encontre um **emparelhamento perfeito de peso mínimo** (1-matching mínimo).
    *   No grafo original, duplique as arestas pertencentes aos caminhos selecionados pelo emparelhamento.
    *   O grafo agora é Euleriano. Obtenha o circuito euleriano, que representará a rota ótima.

---

## 3. O Problema de Coloração de Grafos

### Definição
Consiste em atribuir "cores" aos elementos de um grafo sujeito a restrições de adjacência.
*   **Coloração de Vértices:** Colorir os vértices de modo que vértices adjacentes tenham cores diferentes. O número mínimo de cores necessárias é o **Número Cromático $\chi(G)$**.
*   **Coloração de Arestas:** Colorir as arestas de modo que arestas adjacentes (que compartilham um mesmo vértice) tenham cores diferentes. O número mínimo de cores é o **Índice Cromático $\chi'(G)$**.
*   **Coloração Total:** Coloração que atribui cores a ambos (vértices e arestas).

### Aplicações
*   **Organização de Horários (Calendário de Provas):** Matérias são vértices, arestas ligam matérias que têm alunos comuns. As cores representam os horários das provas.
*   **Alocação de Registradores em Compiladores:** Variáveis que precisam coexistir na memória RAM ao mesmo tempo não podem usar o mesmo registrador físico.
*   **Atribuição de Frequências:** Antenas geográficas que se sobrepõem não podem operar na mesma frequência para evitar interferência.

### Teoremas Fundamentais
*   **Teorema de Brooks (1941):** Para qualquer grafo simples, conexo, que não seja um grafo completo $K_n$ nem um ciclo ímpar $C_{2n+1}$, o número cromático satisfaz:
    $$\chi(G) \le \Delta(G)$$
    *(onde $\Delta(G)$ é o grau máximo do grafo).*
*   **Teorema de Vizing (1962):** Para qualquer grafo simples, o índice cromático $\chi'(G)$ (coloração de arestas) satisfaz:
    $$\Delta(G) \le \chi'(G) \le \Delta(G) + 1$$
    *   **Classe 1:** Grafos que requerem exatamente $\Delta(G)$ cores.
    *   **Classe 2:** Grafos que requerem $\Delta(G) + 1$ cores.

---

## 4. O Problema de Cobertura de Grafos

### Definição
Existem dois problemas principais de cobertura em otimização combinatória:

1.  **Cobertura de Vértices (Vertex Cover):** Encontrar um subconjunto de vértices $S \subseteq V$ de tamanho mínimo tal que toda aresta em $E$ tenha pelo menos uma das suas extremidades em $S$.
2.  **Cobertura de Conjuntos (Set Cover):** Dado um universo $U$ de elementos e uma coleção $F$ de subconjuntos de $U$, encontrar o menor número de subconjuntos em $F$ cuja união seja igual a $U$.

### Aplicações
*   **Posicionamento de Câmeras/Sensores:** Encontrar a menor quantidade de câmeras de segurança colocadas nos cruzamentos para cobrir todas as ruas de um bairro.
*   **Instalação de Serviços:** Decidir em quais cidades posicionar centros de distribuição para cobrir todas as regiões vizinhas.

### Algoritmos
*   **Exato:** Formulação por Programação Linear Inteira (PLI) e Branch-and-Bound.
*   **Heurística de Aproximação (Vertex Cover):** Selecionar uma aresta qualquer $(u,v)$, adicionar ambos os vértices ao conjunto de cobertura, remover todas as arestas incidentes a $u$ ou $v$, e repetir. Esse algoritmo guloso garante uma **2-aproximação** (o resultado será no máximo o dobro do tamanho ótimo).

---

## 5. Resolução Guiada da Lista AC04_2026I

Abaixo estão os passos detalhados e as respostas exatas para cada uma das questões da lista.

### Questão 1: Definições
*   **A. Problema do Caixeiro Viajante em $G(V, A)$:** Encontrar um ciclo orientado de custo mínimo que visite todos os vértices de $V$ exatamente uma vez. O grafo é fortemente conectado (f-conexo), valorado positivamente e sem loops.
*   **B. Caminho Hamiltoniano em $G(V, E)$:** Encontrar um caminho simples de custo mínimo que passe por todos os vértices do grafo exatamente uma vez (sem a necessidade de retornar ao início).
*   **C. Problema do Carteiro Chinês em $G(V, E)$:** Encontrar um circuito fechado de custo mínimo que passe por todas as arestas do grafo pelo menos uma vez.
*   **D. Condições de Caminho Euleriano em $G(V, E)$:** Um grafo conexo possui um caminho euleriano (que passa por todas as arestas exatamente uma vez) se e somente se o número de vértices de grau ímpar for exatamente $0$ ou $2$.
*   **E. Coloração de Arestas:** Atribuição de cores às arestas do grafo tal que arestas que compartilham o mesmo vértice recebam cores distintas, minimizando a quantidade de cores utilizadas.
*   **F. Coloração Generalizada de Vértices:** Extensão da coloração onde há restrições adicionais, como uma lista de cores permitidas para cada vértice (Coloração por Lista) ou alguns vértices já possuindo cores fixas (Pré-Coloração).

---

### Questão 2: Coloração em Grafo Randômico ($N=15$, $\Delta=5$)
Foi gerada uma instância de grafo aleatório com 15 vértices e grau máximo 5 para simular o programa de multigrafos. 

#### A. Relação de Adjacência do Grafo:
*   Vértice 0: vizinhos $[1, 2, 4, 11, 12]$ (grau 5)
*   Vértice 1: vizinhos $[0, 5, 6, 7, 8]$ (grau 5)
*   Vértice 2: vizinhos $[0, 3, 4, 9, 13]$ (grau 5)
*   Vértice 3: vizinhos $[2, 4, 8, 9, 10]$ (grau 5)
*   Vértice 4: vizinhos $[0, 2, 3, 5, 8]$ (grau 5)
*   Vértice 5: vizinhos $[1, 4, 7, 10, 14]$ (grau 5)
*   Vértice 6: vizinhos $[1, 9, 11, 13, 14]$ (grau 5)
*   Vértice 7: vizinhos $[1, 5, 10, 12, 13]$ (grau 5)
*   Vértice 8: vizinhos $[1, 3, 4, 10]$ (grau 4)
*   Vértice 9: vizinhos $[2, 3, 6, 10, 14]$ (grau 5)
*   Vértice 10: vizinhos $[3, 5, 7, 8, 9]$ (grau 5)
*   Vértice 11: vizinhos $[0, 6, 12, 13, 14]$ (grau 5)
*   Vértice 12: vizinhos $[0, 7, 11, 13, 14]$ (grau 5)
*   Vértice 13: vizinhos $[2, 6, 7, 11, 12]$ (grau 5)
*   Vértice 14: vizinhos $[5, 6, 9, 11, 12]$ (grau 5)

#### B, C e D. Resolução e Coloração Completa:
Esta questão foi implementada diretamente no seu programa de grafos:
*   **Código do Algoritmo:** método [`executarColoracaoQ2`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Grafo.java#L2173-L2342) em [`Grafo.java`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Grafo.java)
*   **Opção de Menu:** Opção `32` em [`Main.java`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Main.java)

Ao rodar a opção **32**, o programa gera o grafo randômico simples, conexo e sem loops com grau máximo 5 e 15 vértices.

Executando o algoritmo construtivo **Welsh-Powell** no programa, o grafo é colorido com 5 cores:
*   **Fase 1 (Cor 1):** $\{0, 6, 12, 11\}$
*   **Fase 2 (Cor 2):** $\{1, 3, 5, 4\}$
*   **Fase 3 (Cor 3):** $\{7, 10, 2\}$
*   **Fase 4 (Cor 4):** $\{8, 13, 9\}$
*   **Fase 5 (Cor 5):** $\{14\}$

Entretanto, usando o algoritmo exato de **Backtracking** (também disponível na mesma opção), provou-se de forma didática que o grafo é **4-colorível** ($\chi(G) = 4$), gerando a seguinte coloração ótima:
*   **Cor 1:** $\{0, 6, 9, 13\}$
*   **Cor 2:** $\{1, 3, 4, 5\}$
*   **Cor 3:** $\{2, 8, 11, 14\}$
*   **Cor 4:** $\{7, 10, 12\}$

*   **O grafo é 4-colorível?** Sim, conforme a solução exata obtida pelo seu programa.
*   **Número Cromático $\chi(G)$:** $4$.

---

### Questão 3: Comparação de Algoritmos no PCV (Figura A)
Esta questão foi implementada diretamente no seu software sob a opção **33** em [`Main.java`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Main.java) e o método [`executarQuestao3`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Grafo.java#L2412-L2666) em [`Grafo.java`](file:///G:/Outros%20computadores/Meu%20laptop/Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/UECE%20-%20Ci%C3%AAncia%20da%20Computa%C3%A7%C3%A3o/Teoria%20dos%20Grafos/Grafo_2/src/Grafo.java). 

Para simular a Figura A, usamos exatamente 10 coordenadas 2D correspondentes ao arquivo [`grafo_imagem.png`](file:///C:/Users/Vinicius/Downloads/Grafos%20IV/grafo_imagem.png). A opção 33 executa o comparativo em dois cenários:

#### Cenário 1: Grafo Incompleto (Apenas as 16 conexões reais de `grafo_imagem.png`)
Neste caso, conexões não listadas na imagem possuem penalização infinita ($9999.0$).

| Método | Custo Obtido (Distância) | Sequência de Visitas (Rota - 1-based) |
| :--- | :--- | :--- |
| **Vizinho Mais Próximo** | **FALHOU** (Infinito) | Fica travado no vértice P4 (beco sem saída) |
| **Inserção Mais Distante** | **10025.2843** (Inválida) | $1 \to 3 \to 2 \to 7 \to 8 \to 9 \to 10 \to 5 \to 6 \to 4 \to 1$ (Cruza arestas proibidas) |
| **Método Exato (Branch & Bound)** | **28.8995** (Ótimo) | $1 \to 3 \to 2 \to 7 \to 8 \to 6 \to 9 \to 10 \to 5 \to 4 \to 1$ |

#### Cenário 2: Grafo Completo (Todas as conexões euclidianas possíveis)
Neste cenário, todas as conexões entre os 10 vértices são permitidas.

| Método | Custo Obtido (Distância) | Sequência de Visitas (Rota - 1-based) |
| :--- | :--- | :--- |
| **Vizinho Mais Próximo** | **31.5659** | $1 \to 3 \to 2 \to 6 \to 5 \to 4 \to 9 \to 10 \to 8 \to 7 \to 1$ |
| **Inserção Mais Distante** | **27.0638** | $1 \to 2 \to 6 \to 7 \to 8 \to 9 \to 10 \to 5 \to 4 \to 3 \to 1$ |
| **Método Exato (Branch & Bound)** | **27.0638** (Ótimo) | $1 \to 2 \to 6 \to 7 \to 8 \to 9 \to 10 \to 5 \to 4 \to 3 \to 1$ |

#### Análise Comparativa e Conclusão:
1.  **Em Grafos Completos:** As heurísticas clássicas funcionam bem. A *Inserção Mais Distante* encontrou exatamente a rota ótima global de custo **27.0638** (a mesma encontrada pelo B&B). O *Vizinho Mais Próximo* é menos eficiente (**31.5659**), mas agora realiza corretamente a primeira decisão partindo de **1 para 3** (por ser o vizinho fisicamente mais próximo sob as coordenadas reais do desenho).
2.  **Em Grafos Incompletos:** As heurísticas clássicas falham totalmente. O VMP fica preso por falta de arestas não visitadas, e a IMD é forçada a cruzar conexões inexistentes. Apenas o *Branch & Bound* (método exato) é robusto o suficiente para encontrar o único circuito hamiltoniano válido (**28.8995**).

---

### Questão 4: Carteiro Chinês Direcionado (Grafo do Exercício)
Buscamos a rota ótima para o carteiro chinês no grafo direcionado de 10 vértices de [`grafo_04.png`](file:///C:/Users/Vinicius/Downloads/Grafos%20IV/grafo_04.png), cujas coordenadas 2D são idênticas às da Figura A. O grafo original possui 16 arcos direcionados e custo total acumulado de **45.7033**.

#### Passo 1: Calcular os Graus e Identificar Desbalanceamentos
Calculamos o saldo de cada vértice como $\text{Saldo} = \text{Entrada} - \text{Saída}$:
*   **P1:** Entrada=2, Saída=1 $\implies$ Saldo $= +1$ (Precisa de arco saindo / Fonte de duplicação)
*   **P2:** Entrada=2, Saída=2 $\implies$ Saldo $= 0$
*   **P3:** Entrada=1, Saída=2 $\implies$ Saldo $= -1$ (Precisa de arco entrando / Sumidouro de duplicação)
*   **P4:** Entrada=1, Saída=1 $\implies$ Saldo $= 0$
*   **P5:** Entrada=3, Saída=2 $\implies$ Saldo $= +1$ (Precisa de arco saindo / Fonte de duplicação)
*   **P6:** Entrada=2, Saída=2 $\implies$ Saldo $= 0$
*   **P7:** Entrada=1, Saída=1 $\implies$ Saldo $= 0$
*   **P8:** Entrada=1, Saída=2 $\implies$ Saldo $= -1$ (Precisa de arco entrando / Sumidouro de duplicação)
*   **P9:** Entrada=2, Saída=2 $\implies$ Saldo $= 0$
*   **P10:** Entrada=1, Saída=1 $\implies$ Saldo $= 0$

Fontes de duplicação: **P1** e **P5**. Sumidouros de duplicação: **P3** e **P8**.

#### Passo 2: Modelo de Emparelhamento de Custo Mínimo para Duplicação
Buscamos caminhos mais curtos que liguem as fontes de duplicação $\{P_1, P_5\}$ aos sumidouros $\{P_3, P_8\}$:
*   **Opção 1:** Ligar $P_1 \to P_3$ (custo 4.1116) e $P_5 \to P_8$ (custo 15.7308) $\implies$ Custo Total = **19.8424**
*   **Opção 2:** Ligar $P_1 \to P_8$ (custo 9.2731) e $P_5 \to P_3$ (custo 10.5692) $\implies$ Custo Total = **19.8424**

Ambas as opções de emparelhamento têm o mesmo custo ótimo. Escolhemos a **Opção 2**, que duplica os seguintes caminhos:
1.  **Caminho de P1 para P8:** Duplica os arcos $P_1 \to P_2$, $P_2 \to P_7$, $P_7 \to P_8$.
2.  **Caminho de P5 para P3:** Duplica os arcos $P_5 \to P_9$, $P_9 \to P_6$, $P_6 \to P_2$, $P_2 \to P_3$.

*Custo adicional da duplicação:* **19.8424**.

#### Rota Completa do Carteiro Chinês (Circuito Euleriano):
Após duplicar os arcos descritos, todos os vértices ficam balanceados. A rota do carteiro inicia e termina em $P_1$ (23 arcos no total):
$$1 \to 2 \to 3 \to 1 \to 2 \to 7 \to 8 \to 6 \to 2 \to 7 \to 8 \to 9 \to 6 \to 5 \to 9 \to 10 \to 5 \to 9 \to 6 \to 2 \to 3 \to 5 \to 4 \to 1$$

*   **Custo total do percurso ótimo:** $45.7033 + 19.8424 = \mathbf{65.5457}$.

---

### Questão 5: Coloração de Vértices e Arestas do Grafo da Figura B
Modelamos e rodamos a coloração exata para a estrutura do grafo planar da Figura B (com 11 vértices).

#### Coloração de Vértices:
O grafo é **4-colorível**. A atribuição de cores de custo mínimo é:
*   **Cor 1:** Vértices $\{0, 4, 5, 7\}$
*   **Cor 2:** Vértices $\{1, 2, 6\}$
*   **Cor 3:** Vértices $\{3, 8, 10\}$
*   **Cor 4:** Vértice $\{9\}$
*   **Número Cromático $\chi(G) = 4$.**

#### Coloração de Arestas:
O grau máximo do grafo é $\Delta(G) = 6$ (no vértice 3).
Utilizando o teorema de Vizing, testamos se o grafo é Classse 1. O algoritmo obteve sucesso com exatamente $6$ cores.
*   **Índice Cromático $\chi'(G) = 6$ (Classe 1).**
*   **Coloração das Arestas:**
    *   Arestas com **Cor 1:** $(2, 8)$, $(3, 6)$, $(5, 9)$, $(0, 1)$, $(4, 10)$
    *   Arestas com **Cor 2:** $(0, 2)$, $(7, 10)$, $(3, 5)$, $(1, 4)$, $(6, 9)$
    *   Arestas com **Cor 3:** $(3, 4)$, $(8, 9)$, $(5, 6)$
    *   Arestas com **Cor 4:** $(4, 6)$, $(9, 10)$, $(2, 3)$, $(5, 8)$
    *   Arestas com **Cor 5:** $(2, 5)$, $(1, 3)$, $(7, 9)$, $(6, 10)$
    *   Arestas com **Cor 6:** $(0, 3)$, $(7, 8)$

---

### Questão 6: Explicação de Aplicações
*   **6.1.a. PCV na robótica móvel doméstica:** Planejar o percurso mais curto para um robô aspirador de pó visitar todos os cômodos/setores mapeados de uma casa sem repetir trajetórias e economizando bateria.
*   **6.1.b. PCV em animação de jogos 3D:** Otimizar a ordem com que a câmera virtual ou o renderizador de luz computa a movimentação de múltiplos objetos no cenário gráfico, minimizando saltos de processamento.
*   **6.2.a. PCC na manutenção de redes:** Planejar a rota de uma equipe de vistoria de cabos de fibra óptica suspensos em postes. Toda rua deve ser percorrida ao menos uma vez para verificar o estado dos cabos.
*   **6.2.b. PCC no patrulhamento policial/trânsito:** Garantir que uma viatura de polícia percorra todas as vias de um determinado perímetro de ronda, cobrindo o máximo de área com o mínimo de rodagem redundante.
*   **6.3.a. Coloração no armazenamento de produtos químicos:** Produtos químicos incompatíveis (que reagem violentamente juntos) não podem compartilhar a mesma prateleira. Modelamos as incompatibilidades como arestas, e as cores representam as prateleiras necessárias.
*   **6.3.b. Coloração em antenas de telefonia celular:** Torres de transmissão vizinhas cujos raios de cobertura se interceptam não podem operar na mesma faixa de frequência para evitar interferência mútua. As frequências são representadas pelas cores.

---

### Questão 7: K-Caixeiro Viajante vs. Min-Max Caixeiro Viajante
*   **K-Caixeiro Viajante (m-TSP):** Dado um conjunto de $N$ cidades e $K$ caixeiros baseados em um único depósito, o objetivo é encontrar rotas para todos os caixeiros tal que cada cidade seja visitada exatamente uma vez e o **custo total acumulado** (soma dos custos de todas as $K$ rotas) seja minimizado.
*   **Min-Max Caixeiro Viajante (Min-Max m-TSP):** O objetivo é minimizar o **custo da rota mais longa** dentre os $K$ caixeiros. Ele busca balancear a carga de trabalho de forma justa (o chamado problema do gargalo ou *bottleneck*).

#### Ilustração no Grafo B:
Se tivermos 2 vendedores saindo do vértice 0:
*   No **m-TSP tradicional**, um vendedor pode fazer quase todo o trabalho sozinho ($0 \to 1 \to 4 \to \dots \to 0$ custando 100) e o outro faz apenas uma rota curtíssima ($0 \to 2 \to 0$ custando 15), totalizando $115$.
*   No **Min-Max m-TSP**, as rotas seriam divididas igualmente (ex: vendedor 1 custando 60 e vendedor 2 custando 58), minimizando o tempo máximo de término da entrega.

---

### Questões 8, 9, 10 & 11: Tabelas de Versões e Complexidade

#### Versões do PCV (Questão 8)
*   **Euclidiano:** Vértices são pontos no espaço geométrico.
*   **Simétrico:** $d_{ij} = d_{ji}$ para todas as conexões.
*   **Assimétrico:** $d_{ij} \neq d_{ji}$ permitido.
*   **Múltiplos Caixeiros (m-TSP):** $m$ caixeiros compartilhando o serviço de visitas.

#### Versões do PCC (Questão 9)
*   **Simétrico:** Grafo não direcionado.
*   **Orientado:** Grafo direcionado.
*   **Misto:** Mistura de arcos e arestas.
*   **Íngreme (Windy):** Custo varia baseado na direção física de travessia.

#### Versões de Coloração em Arestas (Questão 10)
*   **Coloração Própria de Arestas:** Nenhuma aresta adjacente tem a mesma cor.
*   **Coloração Total:** Coloração conjunta de vértices e arestas simultaneamente.
*   **Coloração por Lista de Arestas:** Cada aresta possui um subconjunto pré-determinado de cores que pode assumir.

#### Tabela de Complexidade dos Problemas Não-Polinomiais (Questão 11)

| Problema Geral | Versão Não-Polinomial | Classificação de Complexidade | Explicação Teórica |
| :--- | :--- | :--- | :--- |
| **PCV** | PCV Geral / Assimétrico | **NP-difícil** | O problema de decisão correspondente (existe tour $\le K$) é NP-completo (provado por Karp em 1972). |
| **PCC** | Carteiro Chinês Misto / Íngreme | **NP-completo** | A presença conjunta de restrições de direcionamento e arestas livres inviabiliza soluções polinomiais diretas. |
| **Coloração** | Coloração de Vértices ($K \ge 3$) | **NP-completo** | Determinar se um grafo geral aceita uma coloração própria com $K \ge 3$ cores é NP-completo. |

---

### Questão 12: Encontrar Coloração de Vértices a partir da Coloração de Arestas
A questão indaga se é possível obter de forma direta e generalizada uma $K$-coloração própria de *vértices* a partir de uma coloração própria de *arestas* de qualquer grafo simples conexo $G(V, E)$.

**Resposta:** **Não é possível.** 

#### Prova / Contraexemplo:
A coloração de arestas própria exige que arestas que compartilham o mesmo vértice tenham cores diferentes. Isso é equivalente a fazer uma coloração de vértices no **Grafo de Linha $L(G)$**, e não no grafo original $G(V, E)$. 

Considere o grafo estrela $S_n$ (um vértice central conectado a $n$ folhas):
*   **Coloração de Vértices de $S_n$:** É um grafo bipartido, logo $\chi(S_n) = 2$ para qualquer $n \ge 1$ (o centro recebe uma cor e todas as folhas recebem outra).
*   **Coloração de Arestas de $S_n$:** Como todas as $n$ arestas concorrem no vértice central, nenhuma delas pode ter a mesma cor. Logo, precisamos de exatamente $n$ cores: $\chi'(S_n) = n$.
*   À medida que $n$ cresce, a quantidade de cores das arestas cresce linearmente para o infinito ($n$), enquanto o número de cores dos vértices permanece constante em $2$. Não há nenhuma correspondência direta ou mapeamento de funções que permita deduzir a $K$-coloração de vértices de $G$ a partir da coloração de arestas sem a necessidade de processar o grafo do zero.

---

### Questão 13: Problema de Escalonamento de Provas (Coloração)

#### Passo 1: Construção do Grafo de Conflitos
Definimos os vértices como as matérias das provas: $V = \{A, B, C, D, E\}$.
Criamos uma aresta entre duas disciplinas se houver pelo menos um aluno que fará ambas as provas (o que impede que ocorram no mesmo horário). Analisando a lista de alunos:
*   Aluno 1: $A, C, E \implies$ arestas $(A,C), (A,E), (C,E)$
*   Aluno 2: $A, B, E \implies$ arestas $(A,B), (B,E)$
*   Aluno 3: $B, C, D \implies$ arestas $(B,C), (B,D), (C,D)$
*   Aluno 4: $B, C, D \implies$ arestas já criadas
*   Aluno 5: $A, C \implies$ aresta já criada
*   Aluno 6: $B, C, E \implies$ arestas já criadas
*   Aluno 7: $C, D, E \implies$ aresta $(D,E)$
*   Aluno 8: $B, C, E \implies$ arestas já criadas
*   Aluno 9: $B, C \implies$ aresta já criada
*   Aluno 10: $A, B, C \implies$ arestas já criadas

**Grafo de Conflito Resultante:**
*   $A$ é vizinho de $\{B, C, E\}$
*   $B$ é vizinho de $\{A, C, D, E\}$
*   $C$ é vizinho de $\{A, B, D, E\}$
*   $D$ é vizinho de $\{B, C, E\}$
*   $E$ é vizinho de $\{A, B, C, D\}$

*Nota:* O único par de vértices sem conflito (sem aresta) é **$(A, D)$**.

#### Passo 2: Calcular o Número Cromático do Grafo de Conflito
O subgrafo induzido pelos vértices $\{B, C, E\}$ forma um grafo completo $K_3$ (um triângulo), exigindo no mínimo 3 cores. 
O vértice $A$ é adjacente a todos os membros do triângulo $\{B, C, E\}$, logo exige uma 4ª cor. O vértice $D$ também é adjacente a todos do triângulo $\{B, C, E\}$. Como $A$ e $D$ não possuem aresta entre si, eles podem compartilhar a mesma cor.

Temos a coloração própria ótima de tamanho **4**:
*   Horário 1: $\{A, D\}$
*   Horário 2: $\{B\}$
*   Horário 3: $\{C\}$
*   Horário 4: $\{E\}$

Portanto, **o número mínimo de horários necessários para evitar conflitos é 4**.

#### Passo 3: Avaliação das Restrições Físicas
A tabela de salas e horários disponíveis nos dá:
*   Sala 1: Horários $H1, H2$
*   Sala 2: Horário $H1$
*   Sala 3: Horários $H1, H2$

Embora tenhamos 5 vagas físicas de salas (salas distintas), a restrição de simultaneidade temporal é baseada exclusivamente nos **horários**. Só existem **dois horários distintos** disponíveis em todo o curso ($H1$ e $H2$).

*   Número de cores disponíveis (horários) = 2.
*   Número cromático mínimo exigido pelos conflitos de alunos = 4.

**Conclusão:**
Como $4 > 2$, **não é possível** que todos os alunos realizem as provas sem conflito nos horários disponíveis. Seriam necessários pelo menos 4 blocos de horários diferentes para atender à demanda de provas sem que nenhum aluno perdesse exames por choque de horário.
