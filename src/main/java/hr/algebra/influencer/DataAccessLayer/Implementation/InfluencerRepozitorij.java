package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Repozitorij za CRUD operacije nad tablicom Influencer.
// Influencer ima Many-to-Many vezu s Platforma (kroz InfluencerPlatforma) - platforme
// se dohvacaju odvojenim upitom, a spremaju "delete and re-insert" strategijom.
// Nise/TipoviSadrzaja/BrandSuradnje jos nemaju svoje tablice/repozitorije pa se ovdje ne pune.
public class InfluencerRepozitorij implements Repozitorij<Influencer> {

    private static final InfluencerRepozitorij INSTANCA = new InfluencerRepozitorij();

    private InfluencerRepozitorij() {
    }

    public static InfluencerRepozitorij getInstance() {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDInfluencer, ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, JezikSadrzaja, ProfilnaSlika " +
            "FROM Influencer";

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE IDInfluencer = ?";

    private static final String INSERT =
            "INSERT INTO Influencer (ImeNadimak, BrojPratitelja, EngagementRate, Zemlja, JezikSadrzaja, ProfilnaSlika) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE Influencer SET ImeNadimak = ?, BrojPratitelja = ?, EngagementRate = ?, Zemlja = ?, " +
            "JezikSadrzaja = ?, ProfilnaSlika = ? WHERE IDInfluencer = ?";

    private static final String DELETE =
            "DELETE FROM Influencer WHERE IDInfluencer = ?";

    // Dohvaca platforme jednog influencera kroz spojnu tablicu InfluencerPlatforma.
    private static final String SELECT_PLATFORME =
            "SELECT p.IDPlatforma, p.Naziv FROM Platforma p " +
            "JOIN InfluencerPlatforma ip ON p.IDPlatforma = ip.IDPlatforma " +
            "WHERE ip.IDInfluencer = ?";

    private static final String INSERT_PLATFORMA_VEZA =
            "INSERT INTO InfluencerPlatforma (IDInfluencer, IDPlatforma) VALUES (?, ?)";

    private static final String DELETE_PLATFORMA_VEZE =
            "DELETE FROM InfluencerPlatforma WHERE IDInfluencer = ?";

    // Dohvaca sve influencere, a zatim za svakog posebno dohvaca njegove platforme (N+1 pristup).
    @Override
    public List<Influencer> getAll() {
        List<Influencer> influenceri = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                influenceri.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu influencera.", e);
        }

        for (Influencer influencer : influenceri) {
            influencer.setPlatforme(dohvatiPlatforme(influencer.getId()));
        }
        return influenceri;
    }

    @Override
    public Optional<Influencer> getById(int id) {
        Influencer influencer = null;
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    influencer = mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu influencera id=" + id, e);
        }

        if (influencer != null) {
            influencer.setPlatforme(dohvatiPlatforme(influencer.getId()));
        }
        return Optional.ofNullable(influencer);
    }

    @Override
    public void create(Influencer influencer) {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            postaviParametre(ps, influencer);
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys()) {
                if (kljucevi.next()) {
                    influencer.setId(kljucevi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju influencera.", e);
        }

        spremiPlatforme(influencer);
    }

    @Override
    public void update(Influencer influencer) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE)) {
            postaviParametre(ps, influencer); // popunjava 1-6
            ps.setInt(7, influencer.getId()); // 7 -> IDInfluencer (WHERE uvjet)
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri azuriranju influencera.", e);
        }

        // "Delete and re-insert" strategija za Many-to-Many vezu s platformama.
        obrisiPlatformeVeze(influencer.getId());
        spremiPlatforme(influencer);
    }

    // Brise influencera. Veze u InfluencerPlatforma nestaju automatski (ON DELETE CASCADE u DDL),
    // a Korisnik.InfluencerID koji je referencirao ovaj profil postaje NULL (ON DELETE SET NULL).
    @Override
    public void delete(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju influencera id=" + id, e);
        }
    }

    // Postavlja zajednicke parametre za INSERT (1-6) i UPDATE (1-6, a 7 se postavlja u update()).
    private void postaviParametre(PreparedStatement ps, Influencer influencer) throws SQLException {
        ps.setString(1, influencer.getImeNadimak());
        ps.setInt(2, influencer.getBrojPratitelja());
        ps.setDouble(3, influencer.getEngagementRate());
        ps.setString(4, influencer.getZemlja());
        ps.setString(5, influencer.getJezikSadrzaja());
        ps.setString(6, influencer.getProfilnaSlika());
    }

    private Influencer mapRow(ResultSet rs) throws SQLException {
        return new Influencer(
                rs.getInt("IDInfluencer"),
                rs.getString("ImeNadimak"),
                rs.getInt("BrojPratitelja"),
                rs.getDouble("EngagementRate"),
                rs.getString("Zemlja"),
                rs.getString("JezikSadrzaja"),
                rs.getString("ProfilnaSlika")
        );
    }

    // Dohvaca sve platforme povezane s danim influencerom kroz InfluencerPlatforma.
    private List<Platforma> dohvatiPlatforme(int idInfluencer) {
        List<Platforma> platforme = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_PLATFORME)) {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    platforme.add(new Platforma(rs.getInt("IDPlatforma"), rs.getString("Naziv")));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu platformi influencera.", e);
        }
        return platforme;
    }

    // Sprema sve trenutne platforme influencera u spojnu tablicu InfluencerPlatforma.
    private void spremiPlatforme(Influencer influencer) {
        if (influencer.getPlatforme() == null || influencer.getPlatforme().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_PLATFORMA_VEZA)) {
            for (Platforma platforma : influencer.getPlatforme()) {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, platforma.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju veza influencer-platforma.", e);
        }
    }

    // Brise sve veze influencer-platforma za zadanog influencera - prvi korak "delete and re-insert" strategije.
    private void obrisiPlatformeVeze(int idInfluencer) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_PLATFORMA_VEZE)) {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju veza influencer-platforma.", e);
        }
    }
}
