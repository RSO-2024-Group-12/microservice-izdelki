package si.nakupify.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.Slika;

import java.util.List;

@ApplicationScoped
public class SlikaRepository implements PanacheRepository<Slika> {

    public List<Slika> izdelekSlike(Long id_izdelek) {
        return list("id_izdelek", id_izdelek);
    }
}
