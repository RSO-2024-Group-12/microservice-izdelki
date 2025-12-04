package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Lastnost extends PanacheEntity {

    public Long id_izdelek;

    public String lastnost;

    public String vrednost;

    public Lastnost() {}

    public Lastnost(Long id_izdelek, String lastnost, String vrednost) {
        this.id_izdelek = id_izdelek;
        this.lastnost = lastnost;
        this.vrednost = vrednost;
    }
}
