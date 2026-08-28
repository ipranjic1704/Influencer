package hr.algebra.influencer.Task;

import hr.algebra.influencer.DataAccessLayer.Implementation.InfluencerRepozitorij;
import hr.algebra.influencer.DataAccessLayer.Implementation.PlatformaRepozitorij;
import hr.algebra.influencer.Model.Influencer;
import hr.algebra.influencer.Model.Platforma;
import hr.algebra.influencer.Utilization.ConfigUtil;
import hr.algebra.influencer.Utilization.HttpUtil;
import hr.algebra.influencer.Utilization.JsonParserUtil;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UvozInfluenceraYoutubeTask extends Task<Integer>
{

    private static final String SEARCH_URL =
            "https://www.googleapis.com/youtube/v3/search?part=snippet&type=channel&maxResults=10&q=influencer";
    private static final String CHANNELS_URL =
            "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=";

    private final InfluencerRepozitorij influencerRepozitorij = InfluencerRepozitorij.getInstance();
    private final PlatformaRepozitorij platformaRepozitorij = PlatformaRepozitorij.getInstance();

    @Override
    protected Integer call() throws Exception
    {
        String kljuc = ConfigUtil.getYoutubeApiKljuc();
        if (kljuc == null || kljuc.isBlank())
        {
            throw new RuntimeException("YouTube API kljuc nije postavljen u config.xml.");
        }

        updateMessage("Pretrazujem YouTube kanale...");
        String searchOdgovor = HttpUtil.fetchJson(SEARCH_URL + "&key=" + kljuc);
        List<String> kanalIdovi = JsonParserUtil.parseYoutubeKanalId(searchOdgovor);
        if (kanalIdovi.isEmpty())
        {
            throw new RuntimeException("API nije vratio nijedan kanal.");
        }

        String channelsOdgovor = HttpUtil.fetchJson(CHANNELS_URL + String.join(",", kanalIdovi) + "&key=" + kljuc);
        List<Influencer> kanali = JsonParserUtil.parseYoutubeInfluencere(channelsOdgovor);

        Platforma youtube = platformaRepozitorij.getAll().stream()
                .filter(p -> p.getNaziv().equalsIgnoreCase("YouTube"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Platforma 'YouTube' ne postoji u sifrarniku."));

        Set<String> postojeci = influencerRepozitorij.getAll().stream()
                .map(i -> i.getImeNadimak().toLowerCase().trim())
                .collect(Collectors.toCollection(HashSet::new));

        int uvezeno = 0;
        int ukupno = kanali.size();
        for (int i = 0; i < ukupno; i++)
        {
            if (isCancelled())
            {
                break;
            }
            Influencer kanal = kanali.get(i);
            String kljucNaziva = kanal.getImeNadimak().toLowerCase().trim();

            if (postojeci.add(kljucNaziva))
            {
                kanal.setPlatforme(List.of(youtube));
                influencerRepozitorij.create(kanal);
                uvezeno++;
            }

            updateProgress(i + 1, ukupno);
            updateMessage("Obradjeno " + (i + 1) + "/" + ukupno + " (novih: " + uvezeno + ")");
        }

        updateMessage("Uvoz zavrsen. Novih influencera: " + uvezeno);
        return uvezeno;
    }
}
