package hr.algebra.influencer.Model;

// Platforma na kojoj je influencer aktivan (Instagram, TikTok, YouTube, Twitch...).
// Jednostavan šifrarnik - influencer se s njim povezuje M:N vezom.
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

    // Prirodni poredak je padajuci (Z-A) po nazivu - vidi Influencer.compareTo() za isti obrazac.
    @Override
    public int compareTo(Platforma other) {
        return other.naziv.compareToIgnoreCase(naziv);
    }

    @Override
    public String toString() {
        return naziv;
    }
}
