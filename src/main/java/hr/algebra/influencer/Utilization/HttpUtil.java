package hr.algebra.influencer.Utilization;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class HttpUtil
{

    private static final HttpClient KLIJENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    private HttpUtil()
    {
    }

    public static String fetchJson(String url)
    {
        try
        {
            HttpRequest zahtjev = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> odgovor = KLIJENT.send(zahtjev, HttpResponse.BodyHandlers.ofString());
            if (odgovor.statusCode() < 200 || odgovor.statusCode() >= 300)
            {
                throw new RuntimeException("API zahtjev nije uspio. HTTP status: " + odgovor.statusCode());
            }
            return odgovor.body();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Greska pri komunikaciji s API-jem.", e);
        }
    }

    public static String postJson(String url, String tijelo)
    {
        try
        {
            HttpRequest zahtjev = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(tijelo))
                    .build();

            HttpResponse<String> odgovor = KLIJENT.send(zahtjev, HttpResponse.BodyHandlers.ofString());
            if (odgovor.statusCode() < 200 || odgovor.statusCode() >= 300)
            {
                throw new RuntimeException("API zahtjev nije uspio. HTTP status: " + odgovor.statusCode());
            }
            return odgovor.body();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Greska pri komunikaciji s API-jem.", e);
        }
    }
}
