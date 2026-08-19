package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Platforma;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlatformaRepozitorij implements Repozitorij<Platforma> {

    private static final PlatformaRepozitorij INSTANCA = new PlatformaRepozitorij();

    private PlatformaRepozitorij() {
    }

    public static PlatformaRepozitorij getInstance() {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDPlatforma, Naziv FROM Platforma";

    private static final String SELECT_BY_ID =
            "SELECT IDPlatforma, Naziv FROM Platforma WHERE IDPlatforma = ?";

    private static final String INSERT =
            "INSERT INTO Platforma (Naziv) VALUES (?)";

    private static final String UPDATE =
            "UPDATE Platforma SET Naziv = ? WHERE IDPlatforma = ?";

    private static final String DELETE =
            "DELETE FROM Platforma WHERE IDPlatforma = ?";

    @Override
    public List<Platforma> getAll() {
        List<Platforma> platforme = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                platforme.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu platformi.", e);
        }
        return platforme;
    }

    @Override
    public Optional<Platforma> getById(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu platforme id=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void create(Platforma platforma) {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, platforma.getNaziv());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys()) {
                if (kljucevi.next()) {
                    platforma.setId(kljucevi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju platforme.", e);
        }
    }

    @Override
    public void update(Platforma platforma) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE)) {
            ps.setString(1, platforma.getNaziv());
            ps.setInt(2, platforma.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri azuriranju platforme.", e);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju platforme id=" + id, e);
        }
    }

    private Platforma mapRow(ResultSet rs) throws SQLException {
        return new Platforma(rs.getInt("IDPlatforma"), rs.getString("Naziv"));
    }
}
