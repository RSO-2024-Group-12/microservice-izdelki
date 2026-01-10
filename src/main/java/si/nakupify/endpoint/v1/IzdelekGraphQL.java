package si.nakupify.endpoint.v1;

import graphql.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.TenantService;
import si.nakupify.service.dto.ErrorDTO;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.PairDTO;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@GraphQLApi
public class IzdelekGraphQL {

    @Inject
    IzdelekService izdelekService;

    @Inject
    TenantService tenantService;

    private Logger log = Logger.getLogger(IzdelekGraphQL.class.getName());

    @Query("allIzdelki")
    @Description("Vrne seznam vseh izdelkov.")
    public List<IzdelekDTO> getIzdelki() {
        String tenant = tenantService.getTenant();

        if (tenant == null) {
            ErrorDTO authError = new ErrorDTO(400, "Mora biti podan JWT.");
            throw GraphqlErrorException.newErrorException()
                    .message(authError.getError())
                    .extensions(Map.of(
                            "code", authError.getErrorCode(),
                            "error", authError.getError()
                    ))
                    .build();

        }

        PairDTO<List<IzdelekDTO>, ErrorDTO> pair = izdelekService.pridobiVseIzdelke(tenant);
        List<IzdelekDTO> izdelki = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            throw GraphqlErrorException.newErrorException()
                    .message(error.getError())
                    .extensions(Map.of(
                            "code", error.getErrorCode(),
                            "error", error.getError()
                    ))
                    .build();
        }

        return izdelki;
    }

    @Query("allAktivniIzdelki")
    @Description("Vrne seznam vseh aktivnih izdelkov.")
    public List<IzdelekDTO> getAktivniIzdelki() {
        String tenant = tenantService.getTenant();

        if (tenant == null) {
            ErrorDTO authError = new ErrorDTO(400, "Mora biti podan JWT.");
            throw GraphqlErrorException.newErrorException()
                    .message(authError.getError())
                    .extensions(Map.of(
                            "code", authError.getErrorCode(),
                            "error", authError.getError()
                    ))
                    .build();

        }

        PairDTO<List<IzdelekDTO>, ErrorDTO> pair = izdelekService.pridobiVseAktivneIzdelke(tenant);
        List<IzdelekDTO> izdelki = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            throw GraphqlErrorException.newErrorException()
                    .message(error.getError())
                    .extensions(Map.of(
                            "code", error.getErrorCode(),
                            "error", error.getError()
                    ))
                    .build();
        }

        return izdelki;
    }

    @Query("getIzdelek")
    @Description("Vrne izdelek s podanim id.")
    public IzdelekDTO getIzdelek(@Name("id") Long id) {
        String tenant = tenantService.getTenant();

        if (tenant == null) {
            ErrorDTO authError = new ErrorDTO(400, "Mora biti podan JWT.");
            throw GraphqlErrorException.newErrorException()
                    .message(authError.getError())
                    .extensions(Map.of(
                            "code", authError.getErrorCode(),
                            "error", authError.getError()
                    ))
                    .build();

        }

        PairDTO<IzdelekDTO, ErrorDTO> pair = izdelekService.pridobiIzdelek(id, tenant);
        IzdelekDTO izdelek = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            throw GraphqlErrorException.newErrorException()
                    .message(error.getError())
                    .extensions(Map.of(
                            "code", error.getErrorCode(),
                            "error", error.getError()
                    ))
                    .build();
        }

        return izdelek;
    }
}
