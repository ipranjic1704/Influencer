package hr.algebra.influencer.Model.Enum;

public enum StatusSuradnje {
    PLANIRANA("Planirana"),
    AKTIVNA("Aktivna"),
    ZAVRSENA("Završena");

    private final String naziv;

    StatusSuradnje(String naziv) {
        this.naziv = naziv;
    }

    public boolean jeZavrsena() {
        return this == ZAVRSENA;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
