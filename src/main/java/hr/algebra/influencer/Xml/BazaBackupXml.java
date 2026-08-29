package hr.algebra.influencer.Xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "backupBaze")
@XmlAccessorType(XmlAccessType.FIELD)
public class BazaBackupXml
{

    @XmlElementWrapper(name = "influenceri")
    @XmlElement(name = "influencer")
    private List<InfluencerXml> influenceri;

    @XmlElementWrapper(name = "platforme")
    @XmlElement(name = "platforma")
    private List<SifrarnikXml> platforme;

    @XmlElementWrapper(name = "nise")
    @XmlElement(name = "nisa")
    private List<SifrarnikXml> nise;

    @XmlElementWrapper(name = "tipoviSadrzaja")
    @XmlElement(name = "tipSadrzaja")
    private List<SifrarnikXml> tipoviSadrzaja;

    @XmlElementWrapper(name = "gradovi")
    @XmlElement(name = "grad")
    private List<SifrarnikXml> gradovi;

    @XmlElementWrapper(name = "brandovi")
    @XmlElement(name = "brand")
    private List<SifrarnikXml> brandovi;

    @XmlElementWrapper(name = "brandSuradnje")
    @XmlElement(name = "brandSuradnja")
    private List<BrandSuradnjaXml> brandSuradnje;

    @XmlElementWrapper(name = "korisnici")
    @XmlElement(name = "korisnik")
    private List<KorisnikXml> korisnici;

    public BazaBackupXml()
    {
    }

    public BazaBackupXml(List<InfluencerXml> influenceri, List<SifrarnikXml> platforme, List<SifrarnikXml> nise,
                          List<SifrarnikXml> tipoviSadrzaja, List<SifrarnikXml> gradovi, List<SifrarnikXml> brandovi,
                          List<BrandSuradnjaXml> brandSuradnje, List<KorisnikXml> korisnici)
    {
        this.influenceri = influenceri;
        this.platforme = platforme;
        this.nise = nise;
        this.tipoviSadrzaja = tipoviSadrzaja;
        this.gradovi = gradovi;
        this.brandovi = brandovi;
        this.brandSuradnje = brandSuradnje;
        this.korisnici = korisnici;
    }

    public int ukupnoZapisa()
    {
        return influenceri.size() + platforme.size() + nise.size() + tipoviSadrzaja.size()
                + gradovi.size() + brandovi.size() + brandSuradnje.size() + korisnici.size();
    }
}
