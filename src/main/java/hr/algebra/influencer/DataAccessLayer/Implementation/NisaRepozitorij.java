package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Nisa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NisaRepozitorij implements Repozitorij<Nisa>
{

    private static final NisaRepozitorij INSTANCA = new NisaRepozitorij();

    private NisaRepozitorij()
    {
    }

    public static NisaRepozitorij getInstance()
    {
        return INSTANCA;
    }

    // Lazy verzija (umjesto Eager gore):
    // private static volatile NisaRepozitorij instanca;
    //
    // public static NisaRepozitorij getInstance()
    // {
    //     NisaRepozitorij rezultat = instanca;
    //     if (rezultat == null)
    //     {
    //         synchronized (NisaRepozitorij.class)
    //         {
    //             rezultat = instanca;
    //             if (rezultat == null)
    //             {
    //                 instanca = rezultat = new NisaRepozitorij();
    //             }
    //         }
    //     }
    //     return rezultat;
    // }

    private static final String SELECT_ALL =
            "SELECT IDNisa, Naziv FROM Nisa";

    private static final String SELECT_BY_ID =
            "SELECT IDNisa, Naziv FROM Nisa WHERE IDNisa = ?";

    private static final String INSERT =
            "INSERT INTO Nisa (Naziv) VALUES (?)";

    private static final String UPDATE =
            "UPDATE Nisa SET Naziv = ? WHERE IDNisa = ?";

    private static final String DELETE =
            "DELETE FROM Nisa WHERE IDNisa = ?";

    @Override
    public List<Nisa> getAll()
    {
        List<Nisa> nise = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                nise.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu nisa.", e);
        }
        return nise;
    }

    @Override
    public Optional<Nisa> getById(int id)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID))
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu nise id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void create(Nisa nisa)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, nisa.getNaziv());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys())
            {
                if (kljucevi.next())
                {
                    nisa.setId(kljucevi.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju nise.", e);
        }
    }

    @Override
    public void update(Nisa nisa)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE))
        {
            ps.setString(1, nisa.getNaziv());
            ps.setInt(2, nisa.getId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri azuriranju nise.", e);
        }
    }

    @Override
    public void delete(int id)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE))
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri brisanju nise id=" + id, e);
        }
    }

    private Nisa mapRow(ResultSet rs) throws SQLException
    {
        return new Nisa(rs.getInt("IDNisa"), rs.getString("Naziv"));
    }
}
