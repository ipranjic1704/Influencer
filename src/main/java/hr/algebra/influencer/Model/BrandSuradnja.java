package hr.algebra.influencer.Model;

import hr.algebra.influencer.Model.Enum.StatusSuradnje;

import java.util.ArrayList;
import java.util.List;

// Brand suradnja - konkretna marketinška kampanja s konkretnim brendom.
// Za razliku od Platforme/Niše/TipaSadržaja ovo nije čisti šifrarnik nego "spremnik" za tim
// influencera (M:N), isto kao što Akvarij drži svoje organizme - influenceri se u tim dodaju drag & dropom.
public class BrandSuradnja extends Entitet implements Comparable<BrandSuradnja> {

    private String nazivKampanje;
    private String nazivBrenda;
    private int godina;
    private StatusSuradnje status;
    private List<Influencer> tim = new ArrayList<>();

    public BrandSuradnja() {
    }

    public BrandSuradnja(String nazivKampanje, String nazivBrenda, int godina, StatusSuradnje status) {
        this.nazivKampanje = nazivKampanje;
        this.nazivBrenda = nazivBrenda;
        this.godina = godina;
        this.status = status;
    }

    public BrandSuradnja(int id, String nazivKampanje, String nazivBrenda, int godina, StatusSuradnje status) {
        super(id);
        this.nazivKampanje = nazivKampanje;
        this.nazivBrenda = nazivBrenda;
        this.godina = godina;
        this.status = status;
    }

    public String getNazivKampanje() {
        return nazivKampanje;
    }

    public void setNazivKampanje(String nazivKampanje) {
        this.nazivKampanje = nazivKampanje;
    }

    public String getNazivBrenda() {
        return nazivBrenda;
    }

    public void setNazivBrenda(String nazivBrenda) {
        this.nazivBrenda = nazivBrenda;
    }

    public int getGodina() {
        return godina;
    }

    public void setGodina(int godina) {
        this.godina = godina;
    }

    public StatusSuradnje getStatus() {
        return status;
    }

    public void setStatus(StatusSuradnje status) {
        this.status = status;
    }

    public List<Influencer> getTim() {
        return tim;
    }

    public void setTim(List<Influencer> tim) {
        this.tim = tim;
    }

    @Override
    public String opisi() {
        return nazivKampanje + " x " + nazivBrenda + " (" + godina + ", " + status + ", tim: " + tim.size() + ")";
    }

    // Prirodni poredak je padajuci (Z-A) po nazivu kampanje - vidi Influencer.compareTo() za isti obrazac.
    @Override
    public int compareTo(BrandSuradnja other) {
        return other.nazivKampanje.compareToIgnoreCase(nazivKampanje);
    }

    @Override
    public String toString() {
        return nazivKampanje;
    }
}
