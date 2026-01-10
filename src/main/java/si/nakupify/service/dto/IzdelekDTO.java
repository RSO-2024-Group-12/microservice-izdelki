package si.nakupify.service.dto;

import java.sql.Date;
import java.util.List;

public class IzdelekDTO {

    private Long id_izdelek;

    private String naziv;

    private String opis;

    private Float cena;

    private String tenant;

    private Boolean aktiven;

    private Date datum_dodajanja;

    private Date datum_spremembe;

    private Integer zaloga;

    private List<SlikaDTO> slike;

    private List<SlikaDTO> slikeDodaj;

    private List<SlikaDTO> slikeBrisi;

    private List<LastnostDTO> lastnosti;

    private List<LastnostDTO> lastnostiDodaj;

    private List<LastnostDTO> lastnostiBrisi;

    public IzdelekDTO() {}

    public IzdelekDTO(Long id_izdelek, String naziv, String opis, Float cena, String tenant, Boolean aktiven,
                      Date datum_dodajanja, Date datum_spremembe, Integer zaloga,
                      List<SlikaDTO> slike, List<SlikaDTO> slikeDodaj, List<SlikaDTO> slikeBrisi,
                      List<LastnostDTO> lastnosti, List<LastnostDTO> lastnostiDodaj, List<LastnostDTO> lastnostiBrisi) {
        this.id_izdelek = id_izdelek;
        this.naziv = naziv;
        this.opis = opis;
        this.cena = cena;
        this.tenant = tenant;
        this.aktiven = aktiven;
        this.datum_dodajanja = datum_dodajanja;
        this.datum_spremembe = datum_spremembe;
        this.zaloga = zaloga;
        this.slike = slike;
        this.slikeDodaj = slikeDodaj;
        this.slikeBrisi = slikeBrisi;
        this.lastnosti = lastnosti;
        this.lastnostiDodaj = lastnostiDodaj;
        this.lastnostiBrisi = lastnostiBrisi;
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

    public Float getCena() {
        return cena;
    }

    public void setCena(Float cena) {
        this.cena = cena;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
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

    public Integer getZaloga() {
        return zaloga;
    }

    public void setZaloga(Integer zaloga) {
        this.zaloga = zaloga;
    }

    public List<SlikaDTO> getSlike() {
        return slike;
    }

    public void setSlike(List<SlikaDTO> slike) {
        this.slike = slike;
    }

    public List<SlikaDTO> getSlikeDodaj() {
        return slikeDodaj;
    }

    public void setSlikeDodaj(List<SlikaDTO> slikeDodaj) {
        this.slikeDodaj = slikeDodaj;
    }

    public List<SlikaDTO> getSlikeBrisi() {
        return slikeBrisi;
    }

    public void setSlikeBrisi(List<SlikaDTO> slikeBrisi) {
        this.slikeBrisi = slikeBrisi;
    }

    public List<LastnostDTO> getLastnosti() {
        return lastnosti;
    }

    public void setLastnosti(List<LastnostDTO> lastnosti) {
        this.lastnosti = lastnosti;
    }

    public List<LastnostDTO> getLastnostiDodaj() {
        return lastnostiDodaj;
    }

    public void setLastnostiDodaj(List<LastnostDTO> lastnostiDodaj) {
        this.lastnostiDodaj = lastnostiDodaj;
    }

    public List<LastnostDTO> getLastnostiBrisi() {
        return lastnostiBrisi;
    }

    public void setLastnostiBrisi(List<LastnostDTO> lastnostiBrisi) {
        this.lastnostiBrisi = lastnostiBrisi;
    }
}
