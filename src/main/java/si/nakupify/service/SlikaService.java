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

    public boolean validirajSliko(SlikaDTO slikaDTO) {
        if (slikaDTO.getUrl() == null && slikaDTO.getUrl().isBlank()) {
            log.info("Podani mankajoči podatki za sliko!");
            return false;
        }

        return true;
    }

    @Transactional
    public void posodobiSlike(Izdelek izdelek, IzdelekDTO izdelekDTO) {
        if (izdelekDTO.getSlikeDodaj() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlikeDodaj()) {
                if (validirajSliko(slikaDTO)) {
                    slikaRepository.persist(new Slika(izdelek.id, slikaDTO.getUrl()));
                }
            }
        }

        if (izdelekDTO.getSlike() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlike()) {
                if (validirajSliko(slikaDTO)) {
                    Slika slika = slikaRepository.findById(Long.valueOf(slikaDTO.getId_slika()));
                    if (slika == null) {
                        slika.url = slikaDTO.getUrl();
                    }
                }
            }
        }

        if (izdelekDTO.getSlikeBrisi() != null) {
            for (SlikaDTO slikaDTO : izdelekDTO.getSlikeBrisi()) {
                slikaRepository.deleteById(Long.valueOf(slikaDTO.getId_slika()));
            }
        }
    }
}
