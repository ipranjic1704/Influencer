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

    // Poziva se pri brisanju influencera i pri promjeni slike (stara se obrise). putanja dolazi iz
    // Influencer.getProfilnaSlika() - moze biti prazan string (influencer bez slike), zato provjera "file:"
    // prefiksa: samo pravi file:// URI (iz FileChoosera, uvijek unutar assets/) prolazi dalje na brisanje.
    // Nema provjere da je datoteka stvarno unutar assets/ (path traversal rizik prihvacen - FileChooser je
    // zakljucan na assets/ pa putanja izvana ne moze doci drugacije nego kroz odabir postojece datoteke).
    public static void obrisiSliku(String putanja)
    {
        if (putanja == null || !putanja.startsWith("file:"))
        {
            return;
        }

        try
        {
            Files.deleteIfExists(Path.of(URI.create(putanja)));
        }
        catch (Exception e)
        {
            AppLogger.greska("Greska pri brisanju slike: " + putanja, e);
        }
    }
}
