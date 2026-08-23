package hr.algebra.influencer.Model.Enum;

public enum Uloga {
    ADMIN("Administrator"),
    BREND("Brend"),
    INFLUENCER("Influencer");

    private final String naziv;

    Uloga(String naziv) {
        this.naziv = naziv;
    }

    public boolean smijeUredivati() {
        return this == ADMIN;
    }

    public boolean smijeDodatiSuradnju() {
        return this == ADMIN || this == BREND;
    }

    @Override
    public String toString() {
        return naziv;
    }
}
