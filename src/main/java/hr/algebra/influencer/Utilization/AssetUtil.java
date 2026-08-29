package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Exception.RepoException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public final class AssetUtil
{

    private static final Path ASSETS_DIR = Path.of("assets");

    private AssetUtil()
    {
    }

    public static String spremiSliku(File izvor)
    {
        try
        {
            Files.createDirectories(ASSETS_DIR);

            String naziv = izvor.getName();
            int tockaIndex = naziv.lastIndexOf('.');
            String ekstenzija = tockaIndex == -1 ? "" : naziv.substring(tockaIndex);
            Path odrediste = ASSETS_DIR.resolve(UUID.randomUUID() + ekstenzija);

            Files.copy(izvor.toPath(), odrediste, StandardCopyOption.REPLACE_EXISTING);

            return odrediste.toUri().toString();
        }
        catch (IOException e)
        {
            throw new RepoException("Greska pri spremanju slike.", e);
        }
    }

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
