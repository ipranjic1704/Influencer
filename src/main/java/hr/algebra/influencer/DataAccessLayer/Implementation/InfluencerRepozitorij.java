package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Model.TipSadrzaja;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Repozitorij za CRUD operacije nad tablicom Influencer.
// Influencer ima Many-to-Many vezu s Platforma, Nisa i TipSadrzaja (kroz InfluencerPlatforma,
// InfluencerNisa, InfluencerTipSadrzaja) - svaka se dohvaca odvojenim upitom, a sprema
// "delete and re-insert" strategijom. BrandSuradnja nije ovdje jer je influencer u tom vezu
// vlasnik BrandSuradnjaRepozitorij (suradnja drzi svoj tim, ne obrnuto).
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

    // Dohvaca nise jednog influencera kroz spojnu tablicu InfluencerNisa.
    private static final String SELECT_NISE =
            "SELECT n.IDNisa, n.Naziv FROM Nisa n " +
            "JOIN InfluencerNisa ini ON n.IDNisa = ini.IDNisa " +
            "WHERE ini.IDInfluencer = ?";

    private static final String INSERT_NISA_VEZA =
            "INSERT INTO InfluencerNisa (IDInfluencer, IDNisa) VALUES (?, ?)";

    private static final String DELETE_NISA_VEZE =
            "DELETE FROM InfluencerNisa WHERE IDInfluencer = ?";

    // Dohvaca tipove sadrzaja jednog influencera kroz spojnu tablicu InfluencerTipSadrzaja.
    private static final String SELECT_TIPOVI_SADRZAJA =
            "SELECT t.IDTipSadrzaja, t.Naziv FROM TipSadrzaja t " +
            "JOIN InfluencerTipSadrzaja iti ON t.IDTipSadrzaja = iti.IDTipSadrzaja " +
            "WHERE iti.IDInfluencer = ?";

    private static final String INSERT_TIP_SADRZAJA_VEZA =
            "INSERT INTO InfluencerTipSadrzaja (IDInfluencer, IDTipSadrzaja) VALUES (?, ?)";

    private static final String DELETE_TIPOVI_SADRZAJA_VEZE =
            "DELETE FROM InfluencerTipSadrzaja WHERE IDInfluencer = ?";

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
            influencer.setNise(dohvatiNise(influencer.getId()));
            influencer.setTipoviSadrzaja(dohvatiTipoveSadrzaja(influencer.getId()));
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
            influencer.setNise(dohvatiNise(influencer.getId()));
            influencer.setTipoviSadrzaja(dohvatiTipoveSadrzaja(influencer.getId()));
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
        spremiNise(influencer);
        spremiTipoveSadrzaja(influencer);
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

        // "Delete and re-insert" strategija za Many-to-Many veze s platformama, nisama i tipovima sadrzaja.
        obrisiPlatformeVeze(influencer.getId());
        spremiPlatforme(influencer);
        obrisiNiseVeze(influencer.getId());
        spremiNise(influencer);
        obrisiTipoveSadrzajaVeze(influencer.getId());
        spremiTipoveSadrzaja(influencer);
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

    // Dohvaca sve nise povezane s danim influencerom kroz InfluencerNisa.
    private List<Nisa> dohvatiNise(int idInfluencer) {
        List<Nisa> nise = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_NISE)) {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    nise.add(new Nisa(rs.getInt("IDNisa"), rs.getString("Naziv")));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu nisa influencera.", e);
        }
        return nise;
    }

    // Sprema sve trenutne nise influencera u spojnu tablicu InfluencerNisa.
    private void spremiNise(Influencer influencer) {
        if (influencer.getNise() == null || influencer.getNise().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_NISA_VEZA)) {
            for (Nisa nisa : influencer.getNise()) {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, nisa.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju veza influencer-nisa.", e);
        }
    }

    // Brise sve veze influencer-nisa za zadanog influencera - prvi korak "delete and re-insert" strategije.
    private void obrisiNiseVeze(int idInfluencer) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_NISA_VEZE)) {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju veza influencer-nisa.", e);
        }
    }

    // Dohvaca sve tipove sadrzaja povezane s danim influencerom kroz InfluencerTipSadrzaja.
    private List<TipSadrzaja> dohvatiTipoveSadrzaja(int idInfluencer) {
        List<TipSadrzaja> tipovi = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_TIPOVI_SADRZAJA)) {
            ps.setInt(1, idInfluencer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tipovi.add(new TipSadrzaja(rs.getInt("IDTipSadrzaja"), rs.getString("Naziv")));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu tipova sadrzaja influencera.", e);
        }
        return tipovi;
    }

    // Sprema sve trenutne tipove sadrzaja influencera u spojnu tablicu InfluencerTipSadrzaja.
    private void spremiTipoveSadrzaja(Influencer influencer) {
        if (influencer.getTipoviSadrzaja() == null || influencer.getTipoviSadrzaja().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_TIP_SADRZAJA_VEZA)) {
            for (TipSadrzaja tip : influencer.getTipoviSadrzaja()) {
                ps.setInt(1, influencer.getId());
                ps.setInt(2, tip.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju veza influencer-tip sadrzaja.", e);
        }
    }

    // Brise sve veze influencer-tip sadrzaja za zadanog influencera - prvi korak "delete and re-insert" strategije.
    private void obrisiTipoveSadrzajaVeze(int idInfluencer) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_TIPOVI_SADRZAJA_VEZE)) {
            ps.setInt(1, idInfluencer);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju veza influencer-tip sadrzaja.", e);
        }
    }
}
