package hr.algebra.influencer.Utilization;

import hr.algebra.influencer.DataAccessLayer.Implementation.BrandRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.BrandSuradnjaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.NisaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.TipSadrzajaRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Interface.Repozitorij;
import hr.algebra.influencer.Model.Brand;
import hr.algebra.influencer.Model.BrandSuradnja;
import hr.algebra.influencer.Model.Enum.StatusSuradnje;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Nisa;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Model.TipSadrzaja;

import java.util.List;
import java.util.function.Function;

public final class DummyPodaciUtil
{

    private DummyPodaciUtil()
    {
    }

    public static void ispuniDummyPodatke()
    {
        dodajSifrarnik(PlatformaRepozitorij.getInstance(), Platforma::getNaziv, Platforma::new,
                List.of("Instagram", "TikTok", "YouTube", "Twitch"));
        dodajSifrarnik(NisaRepozitorij.getInstance(), Nisa::getNaziv, Nisa::new,
                List.of("Beauty", "Fitness", "Tech", "Lifestyle", "Gaming"));
        dodajSifrarnik(TipSadrzajaRepozitorij.getInstance(), TipSadrzaja::getNaziv, TipSadrzaja::new,
                List.of("Reels", "Shorts", "Recenzije", "Tutoriali"));
        dodajSifrarnik(GradRepozitorij.getInstance(), Grad::getNaziv, Grad::new,
                List.of("Zagreb", "Split", "Rijeka", "Osijek", "Zadar"));
        dodajSifrarnik(BrandRepozitorij.getInstance(), Brand::getNaziv, Brand::new,
                List.of("Nike", "Adidas", "Coca-Cola", "Samsung", "Zara"));

        List<Platforma> platforme = PlatformaRepozitorij.getInstance().getAll();
        List<Nisa> nise = NisaRepozitorij.getInstance().getAll();
        List<TipSadrzaja> tipovi = TipSadrzajaRepozitorij.getInstance().getAll();
        List<Grad> gradovi = GradRepozitorij.getInstance().getAll();

        dodajInfluencere(gradovi, platforme, nise, tipovi);

        List<Brand> brandovi = BrandRepozitorij.getInstance().getAll();
        List<Influencer> influenceri = InfluencerRepozitorij.getInstance().getAll();
        dodajBrandSuradnje(brandovi, influenceri);
    }

    private static <T> void dodajSifrarnik(Repozitorij<T> repozitorij, Function<T, String> nazivGetter,
                                            Function<String, T> tvorac, List<String> nazivi)
    {
        for (String naziv : nazivi)
        {
            T novi = tvorac.apply(naziv);
            if (!repozitorij.isDuplicate(nazivGetter, novi))
            {
                repozitorij.create(novi);
            }
        }
    }

    private static void dodajInfluencere(List<Grad> gradovi, List<Platforma> platforme, List<Nisa> nise, List<TipSadrzaja> tipovi)
    {
        InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();

        String[][] podaci = {
                {"Igor Belan", "109000", "4.2", "Zagreb", "Gaming"},
                {"Marijana Batinić", "250000", "5.8", "Split", "Fitness"},
                {"Antonija Blaće", "180000", "3.9", "Rijeka", "Lifestyle"},
                {"Petra Kovač", "95000", "6.1", "Osijek", "Tech"},
                {"Filip Horvat", "420000", "3.3", "Zadar", "Beauty"}
        };

        for (int i = 0; i < podaci.length; i++)
        {
            String imeNadimak = podaci[i][0];
            int brojPratitelja = Integer.parseInt(podaci[i][1]);
            double engagementRate = Double.parseDouble(podaci[i][2]);
            String gradNaziv = podaci[i][3];
            String nisaNaziv = podaci[i][4];

            Influencer influencer = new Influencer(imeNadimak, brojPratitelja, engagementRate, "Hrvatska",
                    pronadiPoNazivu(gradovi, Grad::getNaziv, gradNaziv), "Hrvatski", "");

            if (influencerRepozitorij.isDuplicate(Influencer::getImeNadimak, influencer))
            {
                continue;
            }

            influencer.setPlatforme(List.of(platforme.get(i % platforme.size())));
            influencer.setNise(List.of(pronadiPoNazivu(nise, Nisa::getNaziv, nisaNaziv)));
            influencer.setTipoviSadrzaja(List.of(tipovi.get(i % tipovi.size())));
            influencerRepozitorij.create(influencer);
        }
    }

    private static void dodajBrandSuradnje(List<Brand> brandovi, List<Influencer> influenceri)
    {
        if (brandovi.isEmpty() || influenceri.isEmpty())
        {
            return;
        }

        BrandSuradnjaRepozitorij brandSuradnjaRepozitorij = BrandSuradnjaRepozitorij.getInstance();
        StatusSuradnje[] statusi = {StatusSuradnje.PLANIRANA, StatusSuradnje.AKTIVNA, StatusSuradnje.ZAVRSENA};

        BrandSuradnja dummySuradnja = new BrandSuradnja("Dummy suradnja",
                pronadiPoNazivu(brandovi, Brand::getNaziv, "Nike"), 2026, StatusSuradnje.PLANIRANA);
        if (!brandSuradnjaRepozitorij.isDuplicate(BrandSuradnja::getNazivKampanje, dummySuradnja))
        {
            Influencer igorBelan = pronadiPoNazivu(influenceri, Influencer::getImeNadimak, "Igor Belan");
            dummySuradnja.setTim(List.of(igorBelan != null ? igorBelan : influenceri.get(0)));
            brandSuradnjaRepozitorij.create(dummySuradnja);
        }

        for (int i = 0; i < 3 && i < brandovi.size(); i++)
        {
            Brand brand = brandovi.get(i);
            String nazivKampanje = "Kampanja " + brand.getNaziv() + " 2026";

            BrandSuradnja suradnja = new BrandSuradnja(nazivKampanje, brand, 2026, statusi[i % statusi.length]);
            if (brandSuradnjaRepozitorij.isDuplicate(BrandSuradnja::getNazivKampanje, suradnja))
            {
                continue;
            }

            suradnja.setTim(List.of(influenceri.get(i % influenceri.size())));
            brandSuradnjaRepozitorij.create(suradnja);
        }
    }

    private static <T> T pronadiPoNazivu(List<T> lista, Function<T, String> nazivGetter, String naziv)
    {
        return lista.stream()
                .filter(stavka -> nazivGetter.apply(stavka).equalsIgnoreCase(naziv))
                .findFirst()
                .orElse(null);
    }
}
