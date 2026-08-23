package hr.algebra.influencer.Utilization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

// Pomocna klasa za parsiranje JSON odgovora s API-ja.
public final class JsonParserUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonParserUtil() {
    }

    // Parsira JSON odgovor s countriesnow /countries API-ja i izvlaci listu gradova za zadanu drzavu.
    // json - sirovi JSON string iz HTTP odgovora (oblik {"data": [{"country": "...", "cities": [...]}, ...]})
    // drzava - naziv drzave cije gradove trazimo (npr. "Croatia")
    // Vraca listu naziva gradova ili praznu listu ako drzava nije pronadjena.
    public static List<String> parseGradove(String json, String drzava) {
        try {
            JsonNode korijen = MAPPER.readTree(json);
            JsonNode data = korijen.get("data");

            List<String> gradovi = new ArrayList<>();
            if (data != null && data.isArray()) {
                for (JsonNode drzavaCvor : data) {
                    JsonNode ime = drzavaCvor.get("country");
                    if (ime == null || !ime.asText().equalsIgnoreCase(drzava)) {
                        continue;
                    }
                    JsonNode cities = drzavaCvor.get("cities");
                    if (cities != null && cities.isArray()) {
                        for (JsonNode cvor : cities) {
                            String naziv = cvor.asText().trim();
                            if (!naziv.isEmpty()) {
                                gradovi.add(naziv);
                            }
                        }
                    }
                    break;
                }
            }
            return gradovi;
        } catch (Exception e) {
            throw new RuntimeException("Greska pri parsiranju JSON-a gradova.", e);
        }
    }
}
