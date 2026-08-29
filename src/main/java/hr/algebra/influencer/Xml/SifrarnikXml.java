package hr.algebra.influencer.Xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SifrarnikXml
{

    @XmlElement
    private int id;
    @XmlElement
    private String naziv;

    public SifrarnikXml()
    {
    }

    public SifrarnikXml(int id, String naziv)
    {
        this.id = id;
        this.naziv = naziv;
    }
}
