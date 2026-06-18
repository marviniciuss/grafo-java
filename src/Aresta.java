public class Aresta {
    int idDestino;
    double custo; // Representa a capacidade da aresta na rede de fluxo
    String caracteristica;
    Aresta proxima; // O ponteiro para o próximo item da lista encadeada

    // Atributos adicionais para Redes de Fluxo
    double fluxo;                  // Fluxo atual na aresta
    Aresta arestaReversa;          // Referência direta à aresta reversa no grafo residual
    boolean ehReversaProvisoria;   // Indica se é uma aresta reversa adicionada temporariamente

    public Aresta(int idDestino, double custo, String caracteristica) {
        this.idDestino = idDestino;
        this.custo = custo;
        this.caracteristica = caracteristica;
        this.proxima = null;
        this.fluxo = 0.0;
        this.arestaReversa = null;
        this.ehReversaProvisoria = false;
    }
}