package hr.algebra.influencer;

import hr.algebra.influencer.Exception.RepoException;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Klasa koja upravlja jedinom SQL konekcijom u aplikaciji (eager Singleton).
public class BazaPodataka {

    private static final String URL = "jdbc:h2:./InfluencerDB";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static final Connection veza;

    private BazaPodataka() {
    }

    static {
        try {
            veza = DriverManager.getConnection(URL, USER, PASS);
            initSchema(veza);
            System.out.println("[db] veza stvorena");
        } catch (Exception e) {
            throw new RepoException(e);
        }
    }

    public static void pokreni() {
    }

    public static void zatvori() {
        try {
            veza.close();
            System.out.println("[db] veza zatvorena");
        } catch (SQLException e) {
            System.err.println("[db] Greska pri zatvaranju konekcije: " + e.getMessage());
        }
    }

    private static void initSchema(Connection conn) throws Exception {
        String ddl = new String(
                BazaPodataka.class.getResourceAsStream("/hr/algebra/influencer/sql/init_ddl.sql").readAllBytes(),
                StandardCharsets.UTF_8
        );
        try (Statement stmt = conn.createStatement()) {
            for (String sql : ddl.split(";")) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
        System.out.println("[db] Shema inicijalizirana");
    }

    public static Connection getConnection() {
        return veza;
    }
}
