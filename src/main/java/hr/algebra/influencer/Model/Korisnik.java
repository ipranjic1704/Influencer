package hr.algebra.influencer.Model;

import hr.algebra.influencer.Model.Enum.Uloga;

public class Korisnik extends Entitet implements Comparable<Korisnik>
{

    private String korisnickoIme;
    private String lozinka;
    private Uloga uloga;
    private Integer influencerId;

    public Korisnik()
    {
    }

    public Korisnik(String korisnickoIme, String lozinka, Uloga uloga)
    {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.uloga = uloga;
    }

    public Korisnik(int id, String korisnickoIme, String lozinka, Uloga uloga)
    {
        super(id);
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.uloga = uloga;
    }

    public Korisnik(int id, String korisnickoIme, String lozinka, Uloga uloga, Integer influencerId)
    {
        super(id);
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.uloga = uloga;
        this.influencerId = influencerId;
    }

    public String getKorisnickoIme()
    {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme)
    {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka()
    {
        return lozinka;
    }

    public void setLozinka(String lozinka)
    {
        this.lozinka = lozinka;
    }

    public Uloga getUloga()
    {
        return uloga;
    }

    public void setUloga(Uloga uloga)
    {
        this.uloga = uloga;
    }

    public Integer getInfluencerId()
    {
        return influencerId;
    }

    public void setInfluencerId(Integer influencerId)
    {
        this.influencerId = influencerId;
    }

    @Override
    public String opisi()
    {
        return korisnickoIme + " (" + uloga + ")";
    }

    @Override
    public int compareTo(Korisnik other)
    {
        return korisnickoIme.compareToIgnoreCase(other.korisnickoIme);
    }

    @Override
    public String toString()
    {
        return korisnickoIme;
    }
}
