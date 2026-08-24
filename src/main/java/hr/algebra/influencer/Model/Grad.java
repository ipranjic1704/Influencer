package hr.algebra.influencer.Model;

public class Grad extends Entitet implements Comparable<Grad>
{

    private String naziv;

    public Grad()
    {
    }

    public Grad(String naziv)
    {
        this.naziv = naziv;
    }

    public Grad(int id, String naziv)
    {
        super(id);
        this.naziv = naziv;
    }

    public String getNaziv()
    {
        return naziv;
    }

    public void setNaziv(String naziv)
    {
        this.naziv = naziv;
    }

    @Override
    public String opisi()
    {
        return "Grad: " + naziv;
    }

    @Override
    public int compareTo(Grad other)
    {
        return naziv.compareToIgnoreCase(other.naziv);
    }

    @Override
    public String toString()
    {
        return naziv;
    }
}
