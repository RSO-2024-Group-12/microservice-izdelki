package si.nakupify.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.sql.Date;
import java.time.LocalDate;

@Entity
public class Izdelek extends PanacheEntity {

    public String naziv;

    public String opis;

    public Float cena;

    public Boolean aktiven;

    public Date datum_dodajanja;

    public Date datum_spremembe;

    public Izdelek() {}

    public Izdelek(String naziv, String opis, Float cena) {
        this.naziv = naziv;
        this.opis = opis;
        this.cena = cena;
        this.aktiven = true;
        this.datum_dodajanja = Date.valueOf(LocalDate.now());
        this.datum_spremembe = Date.valueOf(LocalDate.now());
    }
}
