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

    @Override
    public int compareTo(Nisa other) {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
