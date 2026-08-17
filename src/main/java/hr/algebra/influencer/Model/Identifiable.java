package hr.algebra.influencer.Model;

// Sučelje koje mora implementirati svaki entitet koji se sprema u bazu.
// Repozitoriji preko njega mogu generički raditi s ID-em bez obzira o kojem se entitetu radi.
public interface Identifiable {
    int getId();
    void setId(int id);
}
