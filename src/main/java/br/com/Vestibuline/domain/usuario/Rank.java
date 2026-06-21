package br.com.Vestibuline.domain.usuario;

public enum Rank {
    DIAMANTE("Diamante"),
    OURO("Ouro"),
    PRATA("Prata"),
    BRONZE("Bronze");

    private final String label;

    Rank(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
