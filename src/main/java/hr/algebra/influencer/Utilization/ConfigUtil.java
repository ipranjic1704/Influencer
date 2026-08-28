package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Exception.RepoException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public final class ConfigUtil
{

    private static final String DB_URL;
    private static final String DB_KORISNIK;
    private static final String DB_LOZINKA;
    private static final int SIRINA_EKRANA;
    private static final int VISINA_EKRANA;
    private static final String YOUTUBE_API_KLJUC;

    static
    {
        Handler handler = new Handler();
        try (InputStream config = ConfigUtil.class.getResourceAsStream("/hr/algebra/influencer/config.xml"))
        {
            SAXParserFactory.newInstance().newSAXParser().parse(config, handler);
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            throw new RepoException("Greska pri citanju config.xml.", e);
        }

        DB_URL = handler.dbUrl;
        DB_KORISNIK = handler.dbKorisnik;
        DB_LOZINKA = handler.dbLozinka;
        SIRINA_EKRANA = Integer.parseInt(handler.sirinaEkrana);
        VISINA_EKRANA = Integer.parseInt(handler.visinaEkrana);
        YOUTUBE_API_KLJUC = handler.youtubeApiKljuc;
    }

    private ConfigUtil()
    {
    }

    public static String getDbUrl()
    {
        return DB_URL;
    }

    public static String getDbKorisnik()
    {
        return DB_KORISNIK;
    }

    public static String getDbLozinka()
    {
        return DB_LOZINKA;
    }

    public static int getSirinaEkrana()
    {
        return SIRINA_EKRANA;
    }

    public static int getVisinaEkrana()
    {
        return VISINA_EKRANA;
    }

    public static String getYoutubeApiKljuc()
    {
        return YOUTUBE_API_KLJUC;
    }

    private static class Handler extends DefaultHandler
    {

        private final StringBuilder sadrzaj = new StringBuilder();

        private String dbUrl = "";
        private String dbKorisnik = "";
        private String dbLozinka = "";
        private String sirinaEkrana = "1280";
        private String visinaEkrana = "800";
        private String youtubeApiKljuc = "";

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            sadrzaj.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length)
        {
            sadrzaj.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            String vrijednost = sadrzaj.toString().trim();
            switch (qName)
            {
                case "url" -> dbUrl = vrijednost;
                case "korisnickoIme" -> dbKorisnik = vrijednost;
                case "lozinka" -> dbLozinka = vrijednost;
                case "sirina" -> sirinaEkrana = vrijednost;
                case "visina" -> visinaEkrana = vrijednost;
                case "apiKey" -> youtubeApiKljuc = vrijednost;
                default ->
                {
                }
            }
        }
    }
}
