package hr.algebra.influencer.Xml;

import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Model.TipSadrzaja;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.stream.Collectors;

@XmlRootElement(name = "influencer")
@XmlAccessorType(XmlAccessType.FIELD)
public class InfluencerXml
{

    @XmlElement
    private String imeNadimak;
    @XmlElement
    private int brojPratitelja;
    @XmlElement
    private double engagementRate;
    @XmlElement
    private String zemlja;
    @XmlElement
    private String grad;
    @XmlElement
    private String jezikSadrzaja;
    @XmlElement
    private String profilnaSlika;
    @XmlElement
    private String platforme;
    @XmlElement
    private String nise;
    @XmlElement
    private String tipoviSadrzaja;

    public InfluencerXml()
    {
    }

    public InfluencerXml(Influencer influencer)
    {
        this.imeNadimak = influencer.getImeNadimak();
        this.brojPratitelja = influencer.getBrojPratitelja();
        this.engagementRate = influencer.getEngagementRate();
        this.zemlja = influencer.getZemlja();
        this.grad = influencer.getGrad() == null ? "" : influencer.getGrad().getNaziv();
        this.jezikSadrzaja = influencer.getJezikSadrzaja();
        this.profilnaSlika = influencer.getProfilnaSlika();
        this.platforme = influencer.getPlatforme().stream()
                .map(Platforma::getNaziv).collect(Collectors.joining(", "));
        this.nise = influencer.getNise().stream()
                .map(Nisa::getNaziv).collect(Collectors.joining(", "));
        this.tipoviSadrzaja = influencer.getTipoviSadrzaja().stream()
                .map(TipSadrzaja::getNaziv).collect(Collectors.joining(", "));
    }
}
