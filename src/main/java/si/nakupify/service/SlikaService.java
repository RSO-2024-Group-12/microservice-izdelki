package si.nakupify.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import si.nakupify.entity.Izdelek;
import si.nakupify.entity.Slika;
import si.nakupify.service.dto.IzdelekDTO;
import si.nakupify.service.dto.SlikaDTO;
import si.nakupify.service.repository.SlikaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class SlikaService {

    @Inject
    SlikaRepository slikaRepository;

    private Logger log = Logger.getLogger(SlikaService.class.getName());

    public List<SlikaDTO> pridobiSlike(Long id_izdelek) {
        List<Slika> slikaList = slikaRepository.izdelekSlike(id_izdelek);
        List<SlikaDTO> slikaDTOList = new ArrayList<>();

        for (Slika slika : slikaList) {
            SlikaDTO slikaDTO = new SlikaDTO(slika.id, slika.url);
            slikaDTOList.add(slikaDTO);
        }

        return slikaDTOList;
    }

    @Transactional
    public void posodobiSlike(Izdelek izdelek, IzdelekDTO izdelekDTO) {
        if (izdelekDTO.getSlikeDodaj() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlikeDodaj()) {
                slikaRepository.persist(new Slika(izdelek.id, slikaDTO.getUrl()));
            }
        }

        if (izdelekDTO.getSlike() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlike()) {
                Slika slika = slikaRepository.findById(slikaDTO.getId_slika());
                if (slika != null) {
                    slika.url = slikaDTO.getUrl();
                } else {
                    log.info("Slike z id " + slikaDTO.getId_slika() + "ni bilo mogoče najti!");
                }
            }
        }

        if (izdelekDTO.getSlikeBrisi() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlikeBrisi()) {
                Slika slika = slikaRepository.findById(slikaDTO.getId_slika());
                if (slika != null) {
                    slikaRepository.deleteById(slikaDTO.getId_slika());
                } else {
                    log.info("Slike z id " + slikaDTO.getId_slika() + "ni bilo mogoče najti!");
                }
            }
        }
    }
}
