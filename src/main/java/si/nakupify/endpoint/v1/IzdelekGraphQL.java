package si.nakupify.endpoint.v1;

import graphql.*;
import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import si.nakupify.service.IzdelekService;
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

    private Logger log = Logger.getLogger(IzdelekGraphQL.class.getName());

    @Query("allIzdelki")
    @Description("Vrne seznam vseh izdelkov.")
    public List<IzdelekDTO> getIzdelki() {
        PairDTO<List<IzdelekDTO>, ErrorDTO> pair = izdelekService.pridobiVseIzdelke();
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
        PairDTO<List<IzdelekDTO>, ErrorDTO> pair = izdelekService.pridobiVseAktivneIzdelke();
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
        PairDTO<IzdelekDTO, ErrorDTO> pair = izdelekService.pridobiIzdelek(id);
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
