package hr.algebra.influencer.Utilization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class JsonParserUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonParserUtil() {
    }

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
