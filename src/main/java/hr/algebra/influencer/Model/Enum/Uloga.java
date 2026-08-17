package hr.algebra.influencer.Model.Enum;

// Uloga korisnika aplikacije - ograničava pristup admin funkcijama (dodavanje, uređivanje, brisanje).
public enum Uloga {
    ADMIN,
    KORISNIK;

    public boolean smijeUredivati() {
        return this == ADMIN;
    }
}
