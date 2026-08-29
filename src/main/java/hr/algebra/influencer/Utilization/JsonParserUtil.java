package hr.algebra.influencer.Utilization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.influencer.Model.Influencer;

import java.util.ArrayList;
import java.util.List;

public final class JsonParserUtil
{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonParserUtil()
    {
    }

    public static List<String> parseGradove(String json, String drzava)
    {
        try
        {
            JsonNode korijen = MAPPER.readTree(json);
            JsonNode data = korijen.get("data");

            List<String> gradovi = new ArrayList<>();
            if (data != null && data.isArray())
            {
                for (JsonNode drzavaCvor : data)
                {
                    JsonNode ime = drzavaCvor.get("country");
                    if (ime == null || !ime.asText().equalsIgnoreCase(drzava))
                    {
                        continue;
                    }
                    JsonNode cities = drzavaCvor.get("cities");
                    if (cities != null && cities.isArray())
                    {
                        for (JsonNode cvor : cities)
                        {
                            String naziv = cvor.asText().trim();
                            if (!naziv.isEmpty())
                            {
                                gradovi.add(naziv);
                            }
                        }
                    }
                    break;
                }
            }
            return gradovi;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Greska pri parsiranju JSON-a gradova.", e);
        }
    }

    public static List<String> parseYoutubeKanalId(String json)
    {
        try
        {
            JsonNode korijen = MAPPER.readTree(json);
            JsonNode items = korijen.get("items");

            List<String> idovi = new ArrayList<>();
            if (items != null && items.isArray())
            {
                for (JsonNode stavka : items)
                {
                    JsonNode id = stavka.get("id");
                    if (id != null && id.has("channelId"))
                    {
                        idovi.add(id.get("channelId").asText());
                    }
                }
            }
            return idovi;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Greska pri parsiranju JSON-a YouTube pretrage.", e);
        }
    }

    public static List<Influencer> parseYoutubeInfluencere(String json)
    {
        try
        {
            JsonNode korijen = MAPPER.readTree(json);
            JsonNode items = korijen.get("items");

            List<Influencer> influenceri = new ArrayList<>();
            if (items != null && items.isArray())
            {
                for (JsonNode stavka : items)
                {
                    JsonNode snippet = stavka.get("snippet");
                    JsonNode statistics = stavka.get("statistics");
                    if (snippet == null || statistics == null)
                    {
                        continue;
                    }

                    String naziv = snippet.path("title").asText("");
                    if (naziv.isBlank())
                    {
                        continue;
                    }

                    int brojPretplatnika = statistics.path("subscriberCount").asInt(0);
                    String zemlja = snippet.path("country").asText("");
                    String jezik = snippet.path("defaultLanguage").asText("");

                    influenceri.add(new Influencer(naziv, brojPretplatnika, 0.0, zemlja, null, jezik, ""));
                }
            }
            return influenceri;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Greska pri parsiranju JSON-a YouTube kanala.", e);
        }
    }
}
