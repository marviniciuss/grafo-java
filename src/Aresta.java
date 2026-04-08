public class Aresta {
    int idDestino;
    double custo;
    String caracteristica;
    Aresta proxima; // O ponteiro para o próximo item da lista encadeada

    public Aresta(int idDestino, double custo, String caracteristica) {
        this.idDestino = idDestino;
        this.custo = custo;
        this.caracteristica = caracteristica;
        this.proxima = null;
    }
}