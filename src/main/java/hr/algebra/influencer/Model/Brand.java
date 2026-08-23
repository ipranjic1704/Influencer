package hr.algebra.influencer.Model;

public class Brand extends Entitet implements Comparable<Brand> {

    private String naziv;

    public Brand() {
    }

    public Brand(String naziv) {
        this.naziv = naziv;
    }

    public Brand(int id, String naziv) {
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
        return "Brand: " + naziv;
    }

    @Override
    public int compareTo(Brand other) {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
