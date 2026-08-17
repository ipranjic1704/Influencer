package hr.algebra.influencer.Model.Enum;

// Uloga korisnika aplikacije.
// ADMIN   - puni pristup, administracija šifrarnika i korisnika.
// BREND   - predstavlja brend; smije kreirati brand suradnje i dodavati influencere u tim kampanje.
// INFLUENCER - predstavlja influencera; vidi svoj profil i suradnje, ali ih ne smije sam kreirati.
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
