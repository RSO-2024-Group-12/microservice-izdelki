package si.nakupify.endpoint.v1;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.dto.*;

import java.util.List;
import java.util.logging.Logger;

@Path("/v1/izdelki")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IzdelekREST {

    @Inject
    IzdelekService izdelekService;

    private Logger log = Logger.getLogger(IzdelekREST.class.getName());

    public ErrorDTO validacija(IzdelekDTO izdelekDTO, int mode) {
        if (izdelekDTO == null) {
            log.info("Validation fail: IzdelekDTO ne sme biti null");
            String msg = "Mora biti podan IzdelekDTO!";
            return new ErrorDTO(400, msg);
        }

        if (mode == 1 && izdelekDTO.getId_izdelek() == null) {
            log.info("Validation fail: IzdelekDTO mora imeti podana polja: id_izdelek");
            String msg = "Polje id_izdelek mora biti podano!";
            return new ErrorDTO(400, msg);
        }

        if (izdelekDTO.getNaziv() == null || izdelekDTO.getNaziv().isBlank() ||
                izdelekDTO.getOpis() == null || izdelekDTO.getOpis().isBlank() ||
                izdelekDTO.getCena() == null || izdelekDTO.getCena() <= 0) {
            log.info("Validation fail: IzdelekDTO mora imeti podana polja: naziv, opis, cena");
            String msg = "Polja naziv, opis in cena ne smejo biti prazna!";
            return new ErrorDTO(400, msg);
        }

        if (izdelekDTO.getSlike() != null) {
            for (SlikaDTO slika : izdelekDTO.getSlike()) {
                if (slika.getId_slika() == null || slika.getUrl() == null || slika.getUrl().isBlank()) {
                    log.info("Validation fail: SlikaDTO za posodobitev mora imeti podana polja: id_slika, url");
                    String msg = "Pri posodabljanju slike polji id_slika in url ne smeta biti prazni!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        if (izdelekDTO.getSlikeDodaj() != null) {
            for (SlikaDTO slika : izdelekDTO.getSlikeDodaj()) {
                if (slika.getUrl() == null || slika.getUrl().isBlank()) {
                    log.info("Validation fail: SlikaDTO za dodajanje mora imeti podana polja: url");
                    String msg = "Pri dodajanju slike polje url ne sme biti prazno!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        if (izdelekDTO.getSlikeBrisi() != null) {
            for (SlikaDTO slika : izdelekDTO.getSlikeBrisi()) {
                if (slika.getId_slika() == null) {
                    log.info("Validation fail: SlikaDTO za brisanje mora imeti podana polja: id_slika");
                    String msg = "Pri brisanju slike polje id_slika ne sme biti prazno!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        if (izdelekDTO.getLastnosti() != null) {
            for (LastnostDTO lastnost : izdelekDTO.getLastnosti()) {
                if (lastnost.getId_lastnost() == null ||
                        lastnost.getLastnost() == null || lastnost.getLastnost().isBlank() ||
                        lastnost.getVrednost() == null || lastnost.getVrednost().isBlank()) {
                    log.info("Validation fail: LastnostDTO za posodabljanje mora imeti podana polja: id_lastnost, lastnost, vrednost");
                    String msg = "Pri posodabljanju lastnosti polja id_lastnost, lastnost in vrednost ne smejo biti prazna!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        if (izdelekDTO.getLastnostiDodaj() != null) {
            for (LastnostDTO lastnost : izdelekDTO.getLastnostiDodaj()) {
                if (lastnost.getLastnost() == null || lastnost.getLastnost().isBlank() ||
                        lastnost.getVrednost() == null || lastnost.getVrednost().isBlank()) {
                    log.info("Validation fail: LastnostDTO za dodajanje mora imeti podana polja: id_lastnost, vrednost");
                    String msg = "Pri dodajanju lastnosti polji lastnost in vrednost ne smeta biti prazni!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        if (izdelekDTO.getLastnostiBrisi() != null) {
            for (LastnostDTO lastnost : izdelekDTO.getLastnostiBrisi()) {
                if (lastnost.getId_lastnost() == null) {
                    log.info("Validation fail: LastnostDTO za brisanje mora imeti podana polja: id_lastnost");
                    String msg = "Pri brisanju lastnosti poljeid_ lastnost ne sme biti prazno!";
                    return new ErrorDTO(400, msg);
                }
            }
        }

        return null;
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
            ErrorDTO parameterError = new ErrorDTO(400, "V URL mora biti podan parameter id.");
            log.info("Path parameter error: V URL ni podanega id");
            return Response.status(Response.Status.BAD_REQUEST).entity(parameterError).build();
        }

        PairDTO<IzdelekDTO, ErrorDTO> pair = izdelekService.pridobiIzdelek(id);
        IzdelekDTO izdelek = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            if (error.getErrorCode() == 404) {
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
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
        ErrorDTO validationError = validacija(izdelekDTO, 0);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        PairDTO<IzdelekDTO, ErrorDTO> pair = izdelekService.dodajIzdelek(izdelekDTO);
        IzdelekDTO izdelek = pair.getValue();

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
        ErrorDTO validationError = validacija(izdelekDTO, 1);
        if (validationError != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationError).build();
        }

        PairDTO<IzdelekDTO, ErrorDTO> pair  = izdelekService.posodobiIzdelek(izdelekDTO);
        IzdelekDTO izdelek = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            if (error.getErrorCode() == 404) {
                return Response.status(Response.Status.NOT_FOUND).entity(pair.getError()).build();
            }
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
            ErrorDTO parameterError = new ErrorDTO(400, "V URL mora biti podan parameter id.");
            log.info("Path parameter error: V URL ni podanega id");
            return Response.status(Response.Status.BAD_REQUEST).entity(parameterError).build();
        }

        PairDTO<IzdelekDTO, ErrorDTO> pair  = izdelekService.izbrisiIzdelek(id);
        IzdelekDTO izdelek = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            if (error.getErrorCode() == 404) {
                return Response.status(Response.Status.NOT_FOUND).entity(error).build();
            }
        }

        return Response.status(Response.Status.NO_CONTENT).entity(izdelek).build();
    }
}
