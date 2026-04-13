import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Agora temos um ambiente que suporta os 2 grafos exigidos pelo professor
        Grafo[] ambienteGrafos = new Grafo[2];
        ambienteGrafos[0] = new Grafo(10); // G1
        ambienteGrafos[1] = new Grafo(10); // G2

        int indiceAtivo = 0; // Começamos editando o G1 (índice 0)
        int opcao = -1;

        while (opcao != 0) {
            Grafo grafoAtual = ambienteGrafos[indiceAtivo]; // Aponta para o grafo selecionado

            System.out.println("\n========================================");
            System.out.println("   PAINEL DE CONTROLE - EDITANDO G" + (indiceAtivo + 1));
            System.out.println("========================================");
            System.out.println("1. Resetar Grafo Atual");
            System.out.println("2. Incluir Vértice");
            System.out.println("3. Remover Vértice");
            System.out.println("4. Incluir Aresta");
            System.out.println("5. Remover Aresta");
            System.out.println("6. Mostrar Grafo Atual");
            System.out.println("7. Preenchimento Automático");
            System.out.println("----------------------------------------");
            System.out.println("8.  [Q8]  Gerar Subgrafo Maximal Árvore (Kruskal)"); // <-- NOVA OPÇÃO AQUI
            System.out.println("16. [Q6]  Executar Travessia In-Order");
            System.out.println("17. [Q7]  Executar Travessia Pos-Order (DFS)");
            System.out.println("18. [Q18] Executar Travessia BFS (Kahn)");
            System.out.println("----------------------------------------");
            System.out.println("88. [?] COMPARAR G1 e G2 (Isomorfismo/Similaridade)");
            System.out.println("99. [!] ALTERNAR GRAFO ATIVO (Mudar para G" + (indiceAtivo == 0 ? "2" : "1") + ")");
            System.out.println("0. Sair do Programa");
            System.out.println("----------------------------------------");
            System.out.print("-> Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.print("Digite a capacidade inicial desejada para G" + (indiceAtivo + 1) + ": ");
                        int cap = scanner.nextInt();
                        ambienteGrafos[indiceAtivo] = new Grafo(cap);
                        System.out.println("[!] G" + (indiceAtivo + 1) + " resetado com sucesso.");
                        break;

                    case 2:
                        System.out.print("ID do Vértice: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Rótulo do Vértice: ");
                        String rotulo = scanner.nextLine();
                        grafoAtual.incluirVertice(id, rotulo);
                        System.out.println("[+] Vértice " + id + " incluído em G" + (indiceAtivo + 1) + ".");
                        break;

                    case 3:
                        System.out.print("ID do Vértice a remover: ");
                        int idRem = scanner.nextInt();
                        grafoAtual.removerVertice(idRem);
                        System.out.println("[-] Vértice removido de G" + (indiceAtivo + 1) + ".");
                        break;

                    case 4:
                        System.out.print("Origem: ");
                        int origem = scanner.nextInt();
                        System.out.print("Destino: ");
                        int destino = scanner.nextInt();
                        System.out.print("Custo (use vírgula): ");
                        double custo = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Característica: ");
                        String carac = scanner.nextLine();
                        grafoAtual.incluirAresta(origem, destino, custo, carac);
                        System.out.println("[+] Aresta incluída em G" + (indiceAtivo + 1) + ".");
                        break;

                    case 5:
                        System.out.print("Origem: ");
                        int oRem = scanner.nextInt();
                        System.out.print("Destino: ");
                        int dRem = scanner.nextInt();
                        grafoAtual.removerAresta(oRem, dRem);
                        System.out.println("[-] Aresta removida de G" + (indiceAtivo + 1) + ".");
                        break;

                    case 6:
                        System.out.println("\n--- ESTRUTURA DO G" + (indiceAtivo + 1) + " ---");
                        grafoAtual.mostrarGrafo();
                        break;

                    case 7:
                        System.out.print("Qtd de vértices: ");
                        int qV = scanner.nextInt();
                        System.out.print("Qtd de arestas: ");
                        int qA = scanner.nextInt();
                        grafoAtual.preenchimentoAutomatico(qV, qA);
                        System.out.println("[!] G" + (indiceAtivo + 1) + " populado.");
                        break;

                    case 8: // QUESTÃO 8
                        System.out.println("\n--- EXECUTANDO QUESTÃO 8 NO G" + (indiceAtivo + 1) + " ---");
                        grafoAtual.subgrafoMaximalArvore();
                        break;


                    case 99: // ALTERNAR GRAFO
                        indiceAtivo = (indiceAtivo == 0) ? 1 : 0;
                        System.out.println(">> Você agora está editando o G" + (indiceAtivo + 1));
                        break;

                    case 88: // COMPARAÇÃO G1 E G2
                        System.out.println("\n--- COMPARANDO ESTRUTURAS ---");
                        System.out.println("Grafo 1 (G1):");
                        ambienteGrafos[0].mostrarGrafo();
                        System.out.println("Grafo 2 (G2):");
                        ambienteGrafos[1].mostrarGrafo();

                        System.out.println("\n[Processando Isomorfismo...]");
                        boolean isomorfos = ambienteGrafos[0].verificarIsomorfismo(ambienteGrafos[1]);

                        if (isomorfos) {
                            System.out.println(">>> RESULTADO DA QUESTÃO 9: SIM! Os grafos G1 e G2 SÃO isomorfos.");
                        } else {
                            System.out.println(">>> RESULTADO DA QUESTÃO 9: NÃO! Os grafos G1 e G2 NÃO são isomorfos.");
                        }

                        System.out.println("\n[Aviso: As similaridades de Jaccard/Cosseno da Q10 serão implementadas aqui em breve]");
                        break;

                    case 16:
                        System.out.println("\n--- EXECUTANDO QUESTÃO 6 (IN-ORDER) NO G" + (indiceAtivo + 1) + " ---");
                        grafoAtual.travessiaInOrdem();
                        break;

                    case 17:
                        System.out.println("\n--- EXECUTANDO QUESTÃO 7 (POS-ORDER) NO G" + (indiceAtivo + 1) + " ---");
                        grafoAtual.travessiaPosOrdem();
                        break;

                    case 18:
                        System.out.println("\n--- EXECUTANDO QUESTÃO 18 (BFS) NO G" + (indiceAtivo + 1) + " ---");
                        grafoAtual.bfsOrdenacaoTopologica();
                        break;

                    case 0:
                        System.out.println("Encerrando...");
                        break;

                    default:
                        System.out.println("[x] Opção inválida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n[x] Erro Crítico: Digite apenas números.");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
}