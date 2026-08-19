package hr.algebra.influencer.DataAccessLayer.Implementation;

import hr.algebra.influencer.BazaPodataka;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Enum.Uloga;
import hr.algebra.influencer.Model.Korisnik;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KorisnikRepozitorij implements Repozitorij<Korisnik> {

    private static final KorisnikRepozitorij INSTANCA = new KorisnikRepozitorij();

    private KorisnikRepozitorij() {
    }

    public static KorisnikRepozitorij getInstance() {
        return INSTANCA;
    }

    private static final String SELECT_ALL =
            "SELECT IDKorisnik, UserName, Lozinka, Uloga, InfluencerID FROM Korisnik";

    private static final String SELECT_BY_ID =
            "SELECT IDKorisnik, UserName, Lozinka, Uloga, InfluencerID FROM Korisnik WHERE IDKorisnik = ?";

    private static final String SELECT_BY_USERNAME =
            "SELECT IDKorisnik, UserName, Lozinka, Uloga, InfluencerID FROM Korisnik WHERE UserName = ?";

    private static final String INSERT =
            "INSERT INTO Korisnik (UserName, Lozinka, Uloga, InfluencerID) VALUES (?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE Korisnik SET UserName = ?, Lozinka = ?, Uloga = ?, InfluencerID = ? WHERE IDKorisnik = ?";

    private static final String DELETE =
            "DELETE FROM Korisnik WHERE IDKorisnik = ?";

    @Override
    public List<Korisnik> getAll() {
        List<Korisnik> korisnici = new ArrayList<>();
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                korisnici.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu korisnika.", e);
        }
        return korisnici;
    }

    @Override
    public Optional<Korisnik> getById(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu korisnika id=" + id, e);
        }
        return Optional.empty();
    }

    public Optional<Korisnik> getByKorisnickoIme(String korisnickoIme) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(SELECT_BY_USERNAME)) {
            ps.setString(1, korisnickoIme);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri dohvatu korisnika po korisnickom imenu.", e);
        }
        return Optional.empty();
    }

    @Override
    public void create(Korisnik korisnik) {
        try (PreparedStatement ps = BazaPodataka.getConnection()
                .prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, korisnik.getKorisnickoIme());
            ps.setString(2, korisnik.getLozinka());
            ps.setString(3, korisnik.getUloga().name());
            postaviInfluencerId(ps, 4, korisnik.getInfluencerId());
            ps.executeUpdate();

            try (ResultSet kljucevi = ps.getGeneratedKeys()) {
                if (kljucevi.next()) {
                    korisnik.setId(kljucevi.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RepoException("Greska pri spremanju korisnika.", e);
        }
    }

    @Override
    public void update(Korisnik korisnik) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(UPDATE)) {
            ps.setString(1, korisnik.getKorisnickoIme());
            ps.setString(2, korisnik.getLozinka());
            ps.setString(3, korisnik.getUloga().name());
            postaviInfluencerId(ps, 4, korisnik.getInfluencerId());
            ps.setInt(5, korisnik.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri azuriranju korisnika.", e);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = BazaPodataka.getConnection().prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepoException("Greska pri brisanju korisnika id=" + id, e);
        }
    }

    private Korisnik mapRow(ResultSet rs) throws SQLException {
        Uloga uloga = Uloga.valueOf(rs.getString("Uloga"));
        int influencerId = rs.getInt("InfluencerID");
        return new Korisnik(
                rs.getInt("IDKorisnik"),
                rs.getString("UserName"),
                rs.getString("Lozinka"),
                uloga,
                rs.wasNull() ? null : influencerId
        );
    }

    // InfluencerID je nullable (ADMIN/BREND ga nemaju) - setInt bi null pretvorio u 0,
    // zato ovdje eksplicitno biramo setNull kad reference nema.
    private void postaviInfluencerId(PreparedStatement ps, int index, Integer influencerId) throws SQLException {
        if (influencerId == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, influencerId);
        }
    }
}
