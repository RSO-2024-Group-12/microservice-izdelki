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

@ApplicationScoped
public class LastnostService {

    @Inject
    LastnostRepository lastnostRepository;

    private Logger log = Logger.getLogger(LastnostService.class.getName());

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
                lastnostRepository.persist(new Lastnost(izdelek.id, lastnostDTO.getLastnost(), lastnostDTO.getVrednost()));
            }
        }

        if (izdelekDTO.getLastnosti() != null) {
            for (LastnostDTO lastnostDTO : izdelekDTO.getLastnosti()) {
                Lastnost lastnost = lastnostRepository.findById(lastnostDTO.getId_lastnost());
                if (lastnost != null) {
                    lastnost.lastnost = lastnostDTO.getLastnost();
                    lastnost.vrednost = lastnostDTO.getVrednost();
                } else {
                    log.info("Lastnosti z id " + lastnostDTO.getId_lastnost() + "ni bilo mogoče najti!");
                }
            }
        }

        if (izdelekDTO.getLastnostiBrisi() != null) {
            for (LastnostDTO lastnostDTO : izdelekDTO.getLastnostiBrisi()) {
                Lastnost lastnost = lastnostRepository.findById(lastnostDTO.getId_lastnost());
                if (lastnost != null) {
                    lastnostRepository.deleteById(lastnostDTO.getId_lastnost());
                } else {
                    log.info("Lastnosti z id " + lastnostDTO.getId_lastnost() + "ni bilo mogoče najti!");
                }
            }
        }
    }
}
