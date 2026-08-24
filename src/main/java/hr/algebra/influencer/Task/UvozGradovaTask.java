package hr.algebra.influencer.Task;

import hr.algebra.influencer.DataAccessLayer.Implementation.GradRepozitorij;
import hr.algebra.influencer.Model.Grad;
import hr.algebra.influencer.Utilization.HttpUtil;
import hr.algebra.influencer.Utilization.JsonParserUtil;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UvozGradovaTask extends Task<Integer>
{

    private static final String API_URL = "https://countriesnow.space/api/v0.1/countries";
    private static final String DRZAVA = "Croatia";

    private final GradRepozitorij gradRepozitorij = GradRepozitorij.getInstance();

    @Override
    protected Integer call() throws Exception
    {
        updateMessage("Dohvacam gradove s API-a...");
        String odgovor = HttpUtil.fetchJson(API_URL);

        List<String> naziviGradova = JsonParserUtil.parseGradove(odgovor, DRZAVA);
        if (naziviGradova.isEmpty())
        {
            throw new RuntimeException("API nije vratio nijedan grad.");
        }

        Set<String> postojeci = gradRepozitorij.getAll().stream()
                .map(g -> g.getNaziv().toLowerCase().trim())
                .collect(Collectors.toCollection(HashSet::new));

        int uvezeno = 0;
        int ukupno = naziviGradova.size();
        for (int i = 0; i < ukupno; i++)
        {
            if (isCancelled())
            {
                break;
            }
            String naziv = naziviGradova.get(i).trim();
            String kljuc = naziv.toLowerCase();

            if (!naziv.isEmpty() && postojeci.add(kljuc))
            {
                gradRepozitorij.create(new Grad(naziv));
                uvezeno++;
            }

            updateProgress(i + 1, ukupno);
            updateMessage("Obradjeno " + (i + 1) + "/" + ukupno + " (novih: " + uvezeno + ")");
        }

        updateMessage("Uvoz zavrsen. Novih gradova: " + uvezeno);
        return uvezeno;
    }
}
