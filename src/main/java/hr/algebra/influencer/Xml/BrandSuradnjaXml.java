package hr.algebra.influencer.Xml;

import hr.algebra.influencer.Model.BrandSuradnja;
import hr.algebra.influencer.Model.Influencer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class BrandSuradnjaXml
{

    @XmlElement
    private String nazivKampanje;
    @XmlElement
    private String brand;
    @XmlElement
    private int godina;
    @XmlElement
    private String status;
    @XmlElement
    private String tim;

    public BrandSuradnjaXml()
    {
    }

    public BrandSuradnjaXml(BrandSuradnja brandSuradnja)
    {
        this.nazivKampanje = brandSuradnja.getNazivKampanje();
        this.brand = brandSuradnja.getBrand() == null ? "" : brandSuradnja.getBrand().getNaziv();
        this.godina = brandSuradnja.getGodina();
        this.status = brandSuradnja.getStatus() == null ? "" : brandSuradnja.getStatus().name();
        this.tim = brandSuradnja.getTim().stream()
                .map(Influencer::getImeNadimak).collect(Collectors.joining(", "));
    }
}
