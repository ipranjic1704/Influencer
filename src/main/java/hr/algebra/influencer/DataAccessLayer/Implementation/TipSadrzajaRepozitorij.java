package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.TipSadrzaja;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TipSadrzajaRepozitorij implements Repozitorij<TipSadrzaja> {

    private static final TipSadrzajaRepozitorij INSTANCA = new TipSadrzajaRepozitorij();

    private TipSadrzajaRepozitorij() {
    }

    public static TipSadrzajaRepozitorij getInstance() {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDTipSadrzaja, Naziv FROM TipSadrzaja";

    private static final String SELECT_BY_ID =
            "SELECT IDTipSadrzaja, Naziv FROM TipSadrzaja WHERE IDTipSadrzaja = ?";

    private static final String INSERT =
            "INSERT INTO TipSadrzaja (Naziv) VALUES (?)";

    private static final String UPDATE =
            "UPDATE TipSadrzaja SET Naziv = ? WHERE IDTipSadrzaja = ?";

    private static final String DELETE =
            "DELETE FROM TipSadrzaja WHERE IDTipSadrzaja = ?";

    @Override
    public List<TipSadrzaja> getAll() {
        List<TipSadrzaja> tipovi = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tipovi.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu tipova sadrzaja.", e);
        }
        return tipovi;
    }

    @Override
    public Optional<TipSadrzaja> getById(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu tipa sadrzaja id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void create(TipSadrzaja tipSadrzaja) {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tipSadrzaja.getNaziv());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys()) {
                if (kljucevi.next()) {
                    tipSadrzaja.setId(kljucevi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju tipa sadrzaja.", e);
        }
    }

    @Override
    public void update(TipSadrzaja tipSadrzaja) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE)) {
            ps.setString(1, tipSadrzaja.getNaziv());
            ps.setInt(2, tipSadrzaja.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri azuriranju tipa sadrzaja.", e);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju tipa sadrzaja id=" + id, e);
        }
    }

    private TipSadrzaja mapRow(ResultSet rs) throws SQLException {
        return new TipSadrzaja(rs.getInt("IDTipSadrzaja"), rs.getString("Naziv"));
    }
}
