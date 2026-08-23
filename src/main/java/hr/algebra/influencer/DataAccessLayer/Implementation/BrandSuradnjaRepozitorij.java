package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.BrandSuradnja;
import hr.algebra.influencer.Model.Enum.StatusSuradnje;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Model.Influencer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Repozitorij za CRUD operacije nad tablicom BrandSuradnja.
// Tim (influenceri dodani u kampanju drag & dropom) je M:N veza kroz BrandSuradnjaInfluencer -
// isti "delete and re-insert" pristup kao InfluencerRepozitorij koristi za platforme.
public class BrandSuradnjaRepozitorij implements Repozitorij<BrandSuradnja> {

    private static final BrandSuradnjaRepozitorij INSTANCA = new BrandSuradnjaRepozitorij();

    private BrandSuradnjaRepozitorij() {
    }

    public static BrandSuradnjaRepozitorij getInstance() {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDBrandSuradnja, NazivKampanje, NazivBrenda, Godina, Status FROM BrandSuradnja";

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE IDBrandSuradnja = ?";

    private static final String INSERT =
            "INSERT INTO BrandSuradnja (NazivKampanje, NazivBrenda, Godina, Status) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE BrandSuradnja SET NazivKampanje = ?, NazivBrenda = ?, Godina = ?, Status = ? WHERE IDBrandSuradnja = ?";

    private static final String DELETE =
            "DELETE FROM BrandSuradnja WHERE IDBrandSuradnja = ?";

    private static final String SELECT_TIM =
            "SELECT i.IDInfluencer, i.ImeNadimak, i.BrojPratitelja, i.EngagementRate, i.Zemlja, " +
            "i.GradID, g.Naziv AS NazivGrad, i.JezikSadrzaja, i.ProfilnaSlika " +
            "FROM Influencer i " +
            "JOIN BrandSuradnjaInfluencer bi ON i.IDInfluencer = bi.IDInfluencer " +
            "LEFT JOIN Grad g ON i.GradID = g.IDGrad " +
            "WHERE bi.IDBrandSuradnja = ?";

    private static final String INSERT_TIM_VEZA =
            "INSERT INTO BrandSuradnjaInfluencer (IDBrandSuradnja, IDInfluencer) VALUES (?, ?)";

    private static final String DELETE_TIM_VEZE =
            "DELETE FROM BrandSuradnjaInfluencer WHERE IDBrandSuradnja = ?";

    @Override
    public List<BrandSuradnja> getAll() {
        List<BrandSuradnja> suradnje = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suradnje.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu brand suradnji.", e);
        }

        for (BrandSuradnja suradnja : suradnje) {
            suradnja.setTim(dohvatiTim(suradnja.getId()));
        }
        return suradnje;
    }

    @Override
    public Optional<BrandSuradnja> getById(int id) {
        BrandSuradnja suradnja = null;
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    suradnja = mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu brand suradnje id=" + id, e);
        }

        if (suradnja != null) {
            suradnja.setTim(dohvatiTim(suradnja.getId()));
        }
        return Optional.ofNullable(suradnja);
    }

    @Override
    public void create(BrandSuradnja suradnja) {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            postaviParametre(ps, suradnja);
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys()) {
                if (kljucevi.next()) {
                    suradnja.setId(kljucevi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju brand suradnje.", e);
        }

        spremiTim(suradnja);
    }

    @Override
    public void update(BrandSuradnja suradnja) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE)) {
            postaviParametre(ps, suradnja); // popunjava 1-4
            ps.setInt(5, suradnja.getId()); // 5 -> IDBrandSuradnja (WHERE uvjet)
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri azuriranju brand suradnje.", e);
        }

        // "Delete and re-insert" strategija za tim - isto kao platforme u InfluencerRepozitoriju.
        obrisiTimVeze(suradnja.getId());
        spremiTim(suradnja);
    }

    // Brise brand suradnju. Veze u BrandSuradnjaInfluencer nestaju automatski (ON DELETE CASCADE u DDL).
    @Override
    public void delete(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju brand suradnje id=" + id, e);
        }
    }

    private void postaviParametre(PreparedStatement ps, BrandSuradnja suradnja) throws SQLException {
        ps.setString(1, suradnja.getNazivKampanje());
        ps.setString(2, suradnja.getNazivBrenda());
        ps.setInt(3, suradnja.getGodina());
        ps.setString(4, suradnja.getStatus().name());
    }

    private BrandSuradnja mapRow(ResultSet rs) throws SQLException {
        return new BrandSuradnja(
                rs.getInt("IDBrandSuradnja"),
                rs.getString("NazivKampanje"),
                rs.getString("NazivBrenda"),
                rs.getInt("Godina"),
                StatusSuradnje.valueOf(rs.getString("Status"))
        );
    }

    // Dohvaca sve influencere u timu dane brand suradnje kroz BrandSuradnjaInfluencer.
    private List<Influencer> dohvatiTim(int idBrandSuradnja) {
        List<Influencer> tim = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_TIM)) {
            ps.setInt(1, idBrandSuradnja);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idGrad = rs.getInt("GradID");
                    Grad grad = rs.wasNull() ? null : new Grad(idGrad, rs.getString("NazivGrad"));

                    tim.add(new Influencer(
                            rs.getInt("IDInfluencer"),
                            rs.getString("ImeNadimak"),
                            rs.getInt("BrojPratitelja"),
                            rs.getDouble("EngagementRate"),
                            rs.getString("Zemlja"),
                            grad,
                            rs.getString("JezikSadrzaja"),
                            rs.getString("ProfilnaSlika")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu tima brand suradnje.", e);
        }
        return tim;
    }

    // Sprema sve trenutne clanove tima brand suradnje u spojnu tablicu BrandSuradnjaInfluencer.
    private void spremiTim(BrandSuradnja suradnja) {
        if (suradnja.getTim() == null || suradnja.getTim().isEmpty()) {
            return;
        }
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(INSERT_TIM_VEZA)) {
            for (Influencer influencer : suradnja.getTim()) {
                ps.setInt(1, suradnja.getId());
                ps.setInt(2, influencer.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju tima brand suradnje.", e);
        }
    }

    // Brise sve veze suradnja-influencer za zadanu suradnju - prvi korak "delete and re-insert" strategije.
    private void obrisiTimVeze(int idBrandSuradnja) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE_TIM_VEZE)) {
            ps.setInt(1, idBrandSuradnja);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju veza brand suradnje.", e);
        }
    }
}
