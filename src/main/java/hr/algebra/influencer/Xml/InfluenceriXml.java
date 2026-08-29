package hr.algebra.influencer.Xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "influenceri")
public class InfluenceriXml
{

    private List<InfluencerXml> influencer = new ArrayList<>();

    public InfluenceriXml()
    {
    }

    public InfluenceriXml(List<InfluencerXml> influencer)
    {
        this.influencer = influencer;
    }

    @XmlElement(name = "influencer")
    public List<InfluencerXml> getInfluencer()
    {
        return influencer;
    }

    public void setInfluencer(List<InfluencerXml> influencer)
    {
        this.influencer = influencer;
    }
}
