package hr.algebra.influencer.Model;

public class Platforma extends Entitet implements Comparable<Platforma> {

    private String naziv;

    public Platforma() {
    }

    public Platforma(String naziv) {
        this.naziv = naziv;
    }

    public Platforma(int id, String naziv) {
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
        return "Platforma: " + naziv;
    }

    @Override
    public int compareTo(Platforma other) {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
