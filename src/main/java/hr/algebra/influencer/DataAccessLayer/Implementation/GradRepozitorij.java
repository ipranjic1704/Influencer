package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Grad;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradRepozitorij implements Repozitorij<Grad>
{

    private static final GradRepozitorij INSTANCA = new GradRepozitorij();

    private GradRepozitorij()
    {
    }

    public static GradRepozitorij getInstance()
    {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDGrad, Naziv FROM Grad";

    private static final String SELECT_BY_ID =
            "SELECT IDGrad, Naziv FROM Grad WHERE IDGrad = ?";

    private static final String INSERT =
            "INSERT INTO Grad (Naziv) VALUES (?)";

    private static final String UPDATE =
            "UPDATE Grad SET Naziv = ? WHERE IDGrad = ?";

    private static final String DELETE =
            "DELETE FROM Grad WHERE IDGrad = ?";

    @Override
    public List<Grad> getAll()
    {
        List<Grad> gradovi = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                gradovi.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu gradova.", e);
        }
        return gradovi;
    }

    @Override
    public Optional<Grad> getById(int id)
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
            throw new RepoException("Greska pri dohvatu grada id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void create(Grad grad)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, grad.getNaziv());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys())
            {
                if (kljucevi.next())
                {
                    grad.setId(kljucevi.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju grada.", e);
        }
    }

    @Override
    public void update(Grad grad)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE))
        {
            ps.setString(1, grad.getNaziv());
            ps.setInt(2, grad.getId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri azuriranju grada.", e);
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
            throw new RepoException("Greska pri brisanju grada id=" + id, e);
        }
    }

    private Grad mapRow(ResultSet rs) throws SQLException
    {
        return new Grad(rs.getInt("IDGrad"), rs.getString("Naziv"));
    }
}
