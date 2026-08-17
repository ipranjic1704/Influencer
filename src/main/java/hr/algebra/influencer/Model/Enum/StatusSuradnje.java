package hr.algebra.influencer.Model.Enum;

// Status brand suradnje kroz njen životni ciklus.
public enum StatusSuradnje {
    PLANIRANA,
    AKTIVNA,
    ZAVRSENA;

    public boolean jeZavrsena() {
        return this == ZAVRSENA;
    }
}
