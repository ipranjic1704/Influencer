package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Model.Korisnik;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AppLogger
{

    private static final Logger LOGGER = Logger.getLogger("hr.algebra.influencer");
    private static final File LOG_XML = new File("influencer-log.xml");

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
        zapisiXML("INFO", poruka);
    }

    public static void upozorenje(String poruka)
    {
        LOGGER.warning(korisnikPrefiks() + poruka);
        zapisiXML("UPOZORENJE", poruka);
    }

    public static void greska(String poruka, Throwable uzrok)
    {
        LOGGER.log(Level.SEVERE, korisnikPrefiks() + poruka, uzrok);
        zapisiXML("GRESKA", uzrok == null ? poruka : poruka + " (" + uzrok.getMessage() + ")");
    }

    private static String korisnikPrefiks()
    {
        Korisnik korisnik = Session.getTrenutniKorisnik();
        return "[" + (korisnik == null ? "nepoznat" : korisnik.getKorisnickoIme()) + "] ";
    }

    private static synchronized void zapisiXML(String razina, String poruka)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document;

            if (LOG_XML.exists())
            {
                document = builder.parse(LOG_XML);
                ukloniPrazneTekstualneCvorove(document);
            }
            else
            {
                document = builder.newDocument();
                document.appendChild(document.createElement("zapisnik"));
            }

            Element korijen = document.getDocumentElement();
            Element zapis = document.createElement("zapis");

            Element vrijeme = document.createElement("vrijeme");
            vrijeme.setTextContent(LocalDateTime.now().toString());
            zapis.appendChild(vrijeme);

            Element razinaElement = document.createElement("razina");
            razinaElement.setTextContent(razina);
            zapis.appendChild(razinaElement);

            Korisnik korisnik = Session.getTrenutniKorisnik();
            Element korisnikElement = document.createElement("korisnik");
            korisnikElement.setTextContent(korisnik == null ? "nepoznat" : korisnik.getKorisnickoIme());
            zapis.appendChild(korisnikElement);

            Element porukaElement = document.createElement("poruka");
            porukaElement.setTextContent(poruka);
            zapis.appendChild(porukaElement);

            korijen.appendChild(zapis);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(LOG_XML));
        }
        catch (Exception e)
        {
            throw new RepoException(e);
        }
    }

    private static void ukloniPrazneTekstualneCvorove(Node cvor)
    {
        NodeList djeca = cvor.getChildNodes();
        for (int i = djeca.getLength() - 1; i >= 0; i--)
        {
            Node dijete = djeca.item(i);
            if (dijete.getNodeType() == Node.TEXT_NODE && dijete.getTextContent().isBlank())
            {
                cvor.removeChild(dijete);
            }
            else if (dijete.getNodeType() == Node.ELEMENT_NODE)
            {
                ukloniPrazneTekstualneCvorove(dijete);
            }
        }
    }
}
