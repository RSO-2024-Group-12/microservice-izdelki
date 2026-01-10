package si.nakupify.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import si.nakupify.entity.Izdelek;

import java.util.List;

@ApplicationScoped
public class IzdelekRepository implements PanacheRepository<Izdelek> {

    public List<Izdelek> izdelekiPoTenant(String tenant) {
        return list("tenant", tenant);
    }

    public List<Izdelek> aktivniIzdeleki(String tenant) {
        return list("aktiven = true and tenant = ?1", tenant);
    }
}
