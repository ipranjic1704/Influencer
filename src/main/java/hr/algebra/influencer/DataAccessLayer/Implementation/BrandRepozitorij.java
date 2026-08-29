package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Brand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrandRepozitorij implements Repozitorij<Brand>
{

    private static final BrandRepozitorij INSTANCA = new BrandRepozitorij();

    private BrandRepozitorij()
    {
    }

    public static BrandRepozitorij getInstance()
    {
        return INSTANCA;
    }

    // Lazy verzija (umjesto Eager gore):
    // private static volatile BrandRepozitorij instanca;
    //
    // public static BrandRepozitorij getInstance()
    // {
    //     BrandRepozitorij rezultat = instanca;
    //     if (rezultat == null)
    //     {
    //         synchronized (BrandRepozitorij.class)
    //         {
    //             rezultat = instanca;
    //             if (rezultat == null)
    //             {
    //                 instanca = rezultat = new BrandRepozitorij();
    //             }
    //         }
    //     }
    //     return rezultat;
    // }

    private static final String SELECT_ALL =
            "SELECT IDBrand, Naziv FROM Brand";

    private static final String SELECT_BY_ID =
            "SELECT IDBrand, Naziv FROM Brand WHERE IDBrand = ?";

    private static final String INSERT =
            "INSERT INTO Brand (Naziv) VALUES (?)";

    private static final String UPDATE =
            "UPDATE Brand SET Naziv = ? WHERE IDBrand = ?";

    private static final String DELETE =
            "DELETE FROM Brand WHERE IDBrand = ?";

    @Override
    public List<Brand> getAll()
    {
        List<Brand> brendovi = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                brendovi.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu brendova.", e);
        }
        return brendovi;
    }

    @Override
    public Optional<Brand> getById(int id)
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
            throw new RepoException("Greska pri dohvatu brenda id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void create(Brand brand)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, brand.getNaziv());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys())
            {
                if (kljucevi.next())
                {
                    brand.setId(kljucevi.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju brenda.", e);
        }
    }

    @Override
    public void update(Brand brand)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE))
        {
            ps.setString(1, brand.getNaziv());
            ps.setInt(2, brand.getId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri azuriranju brenda.", e);
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
            throw new RepoException("Greska pri brisanju brenda id=" + id, e);
        }
    }

    private Brand mapRow(ResultSet rs) throws SQLException
    {
        return new Brand(rs.getInt("IDBrand"), rs.getString("Naziv"));
    }
}
