package si.nakupify;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import si.nakupify.service.IzdelekService;
import si.nakupify.service.TenantService;
import si.nakupify.service.dto.ErrorDTO;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.PairDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class IzdelekEndpointTest {

    @InjectMock
    IzdelekService izdelekService;

    @InjectMock
    TenantService tenantService;

    private IzdelekDTO izdelekDTO(Long id) {
        IzdelekDTO izdelekDTO = new IzdelekDTO();
        izdelekDTO.setId_izdelek(id);
        izdelekDTO.setNaziv("Pametna ura Z");
        izdelekDTO.setOpis("Ura z merjenjem srčnega utripa, GPS-om in dolgo baterijo");
        izdelekDTO.setCena(199.99F);
        return izdelekDTO;
    }

    private ErrorDTO errorDTO(int code, String message) {
        return new ErrorDTO(code, message);
    }

    @Test
    void getIzdelek_test() {
        when(tenantService.getTenant()).thenReturn("org1");
        when(izdelekService.pridobiIzdelek(any(), any())).thenReturn(new PairDTO<>(izdelekDTO(5L), null));

        given()
                .accept(ContentType.JSON)
        .when()
                .get("/v1/izdelki/5")
        .then()
                .statusCode(200)
                .body("id_izdelek", equalTo(5));

        verify(tenantService).getTenant();
        verify(izdelekService).pridobiIzdelek(any(), any());
    }

    @Test
    void createIzdelek_test() {
        when(tenantService.getTenant()).thenReturn("org1");
        when(izdelekService.dodajIzdelek(any(), any())).thenReturn(new PairDTO<>(izdelekDTO(5L), null));

        String requestBody = """
        {
          "naziv": "Pametna ura Z",
          "opis": "Ura z merjenjem srčnega utripa, GPS-om in dolgo baterijo",
          "cena": 199.99,
          "lastnostiDodaj": [
            {"lastnost": "Barva", "vrednost": "Srebrna"}
          ],
          "slikeDodaj": [
            {"url": "https://example.com/slike/pametna-ura-z.jpg"}
          ]
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .post("/v1/izdelki")
        .then()
                .statusCode(201)
                .body("id_izdelek", equalTo(5));

        verify(tenantService).getTenant();
        verify(izdelekService).dodajIzdelek(any(), any());
    }

    @Test
    void updateIzdelek_test() {
        when(tenantService.getTenant()).thenReturn("org1");
        when(izdelekService.posodobiIzdelek(any(), any())).thenReturn(new PairDTO<>(izdelekDTO(5L), null));

        String requestBody = """
        {
          "id_izdelek": 5,
          "naziv": "Pametna ura Z",
          "opis": "Ura z merjenjem srčnega utripa, GPS-om in dolgo baterijo",
          "cena": 199.99,
          "lastnostiDodaj": [
            {"lastnost": "Barva", "vrednost": "Srebrna"}
          ],
          "slikeDodaj": [
            {"url": "https://example.com/slike/pametna-ura-z.jpg"}
          ]
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
        .when()
                .put("/v1/izdelki")
        .then()
                .statusCode(200)
                .body("id_izdelek", equalTo(5));

        verify(tenantService).getTenant();
        verify(izdelekService).posodobiIzdelek(any(), any());
    }

    @Test
    void deleteIzdelek_test() {
        when(tenantService.getTenant()).thenReturn("org1");
        when(izdelekService.izbrisiIzdelek(any(), any())).thenReturn(new PairDTO<>(izdelekDTO(5L), null));

        given()
                .accept(ContentType.JSON)
        .when()
                .delete("/v1/izdelki/5")
        .then()
                .statusCode(204);

        verify(tenantService).getTenant();
        verify(izdelekService).izbrisiIzdelek(any(), any());
    }
}
