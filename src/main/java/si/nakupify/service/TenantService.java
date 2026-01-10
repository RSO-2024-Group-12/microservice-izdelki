package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.logging.Logger;

@ApplicationScoped
public class TenantService {

    private Logger log = Logger.getLogger(TenantService.class.getName());

    public String getTenant() {
        /*
        if (identity.isAnonymous()) {
            log.info("Zahtevana avtorizacija");
            return null;
        }

        String tenant = identity.getAttribute("tenant");

        if (tenant == null || tenant.isBlank()) {
            log.info("JWT ne vsebuje claim tenant");
            return null;
        }
        */

        String tenant = "org1";

        return tenant;
    }
}
