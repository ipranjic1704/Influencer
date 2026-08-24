package hr.algebra.influencer.Model;

import java.util.Objects;

public abstract class Entitet implements Identifiable
{

    protected int id;

    protected Entitet()
    {
    }

    protected Entitet(int id)
    {
        this.id = id;
    }

    @Override
    public int getId()
    {
        return id;
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    public abstract String opisi();

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Entitet) o).id;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getClass(), id);
    }
}
