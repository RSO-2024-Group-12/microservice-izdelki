package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Slika extends PanacheEntity {

    public Long id_izdelek;

    public String url;

    public Slika() {}

    public Slika(Long id_izdelek, String url) {
        this.id_izdelek = id_izdelek;
        this.url = url;
    }
}
