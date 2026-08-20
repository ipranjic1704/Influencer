package hr.algebra.influencer.Model;

import java.util.ArrayList;
import java.util.List;
// Glavni entitet aplikacije. Drži svoje osnovne atribute te liste povezanih entiteta
// (platforme, niše, tipovi sadržaja su M:N šifrarnici; brand suradnje su njegove vlastite, 1:N).
public class Influencer extends Entitet implements Comparable<Influencer> {

    private String imeNadimak;
    private int brojPratitelja;
    private double engagementRate;
    private String zemlja;
    private String jezikSadrzaja;
    private String profilnaSlika;

    private List<Platforma> platforme = new ArrayList<>();
    private List<Nisa> nise = new ArrayList<>();
    private List<TipSadrzaja> tipoviSadrzaja = new ArrayList<>();
    private List<BrandSuradnja> brandSuradnje = new ArrayList<>();

    public Influencer() {
    }

    public Influencer(String imeNadimak, int brojPratitelja, double engagementRate, String zemlja, String jezikSadrzaja, String profilnaSlika) {
        this.imeNadimak = imeNadimak;
        this.brojPratitelja = brojPratitelja;
        this.engagementRate = engagementRate;
        this.zemlja = zemlja;
        this.jezikSadrzaja = jezikSadrzaja;
        this.profilnaSlika = profilnaSlika;
    }

    public Influencer(int id, String imeNadimak, int brojPratitelja, double engagementRate, String zemlja, String jezikSadrzaja, String profilnaSlika) {
        super(id);
        this.imeNadimak = imeNadimak;
        this.brojPratitelja = brojPratitelja;
        this.engagementRate = engagementRate;
        this.zemlja = zemlja;
        this.jezikSadrzaja = jezikSadrzaja;
        this.profilnaSlika = profilnaSlika;
    }

    public String getImeNadimak() {
        return imeNadimak;
    }

    public void setImeNadimak(String imeNadimak) {
        this.imeNadimak = imeNadimak;
    }

    public int getBrojPratitelja() {
        return brojPratitelja;
    }

    public void setBrojPratitelja(int brojPratitelja) {
        this.brojPratitelja = brojPratitelja;
    }

    public double getEngagementRate() {
        return engagementRate;
    }

    public void setEngagementRate(double engagementRate) {
        this.engagementRate = engagementRate;
    }

    public String getZemlja() {
        return zemlja;
    }

    public void setZemlja(String zemlja) {
        this.zemlja = zemlja;
    }

    public String getJezikSadrzaja() {
        return jezikSadrzaja;
    }

    public void setJezikSadrzaja(String jezikSadrzaja) {
        this.jezikSadrzaja = jezikSadrzaja;
    }

    public String getProfilnaSlika() {
        return profilnaSlika;
    }

    public void setProfilnaSlika(String profilnaSlika) {
        this.profilnaSlika = profilnaSlika;
    }

    public List<Platforma> getPlatforme() {
        return platforme;
    }

    public void setPlatforme(List<Platforma> platforme) {
        this.platforme = platforme;
    }

    public List<Nisa> getNise() {
        return nise;
    }

    public void setNise(List<Nisa> nise) {
        this.nise = nise;
    }

    public List<TipSadrzaja> getTipoviSadrzaja() {
        return tipoviSadrzaja;
    }

    public void setTipoviSadrzaja(List<TipSadrzaja> tipoviSadrzaja) {
        this.tipoviSadrzaja = tipoviSadrzaja;
    }

    public List<BrandSuradnja> getBrandSuradnje() {
        return brandSuradnje;
    }

    public void setBrandSuradnje(List<BrandSuradnja> brandSuradnje) {
        this.brandSuradnje = brandSuradnje;
    }

    @Override
    public String opisi() {
        return imeNadimak + " (" + zemlja + ", " + brojPratitelja + " pratitelja, " + engagementRate + "% engagement)";
    }

    // Prirodni poredak je padajuci (Z-A) po imenu/nadimku - tako se influenceri
    // prikazuju svugdje u aplikaciji, pa je logika ovdje, a ne u kontrolerima.
    @Override
    public int compareTo(Influencer other) {
        return other.imeNadimak.compareToIgnoreCase(imeNadimak);
    }

    @Override
    public String toString() {
        return imeNadimak;
    }
}
