package si.nakupify.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.Izdelek;
import si.nakupify.service.client.SkladisceClient;
import si.nakupify.service.dto.ErrorDTO;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.PairDTO;
import si.nakupify.service.dto.ZalogaDTO;
import si.nakupify.service.repository.IzdelekRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class IzdelekService {

    @Inject
    IzdelekRepository izdelekRepository;

    @Inject
    SlikaService slikaService;

    @Inject
    LastnostService lastnostService;

    @Inject
    SkladisceClient skladisceClient;

    private Logger log = Logger.getLogger(IzdelekService.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija microservice-izdelki");
    }

    @PreDestroy
    private void destroy() {
        log.info("Ustavitev microservice-izdelki");
    }

    public List<IzdelekDTO> pridobiVseIzdelke() {
        return pridobiSeznamIzdelkov(izdelekRepository.listAll());
    }

    public List<IzdelekDTO> pridobiVseAktivneIzdelke() {
        return pridobiSeznamIzdelkov(izdelekRepository.aktivniIzdeleki());
    }

    public List<IzdelekDTO> pridobiSeznamIzdelkov(List<Izdelek> izdelekList) {
        List<IzdelekDTO> izdelekDTOList = new ArrayList<>();

        for (Izdelek izdelek : izdelekList) {
            PairDTO<IzdelekDTO, ErrorDTO> pair = pridobiIzdelek(izdelek.id);
            izdelekDTOList.add(pair.getValue());
        }

        return izdelekDTOList;
    }

    public PairDTO<IzdelekDTO, ErrorDTO> pridobiIzdelek(Long id_izdelek) {
        Izdelek izdelek = izdelekRepository.findById(id_izdelek);
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + id_izdelek + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        IzdelekDTO izdelekDTO = new IzdelekDTO();
        izdelekDTO.setId_izdelek(izdelek.id);
        izdelekDTO.setNaziv(izdelek.naziv);
        izdelekDTO.setOpis(izdelek.opis);
        izdelekDTO.setCena(izdelek.cena);
        izdelekDTO.setAktiven(izdelek.aktiven);
        izdelekDTO.setDatum_dodajanja(izdelek.datum_dodajanja);
        izdelekDTO.setDatum_spremembe(izdelek.datum_spremembe);

        ZalogaDTO zalogaDTO = skladisceClient.getZalogaDTO(id_izdelek);
        if (zalogaDTO == null) {
            izdelekDTO.setZaloga(0);
        } else {
            izdelekDTO.setZaloga(zalogaDTO.getStock());
        }

        izdelekDTO.setSlike(slikaService.pridobiSlike(izdelek.id));
        izdelekDTO.setLastnosti(lastnostService.pridobiLastnosti(izdelek.id));

        return new PairDTO<>(izdelekDTO, null);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> dodajIzdelek(IzdelekDTO izdelekDTO) {
        Izdelek izdelek = new Izdelek(izdelekDTO.getNaziv(), izdelekDTO.getOpis(), izdelekDTO.getCena());
        izdelekRepository.persist(izdelek);

        skladisceClient.postZalogaDTO(izdelek.id);

        slikaService.posodobiSlike(izdelek, izdelekDTO);
        lastnostService.posodobiLastnosti(izdelek, izdelekDTO);

        return pridobiIzdelek(izdelek.id);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> posodobiIzdelek(IzdelekDTO izdelekDTO) {
        Izdelek izdelek = izdelekRepository.findById(izdelekDTO.getId_izdelek());
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + izdelekDTO.getId_izdelek() + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        izdelek.naziv = izdelekDTO.getNaziv();
        izdelek.opis = izdelekDTO.getOpis();
        izdelek.cena = izdelekDTO.getCena();
        izdelek.datum_spremembe = Date.valueOf(LocalDate.now());

        slikaService.posodobiSlike(izdelek, izdelekDTO);
        lastnostService.posodobiLastnosti(izdelek, izdelekDTO);

        return pridobiIzdelek(izdelek.id);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> izbrisiIzdelek(Long id_izdelek) {
        Izdelek izdelek = izdelekRepository.findById(id_izdelek);
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + id_izdelek + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        izdelek.aktiven = false;

        return pridobiIzdelek(izdelek.id);
    }
}
