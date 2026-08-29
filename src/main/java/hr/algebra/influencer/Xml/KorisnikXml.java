package hr.algebra.influencer.Xml;

import hr.algebra.influencer.Model.Korisnik;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class KorisnikXml
{

    @XmlElement
    private String korisnickoIme;
    @XmlElement
    private String uloga;

    public KorisnikXml()
    {
    }

    public KorisnikXml(Korisnik korisnik)
    {
        this.korisnickoIme = korisnik.getKorisnickoIme();
        this.uloga = korisnik.getUloga() == null ? "" : korisnik.getUloga().name();
    }
}
