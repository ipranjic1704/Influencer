package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Exception.RepoException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AssetUtil
{

    private static final Path ASSETS_DIR = Path.of("assets").toAbsolutePath().normalize();

    // private static final String ASSETS_PUTANJA = "H:/Programiranje/Java/Influencer/assets";
    // private static final Path ASSETS_DIR = Path.of(ASSETS_PUTANJA);

    private AssetUtil()
    {
    }

    public static Path getAssetsDir()
    {
        try
        {
            Files.createDirectories(ASSETS_DIR);
        }
        catch (IOException e)
        {
            throw new RepoException("Greska pri kreiranju assets direktorija.", e);
        }
        return ASSETS_DIR;
    }

    public static void obrisiSliku(String putanja)
    {
        if (putanja == null || !putanja.startsWith("file:"))
        {
            return;
        }

        try
        {
            Path datoteka = Path.of(URI.create(putanja)).toAbsolutePath().normalize();
            if (!datoteka.startsWith(ASSETS_DIR))
            {
                return;
            }
            Files.deleteIfExists(datoteka);
        }
        catch (Exception e)
        {
            AppLogger.greska("Greska pri brisanju slike: " + putanja, e);
        }
    }
}
