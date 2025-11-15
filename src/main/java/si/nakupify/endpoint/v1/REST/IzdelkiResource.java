package si.nakupify.endpoint.v1.REST;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.dto.IzdelekDTO;

import java.util.List;

@Path("/v1/izdelki")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IzdelkiResource {

    @Inject
    IzdelekService izdelekService;

    @GET
    public Response getVsiIzdelki() {
        List<IzdelekDTO> izdelki = izdelekService.pridobiVseIzdelke();
        return Response.status(Response.Status.OK).entity(izdelki).build();
    }

    @GET
    @Path("/aktivni")
    public Response getVsiAktivniIzdelki() {
        List<IzdelekDTO> izdelki = izdelekService.pridobiAktivneIzdelke();
        return Response.status(Response.Status.OK).entity(izdelki).build();
    }

    @GET
    @Path("{id}")
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
    public Response createIzdelek(IzdelekDTO izdelekDTO) {
        if (izdelekDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.ustvariIzdelek(izdelekDTO);
        if (izdelek == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.CREATED).entity(izdelek).build();
    }

    @PUT
    public Response upgradeIzdelek(IzdelekDTO izdelekDTO) {
        if (izdelekDTO == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        IzdelekDTO izdelek = izdelekService.posodobiIzdelek(izdelekDTO);
        if (izdelek.getId_izdelek() == -1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else if (izdelek.getId_izdelek() == -2) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(izdelek).build();
    }

    @DELETE
    @Path("{id}")
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
