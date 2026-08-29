package hr.algebra.influencer;

import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Utilization.ConfigUtil;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BazaPodataka
{

    private static final String URL = ConfigUtil.getDbUrl();
    private static final String USER = ConfigUtil.getDbKorisnik();
    private static final String PASS = ConfigUtil.getDbLozinka();

    private static final Connection veza;

    private BazaPodataka()
    {
    }

    static
    {
        try
        {
            veza = DriverManager.getConnection(URL, USER, PASS);
            initSchema(veza);
            pozoviKreirajAdminaProceduru(veza);
            System.out.println("[db] veza stvorena");
        }
        catch (Exception e)
        {
            throw new RepoException(e);
        }
    }

    public static void zatvori()
    {
        try
        {
            veza.close();
            System.out.println("[db] veza zatvorena");
        }
        catch (SQLException e)
        {
            System.err.println("[db] Greska pri zatvaranju konekcije: " + e.getMessage());
        }
    }

    private static void initSchema(Connection conn) throws Exception
    {
        izvrsiSkriptu(conn, "/hr/algebra/influencer/sql/init_ddl.sql");
        System.out.println("[db] Shema inicijalizirana");
    }

    // Mora biti public - H2 ovu metodu poziva refleksijom preko CREATE ALIAS KREIRAJ_ADMINA u init_ddl.sql.
    // Connection parametar H2 automatski puni trenutnom vezom, pa ga se u SQL pozivu (KREIRAJ_ADMINA()) i ne vidi.
    // WHERE NOT EXISTS cini metodu idempotentnom - siguno ju je pozvati bilo kad, nikad ne duplira admina.
    public static void kreirajAdminAkoNePostoji(Connection conn) throws SQLException
    {
        String sql = "INSERT INTO Korisnik (UserName, Lozinka, Uloga) " +
                "SELECT 'admin', 'admin', 'ADMIN' " +
                "WHERE NOT EXISTS (SELECT 1 FROM Korisnik WHERE UserName = 'admin')";
        try (Statement stmt = conn.createStatement())
        {
            stmt.execute(sql);
        }
    }

    // CallableStatement + prepareCall poziva pravu proceduru koja zivi u bazi (KREIRAJ_ADMINA), za razliku
    // od PreparedStatement koji samo salje SQL upit - ovdje baza sama izvrsi Java metodu registriranu preko ALIAS-a.
    private static void pozoviKreirajAdminaProceduru(Connection conn) throws SQLException
    {
        try (CallableStatement cs = conn.prepareCall("{call KREIRAJ_ADMINA()}"))
        {
            cs.execute();
        }
        System.out.println("[db] Admin korisnik provjeren/kreiran (procedura)");
    }

    public static void obrisiSveIzBaze()
    {
        try
        {
            izvrsiSkriptu(veza, "/hr/algebra/influencer/sql/delete_all.sql");
            pozoviKreirajAdminaProceduru(veza);
            System.out.println("[db] Svi podaci obrisani");
        }
        catch (Exception e)
        {
            throw new RepoException("Greska pri brisanju svih podataka iz baze.", e);
        }
    }

    private static void izvrsiSkriptu(Connection conn, String putanja) throws Exception
    {
        String sadrzaj = new String(
                BazaPodataka.class.getResourceAsStream(putanja).readAllBytes(),
                StandardCharsets.UTF_8
        );
        try (Statement stmt = conn.createStatement())
        {
            for (String sql : sadrzaj.split(";"))
            {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty())
                {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    public static Connection getConnection()
    {
        return veza;
    }
}
