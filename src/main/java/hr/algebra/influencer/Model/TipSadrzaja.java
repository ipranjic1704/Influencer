package hr.algebra.influencer.Model;

// Tip sadržaja koji influencer proizvodi (reels, shorts, recenzije, tutoriali...).
public class TipSadrzaja extends Entitet implements Comparable<TipSadrzaja> {

    private String naziv;

    public TipSadrzaja() {
    }

    public TipSadrzaja(String naziv) {
        this.naziv = naziv;
    }

    public TipSadrzaja(int id, String naziv) {
        super(id);
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @Override
    public String opisi() {
        return "Tip sadržaja: " + naziv;
    }

    // Prirodni poredak je uzlazni (A-Z) po nazivu - vidi Influencer.compareTo() za isti obrazac.
    @Override
    public int compareTo(TipSadrzaja other) {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
