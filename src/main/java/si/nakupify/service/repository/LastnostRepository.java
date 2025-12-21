package si.nakupify.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.Lastnost;

import java.util.List;

@ApplicationScoped
public class LastnostRepository implements PanacheRepository<Lastnost> {

    public List<Lastnost> izdelekLastnosti(Long id_izdelek) {
        return list("id_izdelek", id_izdelek);
    }
}
