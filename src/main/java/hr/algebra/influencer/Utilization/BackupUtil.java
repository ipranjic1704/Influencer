package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.DataAccessLayer.Implementation.BrandRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.BrandSuradnjaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.KorisnikRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.NisaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.TipSadrzajaRepozitorij;
import hr.algebra.influencer.Exception.RepoException;
import hr.algebra.influencer.Xml.BazaBackupXml;
import hr.algebra.influencer.Xml.BrandSuradnjaXml;
import hr.algebra.influencer.Xml.InfluencerXml;
import hr.algebra.influencer.Xml.KorisnikXml;
import hr.algebra.influencer.Xml.SifrarnikXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class BackupUtil
{

    private BackupUtil()
    {
    }

    public static int kreirajBackup(Path putanja)
    {
        try
        {
            Path roditelj = putanja.toAbsolutePath().getParent();
            if (roditelj != null)
            {
                Files.createDirectories(roditelj);
            }

            List<InfluencerXml> influenceri = InfluencerRepozitorij.getInstance().getAll().stream()
                    .map(InfluencerXml::new).collect(Collectors.toList());

            List<SifrarnikXml> platforme = PlatformaRepozitorij.getInstance().getAll().stream()
                    .map(p -> new SifrarnikXml(p.getId(), p.getNaziv())).collect(Collectors.toList());

            List<SifrarnikXml> nise = NisaRepozitorij.getInstance().getAll().stream()
                    .map(n -> new SifrarnikXml(n.getId(), n.getNaziv())).collect(Collectors.toList());

            List<SifrarnikXml> tipoviSadrzaja = TipSadrzajaRepozitorij.getInstance().getAll().stream()
                    .map(t -> new SifrarnikXml(t.getId(), t.getNaziv())).collect(Collectors.toList());

            List<SifrarnikXml> gradovi = GradRepozitorij.getInstance().getAll().stream()
                    .map(g -> new SifrarnikXml(g.getId(), g.getNaziv())).collect(Collectors.toList());

            List<SifrarnikXml> brandovi = BrandRepozitorij.getInstance().getAll().stream()
                    .map(b -> new SifrarnikXml(b.getId(), b.getNaziv())).collect(Collectors.toList());

            List<BrandSuradnjaXml> brandSuradnje = BrandSuradnjaRepozitorij.getInstance().getAll().stream()
                    .map(BrandSuradnjaXml::new).collect(Collectors.toList());

            List<KorisnikXml> korisnici = KorisnikRepozitorij.getInstance().getAll().stream()
                    .map(KorisnikXml::new).collect(Collectors.toList());

            BazaBackupXml backup = new BazaBackupXml(influenceri, platforme, nise, tipoviSadrzaja,
                    gradovi, brandovi, brandSuradnje, korisnici);

            JAXBContext context = JAXBContext.newInstance(BazaBackupXml.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(backup, putanja.toFile());

            return backup.ukupnoZapisa();
        }
        catch (Exception e)
        {
            throw new RepoException("Greska pri izradi backupa baze.", e);
        }
    }
}
