package hr.algebra.influencer.Model;

// Niša sadržaja kojim se influencer bavi (beauty, fitness, tech, lifestyle, gaming...).
public class Nisa extends Entitet implements Comparable<Nisa> {

    private String naziv;

    public Nisa() {
    }

    public Nisa(String naziv) {
        this.naziv = naziv;
    }

    public Nisa(int id, String naziv) {
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
        return "Niša: " + naziv;
    }

    // Prirodni poredak je padajuci (Z-A) po nazivu - vidi Influencer.compareTo() za isti obrazac.
    @Override
    public int compareTo(Nisa other) {
        return other.naziv.compareToIgnoreCase(naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
