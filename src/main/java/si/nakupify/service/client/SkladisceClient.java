package si.nakupify.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import si.nakupify.service.dto.ZalogaDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

@ApplicationScoped
public class SkladisceClient {

    private HttpClient client;
    private ObjectMapper mapper;

    @ConfigProperty(name="skladisce.url")
    private String skladisceUrl;

    private Logger log = Logger.getLogger(SkladisceClient.class.getName());

    @PostConstruct
    public void init() {
        client = HttpClient.newBuilder().build();
        mapper = new ObjectMapper();
    }

    public ZalogaDTO getZalogaDTO(Long id_izdelek) {
        try {
            String url = skladisceUrl + "/zaloga/" + id_izdelek;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = mapper.readTree(response.body());

            if (response.statusCode() == 404) {
                log.info("HTTP response code 404: Zaloga izdelka z id=" + id_izdelek + " ne obstaja!");
                return null;
            }

            return new ZalogaDTO(node.path("id_product").asLong(), node.path("stock").asInt(), node.path("reserved").asInt());
        } catch (Exception e) {
            log.severe("Communication error: Napaka pri komunikaciji z microservice-skladisce. Napaka: " + e.getMessage());
            return null;
        }
    }

    public void postZalogaDTO(Long id_izdelek) {
        try {
            ZalogaDTO zaloga = new ZalogaDTO(id_izdelek, 0, 0);
            String url = skladisceUrl + "/zaloga";
            String payload = mapper.writeValueAsString(zaloga);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode node = mapper.readTree(response.body());

            if (response.statusCode() == 409) {
                log.info("HTTP response code 409: Zaloga izdelka z id=" + id_izdelek + " že obstaja!");
            }
        } catch (Exception e) {
            log.severe("Communication error: Napaka pri komunikaciji z microservice-skladisce. Napaka: " + e.getMessage());
        }
    }

}
