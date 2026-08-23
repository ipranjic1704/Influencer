package hr.algebra.influencer.Model;

// Grad iz kojeg influencer dolazi - sifrarnik uvezen s vanjskog API-ja (uz mogucnost i rucnog dodavanja).
public class Grad extends Entitet implements Comparable<Grad> {

    private String naziv;

    public Grad() {
    }

    public Grad(String naziv) {
        this.naziv = naziv;
    }

    public Grad(int id, String naziv) {
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
        return "Grad: " + naziv;
    }

    // Prirodni poredak je uzlazni (A-Z) po nazivu - vidi Influencer.compareTo() za isti obrazac.
    @Override
    public int compareTo(Grad other) {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
