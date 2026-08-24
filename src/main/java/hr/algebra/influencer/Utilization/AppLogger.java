package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Korisnik;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AppLogger
{

    private static final Logger LOGGER = Logger.getLogger("hr.algebra.influencer");

    static
    {
        try
        {
            LOGGER.setUseParentHandlers(false);
            LOGGER.setLevel(Level.ALL);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("influencer.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        }
        catch (IOException e)
        {
            throw new RepoException(e);
        }
    }

    private AppLogger()
    {
    }

    public static void info(String poruka)
    {
        LOGGER.info(korisnikPrefiks() + poruka);
    }

    public static void upozorenje(String poruka)
    {
        LOGGER.warning(korisnikPrefiks() + poruka);
    }

    public static void greska(String poruka, Throwable uzrok)
    {
        LOGGER.log(Level.SEVERE, korisnikPrefiks() + poruka, uzrok);
    }

    private static String korisnikPrefiks()
    {
        Korisnik korisnik = Session.getTrenutniKorisnik();
        return "[" + (korisnik == null ? "nepoznat" : korisnik.getKorisnickoIme()) + "] ";
    }
}
