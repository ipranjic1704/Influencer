package hr.algebra.influencer;

import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Utilization.ConfigUtil;

import java.nio.charset.StandardCharsets;
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
            System.out.println("[db] veza stvorena");
        }
        catch (Exception e)
        {
            throw new RepoException(e);
        }
    }

    public static void pokreni()
    {
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

    public static void obrisiSveIzBaze()
    {
        try
        {
            izvrsiSkriptu(veza, "/hr/algebra/influencer/sql/delete_all.sql");
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
