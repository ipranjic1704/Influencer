package hr.algebra.influencer.Model;

import java.util.ArrayList;
import java.util.List;
public class Influencer extends Entitet implements Comparable<Influencer>
{

    private String imeNadimak;
    private int brojPratitelja;
    private double engagementRate;
    private String zemlja;
    private Grad grad;
    private String jezikSadrzaja;
    private String profilnaSlika;

    private List<Platforma> platforme = new ArrayList<>();
    private List<Nisa> nise = new ArrayList<>();
    private List<TipSadrzaja> tipoviSadrzaja = new ArrayList<>();
    private List<BrandSuradnja> brandSuradnje = new ArrayList<>();

    public Influencer()
    {
    }

    public Influencer(String imeNadimak, int brojPratitelja, double engagementRate, String zemlja, Grad grad, String jezikSadrzaja, String profilnaSlika)
    {
        this.imeNadimak = imeNadimak;
        this.brojPratitelja = brojPratitelja;
        this.engagementRate = engagementRate;
        this.zemlja = zemlja;
        this.grad = grad;
        this.jezikSadrzaja = jezikSadrzaja;
        this.profilnaSlika = profilnaSlika;
    }

    public Influencer(int id, String imeNadimak, int brojPratitelja, double engagementRate, String zemlja, Grad grad, String jezikSadrzaja, String profilnaSlika)
    {
        super(id);
        this.imeNadimak = imeNadimak;
        this.brojPratitelja = brojPratitelja;
        this.engagementRate = engagementRate;
        this.zemlja = zemlja;
        this.grad = grad;
        this.jezikSadrzaja = jezikSadrzaja;
        this.profilnaSlika = profilnaSlika;
    }

    public String getImeNadimak()
    {
        return imeNadimak;
    }

    public void setImeNadimak(String imeNadimak)
    {
        this.imeNadimak = imeNadimak;
    }

    public int getBrojPratitelja()
    {
        return brojPratitelja;
    }

    public void setBrojPratitelja(int brojPratitelja)
    {
        this.brojPratitelja = brojPratitelja;
    }

    public double getEngagementRate()
    {
        return engagementRate;
    }

    public void setEngagementRate(double engagementRate)
    {
        this.engagementRate = engagementRate;
    }

    public String getZemlja()
    {
        return zemlja;
    }

    public void setZemlja(String zemlja)
    {
        this.zemlja = zemlja;
    }

    public Grad getGrad()
    {
        return grad;
    }

    public void setGrad(Grad grad)
    {
        this.grad = grad;
    }

    public String getJezikSadrzaja()
    {
        return jezikSadrzaja;
    }

    public void setJezikSadrzaja(String jezikSadrzaja)
    {
        this.jezikSadrzaja = jezikSadrzaja;
    }

    public String getProfilnaSlika()
    {
        return profilnaSlika;
    }

    public void setProfilnaSlika(String profilnaSlika)
    {
        this.profilnaSlika = profilnaSlika;
    }

    public List<Platforma> getPlatforme()
    {
        return platforme;
    }

    public void setPlatforme(List<Platforma> platforme)
    {
        this.platforme = platforme;
    }

    public List<Nisa> getNise()
    {
        return nise;
    }

    public void setNise(List<Nisa> nise)
    {
        this.nise = nise;
    }

    public List<TipSadrzaja> getTipoviSadrzaja()
    {
        return tipoviSadrzaja;
    }

    public void setTipoviSadrzaja(List<TipSadrzaja> tipoviSadrzaja)
    {
        this.tipoviSadrzaja = tipoviSadrzaja;
    }

    public List<BrandSuradnja> getBrandSuradnje()
    {
        return brandSuradnje;
    }

    public void setBrandSuradnje(List<BrandSuradnja> brandSuradnje)
    {
        this.brandSuradnje = brandSuradnje;
    }

    @Override
    public String opisi()
    {
        return imeNadimak + " (" + zemlja + ", " + brojPratitelja + " pratitelja, " + engagementRate + "% engagement)";
    }

    @Override
    public int compareTo(Influencer other)
    {
        return imeNadimak.compareToIgnoreCase(other.imeNadimak);
    }

    @Override
    public String toString()
    {
        return imeNadimak;
    }
}
