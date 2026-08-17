package hr.algebra.influencer.Model;

import java.util.Objects;

// Zajednička apstraktna nadklasa za sve modele u aplikaciji.
// Ovdje držim ono što je svim entitetima identično: ID i identitet (equals/hashCode preko ID-a),
// pa to ne moram ponavljati u svakoj podklasi.
// opisi() ostaje apstraktan - svaka podklasa ga mora ispuniti na svoj način (polimorfizam),
// koristim ga npr. za kratki opis retka pri XML izvozu.
public abstract class Entitet implements Identifiable
{

    protected int id;

    protected Entitet() {
    }

    protected Entitet(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public abstract String opisi();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Entitet) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
