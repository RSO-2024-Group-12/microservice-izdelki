package si.nakupify.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import si.nakupify.service.dto.ErrorDTO;
import si.nakupify.service.dto.PairDTO;
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

    public PairDTO<ZalogaDTO, ErrorDTO> getZalogaDTO(Long id_izdelek) {
        try {
            String url = skladisceUrl + "/zaloga/" + id_izdelek;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                log.info("HTTP response code 404: Zaloge za izdelk z id=" + id_izdelek + " ni bilo mogoče najti");
                ErrorDTO error = mapper.readValue(response.body(), ErrorDTO.class);
                return new PairDTO<>(null, error);
            }

            ZalogaDTO zalogaDTO = mapper.readValue(response.body(), ZalogaDTO.class);

            return new PairDTO<>(zalogaDTO, null);
        } catch (Exception e) {
            log.severe("Communication error: Napaka pri komunikaciji z microservice-skladisce. Napaka: " + e.getMessage());
            ErrorDTO error = new ErrorDTO(503, "Napaka pri komunikaciji z microservice-skladisce.");
            return new PairDTO<>(null, error);
        }
    }

    public ErrorDTO postZalogaDTO(Long id_izdelek) {
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

            if (response.statusCode() == 409) {
                ErrorDTO error = mapper.readValue(response.body(), ErrorDTO.class);
                log.info("HTTP response code 409: Zaloga izdelka z id=" + id_izdelek + " že obstaja!");
                return error;
            }

            return null;
        } catch (Exception e) {
            log.severe("Communication error: Napaka pri komunikaciji z microservice-skladisce. Napaka: " + e.getMessage());
            ErrorDTO error = new ErrorDTO(503, "Napaka pri komunikaciji z microservice-skladisce.");
            return error;
        }
    }

}
