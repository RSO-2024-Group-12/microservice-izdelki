package si.nakupify.service.dto;

public class SlikaDTO {

    private Long id_slika;

    private String url;

    public SlikaDTO() {}

    public SlikaDTO(Long id_slika, String url) {
        this.id_slika = id_slika;
        this.url = url;
    }

    public Long getId_slika() {
        return id_slika;
    }

    public void setId_slika(Long id_slika) {
        this.id_slika = id_slika;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
