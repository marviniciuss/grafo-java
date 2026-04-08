public class Vertice {
    int id;
    String caracteristica;
    Aresta inicioLista; // Aponta para a primeira aresta que sai deste vértice

    public Vertice(int id, String caracteristica) {
        this.id = id;
        this.caracteristica = caracteristica;
        this.inicioLista = null;
    }
}