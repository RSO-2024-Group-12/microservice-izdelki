package si.nakupify.endpoint.v1.REST;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.LastnostDTO;
import si.nakupify.service.dto.SlikaDTO;

import java.util.List;
import java.util.logging.Logger;

@Path("/v1/izdelki")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IzdelekREST {

    @Inject
    IzdelekService izdelekService;

    private Logger log = Logger.getLogger(IzdelekREST.class.getName());

    public boolean validacija(IzdelekDTO izdelekDTO) {
        if (izdelekDTO == null ||
                izdelekDTO.getNaziv() == null || izdelekDTO.getNaziv().isBlank() ||
                izdelekDTO.getOpis() == null || izdelekDTO.getOpis().isBlank() ||
                izdelekDTO.getCena() == null || izdelekDTO.getCena() <= 0) {
            log.info("Podani manjkajoči ali nepravilni podatki!");
            return false;
        }

        for (SlikaDTO slika : izdelekDTO.getSlike()) {
            if (slika.getId_slika() == null ||
                    slika.getUrl() == null || slika.getUrl().isBlank()) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        for (SlikaDTO slika : izdelekDTO.getSlikeDodaj()) {
            if (slika.getUrl() == null || slika.getUrl().isBlank()) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        for (SlikaDTO slika : izdelekDTO.getSlikeBrisi()) {
            if (slika.getId_slika() == null) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        for (LastnostDTO lastnost : izdelekDTO.getLastnosti()) {
            if (lastnost.getId_lastnost() == null ||
                    lastnost.getLastnost() == null || lastnost.getLastnost().isBlank() ||
                    lastnost.getVrednost() == null || lastnost.getVrednost().isBlank()) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        for (LastnostDTO lastnost : izdelekDTO.getLastnostiDodaj()) {
            if (lastnost.getLastnost() == null || lastnost.getLastnost().isBlank() ||
                    lastnost.getVrednost() == null || lastnost.getVrednost().isBlank()) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        for (LastnostDTO lastnost : izdelekDTO.getLastnostiBrisi()) {
            if (lastnost.getId_lastnost() == null) {
                log.info("Podani manjkajoči ali nepravilni podatki!");
                return false;
            }
        }

        return true;
    }

    @GET
    @Operation(summary="Pridobi vse izdelke", description="Vrne seznam vseh izdelkov.")
    @APIResponses({
            @APIResponse(responseCode="200", description="(OK) Uspešno vrne seznam vseh izdelkov.")
    })
    public Response getVsiIzdelki() {
        List<IzdelekDTO> izdelki = izdelekService.pridobiVseIzdelke();
        return Response.status(Response.Status.OK).entity(izdelki).build();
    }

    @GET
    @Path("/aktivni")
    @Operation(summary="Pridobi vse aktivne izdelke", description="Vrne seznam vseh aktivnih izdelkov.")
    @APIResponses({
            @APIResponse(responseCode="200", description="(OK) Uspešno vrne seznam vseh aktivnih izdelkov.")
    })
    public Response getVsiAktivniIzdelki() {
        List<IzdelekDTO> izdelki = izdelekService.pridobiVseAktivneIzdelke();
        return Response.status(Response.Status.OK).entity(izdelki).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary="Pridobi izdelek", description="Vrne izdelek s podanim id.")
    @APIResponses({
            @APIResponse(responseCode="200", description="(OK) Uspešno vrne izdelek s podanim id."),
            @APIResponse(responseCode="400", description="(BAD_REQUEST) Podana nepravilna oblika id v url."),
            @APIResponse(responseCode="404", description="(NOT_FOUND) Izdelka ni bilo mogoče najti.")
    })
    public Response getIzdelek(@PathParam("id") Long id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.pridobiIzdelek(id);
        if (izdelek == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(izdelek).build();
    }

    @POST
    @Operation(summary="Ustvari nov izdelek", description="Ustvari in shrani nov izdelek.<br>" +
            "Potrebuje le polja naziv, opis, cena.<br>" +
            "Slike in lastnosti novega izdelka morajo biti podane v poljih slikeDodaj in stnostiDodaj.")
    @APIResponses({
            @APIResponse(responseCode="201", description="(CREATED) Uspešno ustvarjen in shranjen nov izdelek."),
            @APIResponse(responseCode="400", description="(BAD_REQUEST) Podana nepravilna oblika oz. nepopolna oblika IzdelekDTO.")
    })
    public Response createIzdelek(IzdelekDTO izdelekDTO) {
        if (!validacija(izdelekDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.dodajIzdelek(izdelekDTO);

        return Response.status(Response.Status.CREATED).entity(izdelek).build();
    }

    @PUT
    @Operation(summary="Posodobi izdelek", description="Posodobi obstoječ izdelek.<br>" +
            "Slike v polju slikeDodaj bodo dodane, v polju slike bodo posodobljene, v polju slikeBrisi bodo izbrisane.<br>" +
            "Lastnosti v polju lastnostiDodaj bodo dodane, v polju lastnosti bodo posodobljene, v polju lastnostiBrisi bodo izbrisane.")
    @APIResponses({
            @APIResponse(responseCode="200", description="(OK) Uspešno posodobljen izdelek."),
            @APIResponse(responseCode="400", description="(BAD_REQUEST) Podana nepravilna oblika oz. nepopolna oblika IzdelekDTO."),
            @APIResponse(responseCode="404", description="(NOT_FOUND) Izdelka ni bilo mogoče najti.")
    })
    public Response updateIzdelek(IzdelekDTO izdelekDTO) {
        if (!validacija(izdelekDTO)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.posodobiIzdelek(izdelekDTO);
        if (izdelek == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(izdelek).build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary="Izbriši izdelek", description="Izbriši oziroma naredi mehki izbris nad izdelkom s podanim id.")
    @APIResponses({
            @APIResponse(responseCode="204", description="(NO_CONTENT) Uspešno izbriše izdelek s podanim id."),
            @APIResponse(responseCode="400", description="(BAD_REQUEST) Podana nepravilna oblika id v url."),
            @APIResponse(responseCode="404", description="(NOT_FOUND) Izdelka ni bilo mogoče najti.")
    })
    public Response deleteIzdelek(@PathParam("id") Long id) {
        if (id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.izbrisiIzdelek(id);
        if (izdelek == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
