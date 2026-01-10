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

    @Inject
    TenantService tenantService;

    private Logger log = Logger.getLogger(IzdelekService.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija microservice-izdelki");
    }

    @PreDestroy
    private void destroy() {
        log.info("Ustavitev microservice-izdelki");
    }

    public PairDTO<List<IzdelekDTO>, ErrorDTO> pridobiVseIzdelke(String tenant) {
        return pridobiSeznamIzdelkov(izdelekRepository.izdelekiPoTenant(tenant), tenant);
    }

    public PairDTO<List<IzdelekDTO>, ErrorDTO> pridobiVseAktivneIzdelke(String tenant) {
        return pridobiSeznamIzdelkov(izdelekRepository.aktivniIzdeleki(tenant), tenant);
    }

    public PairDTO<List<IzdelekDTO>, ErrorDTO> pridobiSeznamIzdelkov(List<Izdelek> izdelekList, String tenant) {
        List<IzdelekDTO> izdelekDTOList = new ArrayList<>();

        for (Izdelek izdelek : izdelekList) {
            PairDTO<IzdelekDTO, ErrorDTO> pair = pridobiIzdelek(izdelek.id, tenant);
            IzdelekDTO izdelekDTO = pair.getValue();
            ErrorDTO error = pair.getError();

            if (error != null) {
                return new PairDTO<>(null, error);
            }

            izdelekDTOList.add(izdelekDTO);
        }

        return new PairDTO<>(izdelekDTOList, null);
    }

    public PairDTO<IzdelekDTO, ErrorDTO> pridobiIzdelek(Long id_izdelek, String tenant) {
        Izdelek izdelek = izdelekRepository.findById(id_izdelek);
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + id_izdelek + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        if (!tenant.equals(izdelek.tenant)) {
            log.info("Auth Error: Ne smete brati ali spreminjati podatkov druge organizacije");
            ErrorDTO notFoundError = new ErrorDTO(401, "Ni mogoče brati ali spreminjati podatkov druge organizacije.");
            return new PairDTO<>(null, notFoundError);
        }

        IzdelekDTO izdelekDTO = new IzdelekDTO();
        izdelekDTO.setId_izdelek(izdelek.id);
        izdelekDTO.setNaziv(izdelek.naziv);
        izdelekDTO.setOpis(izdelek.opis);
        izdelekDTO.setCena(izdelek.cena);
        izdelekDTO.setTenant(izdelek.tenant);
        izdelekDTO.setAktiven(izdelek.aktiven);
        izdelekDTO.setDatum_dodajanja(izdelek.datum_dodajanja);
        izdelekDTO.setDatum_spremembe(izdelek.datum_spremembe);

        PairDTO<ZalogaDTO, ErrorDTO> pair = skladisceClient.getZalogaDTO(id_izdelek);
        ZalogaDTO zalogaDTO = pair.getValue();
        ErrorDTO error = pair.getError();

        if (error != null) {
            return new PairDTO<>(null, error);
        }

        izdelekDTO.setZaloga(zalogaDTO.getStock());

        izdelekDTO.setSlike(slikaService.pridobiSlike(izdelek.id));
        izdelekDTO.setLastnosti(lastnostService.pridobiLastnosti(izdelek.id));

        return new PairDTO<>(izdelekDTO, null);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> dodajIzdelek(IzdelekDTO izdelekDTO, String tenant) {
        Izdelek izdelek = new Izdelek(izdelekDTO.getNaziv(), izdelekDTO.getOpis(), izdelekDTO.getCena(), tenant);
        izdelekRepository.persist(izdelek);

        ErrorDTO error = skladisceClient.postZalogaDTO(izdelek.id, tenant);

        if (error != null) {
            return new PairDTO<>(null, error);
        }

        slikaService.posodobiSlike(izdelek, izdelekDTO);
        lastnostService.posodobiLastnosti(izdelek, izdelekDTO);

        return pridobiIzdelek(izdelek.id, tenant);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> posodobiIzdelek(IzdelekDTO izdelekDTO, String tenant) {
        Izdelek izdelek = izdelekRepository.findById(izdelekDTO.getId_izdelek());
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + izdelekDTO.getId_izdelek() + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        if (!tenant.equals(izdelek.tenant)) {
            log.info("Auth Error: Ne smete brati ali spreminjati podatkov druge organizacije");
            ErrorDTO notFoundError = new ErrorDTO(401, "Ni mogoče brati ali spreminjati podatkov druge organizacije.");
            return new PairDTO<>(null, notFoundError);
        }

        izdelek.naziv = izdelekDTO.getNaziv();
        izdelek.opis = izdelekDTO.getOpis();
        izdelek.cena = izdelekDTO.getCena();
        izdelek.datum_spremembe = Date.valueOf(LocalDate.now());

        slikaService.posodobiSlike(izdelek, izdelekDTO);
        lastnostService.posodobiLastnosti(izdelek, izdelekDTO);

        return pridobiIzdelek(izdelek.id, tenant);
    }

    @Transactional
    public PairDTO<IzdelekDTO, ErrorDTO> izbrisiIzdelek(Long id_izdelek, String tenant) {
        Izdelek izdelek = izdelekRepository.findById(id_izdelek);
        if (izdelek == null) {
            log.info("Not Found Error: Izdelka z id=" + id_izdelek + " ni bilo mogoče najti");
            ErrorDTO notFoundError = new ErrorDTO(404, "Izdelka s podanim id_izdelek ni bilo mogoče najti!");
            return new PairDTO<>(null, notFoundError);
        }

        if (!tenant.equals(izdelek.tenant)) {
            log.info("Auth Error: Ne smete brati ali spreminjati podatkov druge organizacije");
            ErrorDTO notFoundError = new ErrorDTO(401, "Ni mogoče brati ali spreminjati podatkov druge organizacije.");
            return new PairDTO<>(null, notFoundError);
        }

        izdelek.aktiven = false;

        return pridobiIzdelek(izdelek.id, tenant);
    }
}
