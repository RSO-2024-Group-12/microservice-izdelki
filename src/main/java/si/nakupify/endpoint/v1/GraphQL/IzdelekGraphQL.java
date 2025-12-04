package si.nakupify.endpoint.v1.GraphQL;

import jakarta.inject.Inject;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.dto.IzdelekDTO;

import java.util.List;

@GraphQLApi
public class IzdelekGraphQL {

    @Inject
    IzdelekService izdelekService;

    @Query("allIzdelki")
    @Description("Vrne seznam vseh izdelkov.")
    public List<IzdelekDTO> getIzdelki() {
        return izdelekService.pridobiVseIzdelke();
    }

    @Query("allAktivniIzdelki")
    @Description("Vrne seznam vseh aktivnih izdelkov.")
    public List<IzdelekDTO> getAktivniIzdelki() {
        return izdelekService.pridobiVseAktivneIzdelke();
    }

    @Query("getIzdelek")
    @Description("Vrne izdelek s podanim id.")
    public IzdelekDTO getIzdelek(Long id) {
        return izdelekService.pridobiIzdelek(id);
    }
}
