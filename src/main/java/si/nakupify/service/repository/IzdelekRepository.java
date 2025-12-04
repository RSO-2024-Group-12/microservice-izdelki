package si.nakupify.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.Izdelek;

import java.util.List;

@ApplicationScoped
public class IzdelekRepository implements PanacheRepository<Izdelek> {

    public List<Izdelek> aktivniIzdeleki() {
        return list("aktiven = true");
    }
}
