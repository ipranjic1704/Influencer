package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Model.TipSadrzaja;
import hr.algebra.influencer.Xml.InfluenceriXml;
import hr.algebra.influencer.Xml.InfluencerXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InfluencerRepozitorij implements Repozitorij<Influencer>
{

    private static final InfluencerRepozitorij INSTANCA = new InfluencerRepozitorij();

    private InfluencerRepozitorij()
    {
    }

    public static InfluencerRepozitorij getInstance()
    {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT i.IDInfluencer, i.ImeNadimak, i.BrojPratitelja, i.EngagementRate, i.Zemlja, " +
            "i.GradID, g.Naziv AS NazivGrad, i.JezikSadrzaja, i.ProfilnaSlika " +
            "FROM Influencer i LEFT JOIN Grad g ON i.GradID = g.IDGrad";

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE i.IDInfluencer = ?";

    private static final String INSERT =
            "INSERT INTO Influencer (ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, GradID, JezikSadrzaja, ProfilnaSlika) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE Influencer SET ImeNadimak = ?, BrojPratitelja = ?, EngagementRate = ?, Zemlja = ?, GradID = ?, " +
            "JezikSadrzaja = ?, ProfilnaSlika = ? WHERE IDInfluencer = ?";

    private static final String DELETE =
            "DELETE FROM Influencer WHERE IDInfluencer = ?";

    private static final String SELECT_PLATFORME =
            "SELECT p.IDPlatforma, p.Naziv FROM Platforma p " +
            "JOIN InfluencerPlatforma ip ON p.IDPlatforma = ip.IDPlatforma " +
            "WHERE ip.IDInfluencer = ?";

    private static final String INSERT_PLATFORMA_VEZA =
            "INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma) VALUES (?, ?)";

    private static final String DELETE_PLATFORMA_VEZE =
            "DELETE FROM InfluencerPlatforma WHERE IDInfluencer = ?";

    private static final String SELECT_NISE =
            "SELECT n.IDNisa, n.Naziv FROM Nisa n " +
            "JOIN InfluencerNisa ini ON n.IDNisa = ini.IDNisa " +
            "WHERE ini.IDInfluencer = ?";

    private static final String INSERT_NISA_VEZA =
            "INSERT INTO InfluencerNisa (IDInfluencer, IDNisa) VALUES (?, ?)";

    private static final String DELETE_NISA_VEZE =
            "DELETE FROM InfluencerNisa WHERE IDInfluencer = ?";

    private static final String SELECT_TIPOVI_SADRZAJA =
            "SELECT t.IDTipSadrzaja, t.Naziv FROM TipSadrzaja t " +
            "JOIN InfluencerTipSadrzaja iti ON t.IDTipSadrzaja = iti.IDTipSadrzaja " +
            "WHERE iti.IDInfluencer = ?";

    private static final String INSERT_TIP_SADRZAJA_VEZA =
            "INSERT INTO InfluencerTipSadrzaja (IDInfluencer, IDTipSadrzaja) VALUES (?, ?)";

    private static final String DELETE_TIPOVI_SADRZAJA_VEZE =
            "DELETE FROM InfluencerTipSadrzaja WHERE IDInfluencer = ?";

    @Override
    public List<Influencer> getAll()
    {
        List<Influencer> influenceri = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                influenceri.add(mapRow(rs));
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu influencera.", e);
        }

        for (Influencer influencer : influenceri)
        {
            influencer.setPlatforme(dohvatiPlatforme(influencer.getId()));
            influencer.setNise(dohvatiNise(influencer.getId()));
            influencer.setTipoviSadrzaja(dohvatiTipoveSadrzaja(influencer.getId()));
        }
        return influenceri;
    }

    @Override
    public Optional<Influencer> getById(int id)
    {
        Influencer influencer = null;
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID))
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    influencer = mapRow(rs);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu influencera id=" + id, e);
        }

        if (influencer != null)
        {
            influencer.setPlatforme(dohvatiPlatforme(influencer.getId()));
            influencer.setNise(dohvatiNise(influencer.getId()));
            influencer.setTipoviSadrzaja(dohvatiTipoveSadrzaja(influencer.getId()));
        }
        return Optional.ofNullable(influencer);
    }

    @Override
    public void create(Influencer influencer)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS))
        {
            postaviParametre(ps, influencer);
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys())
            {
                if (kljucevi.next())
                {
                    influencer.setId(kljucevi.getInt(1));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju influencera.", e);
        }

        spremiPlatforme(influencer);
        spremiNise(influencer);
        spremiTipoveSadrzaja(influencer);
    }

    @Override
    public void update(Influencer influencer)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE))
        {
            postaviParametre(ps, influencer);
            ps.setInt(8, influencer.getId());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri azuriranju influencera.", e);
        }

        obrisiPlatformeVeze(influencer.getId());
        spremiPlatforme(influencer);
        obrisiNiseVeze(influencer.getId());
        spremiNise(influencer);
        obrisiTipoveSadrzajaVeze(influencer.getId());
        spremiTipoveSadrzaja(influencer);
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
            throw new RepoException("Greska pri brisanju influencera id=" + id, e);
        }
    }

    private void postaviParametre(PreparedStatement ps, Influencer influencer) throws SQLException
    {
        ps.setString(1, influencer.getImeNadimak());
        ps.setInt(2, influencer.getBrojPratitelja());
        ps.setDouble(3, influencer.getEngagementRate());
        ps.setString(4, influencer.getZemlja());
        if (influencer.getGrad() != null)
        {
            ps.setInt(5, influencer.getGrad().getId());
        }
        else
        {
            ps.setNull(5, Types.INTEGER);
        }
        ps.setString(6, influencer.getJezikSadrzaja());
        ps.setString(7, influencer.getProfilnaSlika());
    }

    private Influencer mapRow(ResultSet rs) throws SQLException
    {
        int idGrad = rs.getInt("GradID");
        Grad grad = rs.wasNull() ? null : new Grad(idGrad, rs.getString("NazivGrad"));

        return new Influencer(
                rs.getInt("IDInfluencer"),
                rs.getString("ImeNadimak"),
                rs.getInt("BrojPratitelja"),
                rs.getDouble("EngagementRate"),
                rs.getString("Zemlja"),
                grad,
                rs.getString("JezikSadrzaja"),
                rs.getString("ProfilnaSlika")
        );
    }

    private List<Platforma> dohvatiPlatforme(int idInfluencer)
    {
        List<Platforma> platforme = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_PLATFORME))
        {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    platforme.add(new Platforma(rs.getInt("IDPlatforma"), rs.getString("Naziv")));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu platformi influencera.", e);
        }
        return platforme;
    }

    private void spremiPlatforme(Influencer influencer)
    {
        if (influencer.getPlatforme() == null || influencer.getPlatforme().isEmpty())
        {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_PLATFORMA_VEZA))
        {
            for (Platforma platforma : influencer.getPlatforme())
            {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, platforma.getId());
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju veza influencer-platforma.", e);
        }
    }

    private void obrisiPlatformeVeze(int idInfluencer)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_PLATFORMA_VEZE))
        {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri brisanju veza influencer-platforma.", e);
        }
    }

    private List<Nisa> dohvatiNise(int idInfluencer)
    {
        List<Nisa> nise = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_NISE))
        {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    nise.add(new Nisa(rs.getInt("IDNisa"), rs.getString("Naziv")));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu nisa influencera.", e);
        }
        return nise;
    }

    private void spremiNise(Influencer influencer)
    {
        if (influencer.getNise() == null || influencer.getNise().isEmpty())
        {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_NISA_VEZA))
        {
            for (Nisa nisa : influencer.getNise())
            {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, nisa.getId());
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju veza influencer-nisa.", e);
        }
    }

    private void obrisiNiseVeze(int idInfluencer)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_NISA_VEZE))
        {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri brisanju veza influencer-nisa.", e);
        }
    }

    private List<TipSadrzaja> dohvatiTipoveSadrzaja(int idInfluencer)
    {
        List<TipSadrzaja> tipovi = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_TIPOVI_SADRZAJA))
        {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    tipovi.add(new TipSadrzaja(rs.getInt("IDTipSadrzaja"), rs.getString("Naziv")));
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri dohvatu tipova sadrzaja influencera.", e);
        }
        return tipovi;
    }

    private void spremiTipoveSadrzaja(Influencer influencer)
    {
        if (influencer.getTipoviSadrzaja() == null || influencer.getTipoviSadrzaja().isEmpty())
        {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_TIP_SADRZAJA_VEZA))
        {
            for (TipSadrzaja tip : influencer.getTipoviSadrzaja())
            {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, tip.getId());
                ps.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri spremanju veza influencer-tip sadrzaja.", e);
        }
    }

    private void obrisiTipoveSadrzajaVeze(int idInfluencer)
    {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_TIPOVI_SADRZAJA_VEZE))
        {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RepoException("Greska pri brisanju veza influencer-tip sadrzaja.", e);
        }
    }

    public int exportToXmlJaxb(Path putanja)
    {
        List<Influencer> influenceri = getAll();

        try
        {
            Path roditelj = putanja.toAbsolutePath().getParent();
            if (roditelj != null)
            {
                Files.createDirectories(roditelj);
            }

            List<InfluencerXml> influenceriXml = influenceri.stream()
                    .map(InfluencerXml::new)
                    .collect(Collectors.toList());

            JAXBContext context = JAXBContext.newInstance(InfluenceriXml.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new InfluenceriXml(influenceriXml), putanja.toFile());

            return influenceri.size();
        }
        catch (Exception e)
        {
            throw new RepoException("Greska pri JAXB XML izvozu influencera.", e);
        }
    }
}
