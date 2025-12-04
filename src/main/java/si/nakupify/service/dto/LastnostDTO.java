package si.nakupify.service.dto;

public class LastnostDTO {

    private Long id_lastnost;

    private String lastnost;

    private String vrednost;

    public LastnostDTO() {}

    public LastnostDTO(Long id_lastnost, String lastnost, String vrednost) {
        this.id_lastnost = id_lastnost;
        this.lastnost = lastnost;
        this.vrednost = vrednost;
    }

    public Long getId_lastnost() {
        return id_lastnost;
    }

    public void setId_lastnost(Long id_lastnost) {
        this.id_lastnost = id_lastnost;
    }

    public String getLastnost() {
        return lastnost;
    }

    public void setLastnost(String lastnost) {
        this.lastnost = lastnost;
    }

    public String getVrednost() {
        return vrednost;
    }

    public void setVrednost(String vrednost) {
        this.vrednost = vrednost;
    }
}
