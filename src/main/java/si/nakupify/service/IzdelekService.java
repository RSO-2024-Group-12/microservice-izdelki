package si.nakupify.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.Izdelek;
import si.nakupify.entity.Slika;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.SlikaDTO;

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
    SlikaRepository slikaRepository;

    private Logger log = Logger.getLogger(IzdelekService.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija mikrostoritve izdelki.");
        System.out.println("Inicializacija mikrostoritve izdelki.");
    }

    @PreDestroy
    private void destroy() {
        log.info("Ustavitev mikrostoritve izdelki.");
        System.out.println("Ustavitev mikrostoritve izdelki.");
    }

    public List<IzdelekDTO> pridobiVseIzdelke() {
        return pridobiSeznamIzdelkov(izdelekRepository.listAll());
    }

    public List<IzdelekDTO> pridobiAktivneIzdelke() {
        return pridobiSeznamIzdelkov(izdelekRepository.aktivniIzdeleki());
    }

    public List<IzdelekDTO> pridobiSeznamIzdelkov(List<Izdelek> izdelekList) {
        List<IzdelekDTO> izdelekDTOList = new ArrayList<>();

        for (Izdelek izdelek : izdelekList) {
            IzdelekDTO izdelekDTO = pridobiIzdelek(izdelek.id);
            izdelekDTOList.add(izdelekDTO);
        }

        return izdelekDTOList;
    }

    public IzdelekDTO pridobiIzdelek(Long id_izdelek) {
        Izdelek izdelek = izdelekRepository.findById(Long.valueOf(id_izdelek));
        if (izdelek == null) {
            log.info("Iskanje izdelka z id=" + id_izdelek + " ni bilo mogoče najti, saj ne obstaja!");
            return null;
        }

        IzdelekDTO izdelekDTO = new IzdelekDTO();
        izdelekDTO.setId_izdelek(izdelek.id);
        izdelekDTO.setNaziv(izdelek.naziv);
        izdelekDTO.setOpis(izdelek.opis);
        izdelekDTO.setCena(izdelek.cena);
        izdelekDTO.setZaloga(izdelek.zaloga);
        izdelekDTO.setAktiven(izdelek.aktiven);
        izdelekDTO.setDatum_dodajanja(izdelek.datum_dodajanja);
        izdelekDTO.setDatum_spremembe(izdelek.datum_spremembe);

        List<Slika> slikaList = slikaRepository.izdelekSlike(izdelek.id);
        List<SlikaDTO> slikaDTOList = new ArrayList<>();
        for (Slika slika : slikaList) {
            SlikaDTO slikaDTO = new SlikaDTO();
            slikaDTO.setId_slika(slika.id);
            slikaDTO.setUrl(slika.url);

            slikaDTOList.add(slikaDTO);
        }
        izdelekDTO.setSlike(slikaDTOList);

        return izdelekDTO;
    }

    public boolean validirajIzdelek(IzdelekDTO izdelekDTO) {
        if (izdelekDTO.getNaziv() == null && izdelekDTO.getNaziv().isBlank() ||
                izdelekDTO.getOpis() == null && izdelekDTO.getOpis().isBlank() ||
                izdelekDTO.getCena() <= 0) {
            log.info("Podani manjkajoči podatki!");
            return false;
        }

        return true;
    }

    @Transactional
    public IzdelekDTO ustvariIzdelek(IzdelekDTO izdelekDTO) {
        if (!validirajIzdelek(izdelekDTO)) {
            return null;
        }

        Izdelek izdelek = new Izdelek(izdelekDTO.getNaziv(), izdelekDTO.getOpis(), izdelekDTO.getCena());
        izdelekRepository.persist(izdelek);

        for (SlikaDTO slikaDTO : izdelekDTO.getSlike()) {
            Slika slika = new Slika(izdelek.id, slikaDTO.getUrl());
            slikaRepository.persist(slika);
        }

        return pridobiIzdelek(izdelek.id);
    }

    @Transactional
    public IzdelekDTO posodobiIzdelek(IzdelekDTO izdelekDTO) {
        if (!validirajIzdelek(izdelekDTO)) {
            IzdelekDTO response = new IzdelekDTO();
            response.setId_izdelek((long) -1);
            return response;
        }

        Izdelek izdelek = izdelekRepository.findById(Long.valueOf(izdelekDTO.getId_izdelek()));
        if (izdelek == null) {
            log.info("Izdelka z id " + izdelekDTO.getId_izdelek() + "ni bilo mogoče najti!");
            IzdelekDTO response = new IzdelekDTO();
            response.setId_izdelek((long) -2);
            return response;
        }

        izdelek.naziv = izdelekDTO.getNaziv();
        izdelek.opis = izdelekDTO.getOpis();
        izdelek.cena = izdelekDTO.getCena();
        izdelek.datum_spremembe = Date.valueOf(LocalDate.now());

        List<SlikaDTO> slikaDTOList = izdelekDTO.getSlike();
        List<SlikaDTO> removeList = new ArrayList<>();
        for (SlikaDTO slikaDTO : slikaDTOList) {
            if (slikaDTO.getId_slika() == -1) {
                Slika slika = new Slika(izdelek.id, slikaDTO.getUrl());
                slikaRepository.persist(slika);
                removeList.add(slikaDTO);
            }
        }
        slikaDTOList.removeAll(removeList);

        List<Slika> slikaList = slikaRepository.listAll();
        for (Slika slika : slikaList) {
            boolean remove = true;

            for (SlikaDTO slikaDTO : slikaDTOList) {
                if (slika.id == slikaDTO.getId_slika()) {
                    remove = false;
                    break;
                }
            }

            if (remove) {
                slikaRepository.deleteById(slika.id);
            }
        }

        return pridobiIzdelek(izdelek.id);
    }

    @Transactional
    public IzdelekDTO posodobiZalogaIzdelek(IzdelekDTO izdelekDTO) {
        Izdelek izdelek = izdelekRepository.findById(Long.valueOf(izdelekDTO.getId_izdelek()));
        if (izdelek == null) {
            log.info("Izdelka z id " + izdelekDTO.getId_izdelek() + "ni bilo mogoče najti!");
            return null;
        }

        Integer novaZaloga = izdelek.zaloga + izdelekDTO.getZaloga();
        if (novaZaloga < 0) {
            log.info("Izdelka ne more imeti negativne zaloge!");
            return null;
        }

        izdelek.zaloga = novaZaloga;

        return pridobiIzdelek(izdelek.id);
    }

    @Transactional
    public IzdelekDTO izbrisiIzdelek(Long id_izdelek) {
        Izdelek izdelek = izdelekRepository.findById(id_izdelek);
        if (izdelek == null) {
            log.info("Izdelka z id " + id_izdelek + "ni bilo mogoče najti!");
            return null;
        }

        izdelek.aktiven = false;

        return pridobiIzdelek(izdelek.id);
    }
}
