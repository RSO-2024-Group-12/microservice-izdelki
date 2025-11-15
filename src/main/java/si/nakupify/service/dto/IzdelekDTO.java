package si.nakupify.service.dto;

import java.sql.Date;
import java.util.List;

public class IzdelekDTO {

    private Long id_izdelek;

    private String naziv;

    private String opis;

    private List<SlikaDTO> slike;

    private float cena;

    private Integer zaloga;

    private Boolean aktiven;

    private Date datum_dodajanja;

    private Date datum_spremembe;

    public IzdelekDTO() {}

    public IzdelekDTO(Long id_izdelek, String naziv, String opis, List<SlikaDTO> slike, float cena,
                      Integer zaloga, Boolean aktiven, Date datum_dodajanja, Date datum_spremembe) {
        this.id_izdelek = id_izdelek;
        this.naziv = naziv;
        this.opis = opis;
        this.slike = slike;
        this.cena = cena;
        this.zaloga = zaloga;
        this.aktiven = aktiven;
        this.datum_dodajanja = datum_dodajanja;
        this.datum_spremembe = datum_spremembe;
    }

    public Long getId_izdelek() {
        return id_izdelek;
    }

    public void setId_izdelek(Long id_izdelek) {
        this.id_izdelek = id_izdelek;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public List<SlikaDTO> getSlike() {
        return slike;
    }

    public void setSlike(List<SlikaDTO> slike) {
        this.slike = slike;
    }

    public float getCena() {
        return cena;
    }

    public void setCena(float cena) {
        this.cena = cena;
    }

    public Integer getZaloga() {
        return zaloga;
    }

    public void setZaloga(Integer zaloga) {
        this.zaloga = zaloga;
    }

    public Boolean getAktiven() {
        return aktiven;
    }

    public void setAktiven(Boolean aktiven) {
        this.aktiven = aktiven;
    }

    public Date getDatum_dodajanja() {
        return datum_dodajanja;
    }

    public void setDatum_dodajanja(Date datum_dodajanja) {
        this.datum_dodajanja = datum_dodajanja;
    }

    public Date getDatum_spremembe() {
        return datum_spremembe;
    }

    public void setDatum_spremembe(Date datum_spremembe) {
        this.datum_spremembe = datum_spremembe;
    }
}
