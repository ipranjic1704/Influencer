package hr.algebra.influencer.Model;

import hr.algebra.influencer.Model.Enum.StatusSuradnje;

import java.util.ArrayList;
import java.util.List;

public class BrandSuradnja extends Entitet implements Comparable<BrandSuradnja>
{

    private String nazivKampanje;
    private Brand brand;
    private int godina;
    private StatusSuradnje status;
    private List<Influencer> tim = new ArrayList<>();

    public BrandSuradnja()
    {
    }

    public BrandSuradnja(String nazivKampanje, Brand brand, int godina, StatusSuradnje status)
    {
        this.nazivKampanje = nazivKampanje;
        this.brand = brand;
        this.godina = godina;
        this.status = status;
    }

    public BrandSuradnja(int id, String nazivKampanje, Brand brand, int godina, StatusSuradnje status)
    {
        super(id);
        this.nazivKampanje = nazivKampanje;
        this.brand = brand;
        this.godina = godina;
        this.status = status;
    }

    public String getNazivKampanje()
    {
        return nazivKampanje;
    }

    public void setNazivKampanje(String nazivKampanje)
    {
        this.nazivKampanje = nazivKampanje;
    }

    public Brand getBrand()
    {
        return brand;
    }

    public void setBrand(Brand brand)
    {
        this.brand = brand;
    }

    public int getGodina()
    {
        return godina;
    }

    public void setGodina(int godina)
    {
        this.godina = godina;
    }

    public StatusSuradnje getStatus()
    {
        return status;
    }

    public void setStatus(StatusSuradnje status)
    {
        this.status = status;
    }

    public List<Influencer> getTim()
    {
        return tim;
    }

    public void setTim(List<Influencer> tim)
    {
        this.tim = tim;
    }

    @Override
    public String opisi()
    {
        return nazivKampanje + " x " + brand.getNaziv() + " (" + godina + ", " + status + ", tim: " + tim.size() + ")";
    }

    @Override
    public int compareTo(BrandSuradnja other)
    {
        return nazivKampanje.compareToIgnoreCase(other.nazivKampanje);
    }

    @Override
    public String toString()
    {
        return nazivKampanje;
    }
}
