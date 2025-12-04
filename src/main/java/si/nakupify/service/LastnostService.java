package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.Izdelek;
import si.nakupify.entity.Lastnost;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.LastnostDTO;
import si.nakupify.service.repository.LastnostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class LastnostService {

    @Inject
    LastnostRepository lastnostRepository;

    private Logger log = Logger.getLogger(LastnostService.class.getName());

    public boolean validiraj(LastnostDTO lastnostDTO) {
        if (lastnostDTO.getLastnost() == null && lastnostDTO.getLastnost().isBlank() ||
                lastnostDTO.getVrednost() == null && lastnostDTO.getVrednost().isBlank()) {
            log.info("Podani napačni podatki za lastnost!");
            return false;
        }

        return true;
    }

    public List<LastnostDTO> pridobiLastnosti(Long id_izdelek) {
        List<Lastnost> lastnostList = lastnostRepository.izdelekLastnosti(id_izdelek);
        List<LastnostDTO> lastnostDTOList = new ArrayList<>();

        for (Lastnost lastnost : lastnostList) {
            LastnostDTO lastnostDTO = new LastnostDTO(lastnost.id, lastnost.lastnost, lastnost.vrednost);
            lastnostDTOList.add(lastnostDTO);
        }

        return lastnostDTOList;
    }

    @Transactional
    public void posodobiLastnosti(Izdelek izdelek, IzdelekDTO izdelekDTO) {
        if (izdelekDTO.getLastnostiDodaj() != null) {
            for (LastnostDTO lastnostDTO : izdelekDTO.getLastnostiDodaj()) {
                if (validiraj(lastnostDTO)) {
                    lastnostRepository.persist(new Lastnost(izdelek.id, lastnostDTO.getLastnost(), lastnostDTO.getVrednost()));
                }
            }
        }

        if (izdelekDTO.getLastnosti() != null) {
            for (LastnostDTO lastnostDTO : izdelekDTO.getLastnosti()) {
                if (validiraj(lastnostDTO)) {
                    Lastnost lastnost = lastnostRepository.findById(lastnostDTO.getId_lastnost());
                    if (lastnost != null) {
                        lastnost.lastnost = lastnostDTO.getLastnost();
                        lastnost.vrednost = lastnostDTO.getVrednost();
                    }
                }
            }
        }

        if (izdelekDTO.getLastnostiBrisi() != null) {
            for (LastnostDTO lastnostDTO : izdelekDTO.getLastnostiBrisi()) {
                lastnostRepository.deleteById(lastnostDTO.getId_lastnost());
            }
        }
    }
}
