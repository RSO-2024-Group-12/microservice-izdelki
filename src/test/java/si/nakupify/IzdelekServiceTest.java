package si.nakupify;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;
import si.nakupify.entity.Izdelek;
import si.nakupify.service.IzdelekService;
import si.nakupify.service.LastnostService;
import si.nakupify.service.SlikaService;
import si.nakupify.service.client.SkladisceClient;
import si.nakupify.service.dto.*;
import si.nakupify.service.repository.IzdelekRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class IzdelekServiceTest {

    @InjectSpy
    IzdelekService izdelekService;

    @InjectMock
    IzdelekRepository izdelekRepository;

    @InjectMock
    SlikaService slikaService;

    @InjectMock
    LastnostService lastnostService;

    @InjectMock
    SkladisceClient skladisceClient;

    private Izdelek izdelekEntity(Long id) {
        Izdelek izdelek = new Izdelek();
        izdelek.id = id;
        izdelek.naziv = "Test";
        izdelek.opis = "Test";
        izdelek.cena = 9.99F;
        izdelek.aktiven = true;
        izdelek.datum_dodajanja = Date.valueOf(LocalDate.now());
        izdelek.datum_spremembe = Date.valueOf(LocalDate.now());
        return izdelek;
    }

    private IzdelekDTO makeIzdelekDTO(Long id, int mode) {
        IzdelekDTO izdelekDTO = new IzdelekDTO();

        if (mode == 0) {
            List<SlikaDTO> slike = List.of(new SlikaDTO(null, "example.org"));
            izdelekDTO.setSlikeDodaj(slike);

            List<LastnostDTO> lastnost = List.of(new LastnostDTO(null, "Test", "Test"));
            izdelekDTO.setLastnostiDodaj(lastnost);
        }

        if (mode == 1) {
            izdelekDTO.setId_izdelek(id);

            List<SlikaDTO> slike = List.of(new SlikaDTO(1L, "example.com"));
            izdelekDTO.setSlike(slike);

            List<LastnostDTO> lastnosti = List.of(new LastnostDTO(1L, "Test1", "Test1"));
            izdelekDTO.setLastnosti(lastnosti);
        }

        izdelekDTO.setNaziv("Test");
        izdelekDTO.setOpis("Test");
        izdelekDTO.setCena(9.99F);

        return izdelekDTO;
    }

    @Test
    void pridobiIzdelek_test() {
        when(izdelekRepository.findById(5L)).thenReturn(izdelekEntity(5L));

        ZalogaDTO zalogaDTO = new ZalogaDTO(5L, 100, 0);
        when(skladisceClient.getZalogaDTO(5L)).thenReturn(new PairDTO<>(zalogaDTO, null));

        List<SlikaDTO> slike = List.of(new SlikaDTO(1L, "example.org"));
        when(slikaService.pridobiSlike(5L)).thenReturn(slike);

        List<LastnostDTO> lastnosti = List.of(new LastnostDTO(1L, "Test", "Test"));
        when(lastnostService.pridobiLastnosti(5L)).thenReturn(lastnosti);

        PairDTO<IzdelekDTO, ErrorDTO> result = izdelekService.pridobiIzdelek(5L);

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertNull(result.getError());

        IzdelekDTO izdelekDTO = result.getValue();

        assertEquals(5L, izdelekDTO.getId_izdelek());
        assertEquals("Test", izdelekDTO.getNaziv());
        assertEquals("Test", izdelekDTO.getOpis());
        assertEquals(9.99F, izdelekDTO.getCena());
        assertEquals(true, izdelekDTO.getAktiven());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_dodajanja());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_spremembe());
        assertEquals(100, izdelekDTO.getZaloga());
        assertEquals(1, izdelekDTO.getSlike().size());
        assertEquals(1, izdelekDTO.getLastnosti().size());

        verify(izdelekRepository).findById(5L);
        verify(skladisceClient).getZalogaDTO(5L);
        verify(slikaService).pridobiSlike(5L);
        verify(lastnostService).pridobiLastnosti(5L);
    }

    @Test
    void dodajIzdelek_test() {
        doAnswer(invocation -> {
            Izdelek izdelek = invocation.getArgument(0);
            izdelek.id = 5L;
            return null;
        }).when(izdelekRepository).persist(any(Izdelek.class));

        when(skladisceClient.postZalogaDTO(5L)).thenReturn(null);

        doNothing().when(slikaService).posodobiSlike(any(Izdelek.class), any(IzdelekDTO.class));
        doNothing().when(lastnostService).posodobiLastnosti(any(Izdelek.class), any(IzdelekDTO.class));

        IzdelekDTO mockDTO = new IzdelekDTO();
        mockDTO.setId_izdelek(5L);
        mockDTO.setNaziv("Test");
        mockDTO.setOpis("Test");
        mockDTO.setCena(9.99F);
        mockDTO.setAktiven(true);
        mockDTO.setDatum_dodajanja(Date.valueOf(LocalDate.now()));
        mockDTO.setDatum_spremembe(Date.valueOf(LocalDate.now()));
        mockDTO.setZaloga(100);
        mockDTO.setSlike(List.of(new SlikaDTO(1L, "example.org")));
        mockDTO.setLastnosti(List.of(new LastnostDTO(1L, "Test", "Test")));

        doReturn(new PairDTO<>(mockDTO, null)).when(izdelekService).pridobiIzdelek(anyLong());

        PairDTO<IzdelekDTO, ErrorDTO> result = izdelekService.dodajIzdelek(makeIzdelekDTO(0L, 0));

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertNull(result.getError());

        IzdelekDTO izdelekDTO = result.getValue();

        assertEquals(5L, izdelekDTO.getId_izdelek());
        assertEquals("Test", izdelekDTO.getNaziv());
        assertEquals("Test", izdelekDTO.getOpis());
        assertEquals(9.99F, izdelekDTO.getCena());
        assertEquals(true, izdelekDTO.getAktiven());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_dodajanja());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_spremembe());
        assertEquals(100, izdelekDTO.getZaloga());
        assertEquals(1, izdelekDTO.getSlike().size());
        assertEquals(1, izdelekDTO.getLastnosti().size());

        verify(izdelekRepository).persist(any(Izdelek.class));
        verify(skladisceClient).postZalogaDTO(5L);
        verify(slikaService).posodobiSlike(any(Izdelek.class), any(IzdelekDTO.class));
        verify(lastnostService).posodobiLastnosti(any(Izdelek.class), any(IzdelekDTO.class));
        verify(izdelekService).pridobiIzdelek(5L);
    }

    @Test
    void posodobiIzdelek_test() {
        when(izdelekRepository.findById(5L)).thenReturn(izdelekEntity(5L));

        doNothing().when(slikaService).posodobiSlike(any(Izdelek.class), any(IzdelekDTO.class));
        doNothing().when(lastnostService).posodobiLastnosti(any(Izdelek.class), any(IzdelekDTO.class));

        IzdelekDTO mockDTO = new IzdelekDTO();
        mockDTO.setId_izdelek(5L);
        mockDTO.setNaziv("Test");
        mockDTO.setOpis("Test");
        mockDTO.setCena(9.99F);
        mockDTO.setAktiven(true);
        mockDTO.setDatum_dodajanja(Date.valueOf(LocalDate.now()));
        mockDTO.setDatum_spremembe(Date.valueOf(LocalDate.now()));
        mockDTO.setZaloga(100);
        mockDTO.setSlike(List.of(new SlikaDTO(1L, "example.org")));
        mockDTO.setLastnosti(List.of(new LastnostDTO(1L, "Test", "Test")));

        doReturn(new PairDTO<>(mockDTO, null)).when(izdelekService).pridobiIzdelek(anyLong());

        PairDTO<IzdelekDTO, ErrorDTO> result = izdelekService.posodobiIzdelek(makeIzdelekDTO(5L, 1));

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertNull(result.getError());

        IzdelekDTO izdelekDTO = result.getValue();

        assertEquals(5L, izdelekDTO.getId_izdelek());
        assertEquals("Test", izdelekDTO.getNaziv());
        assertEquals("Test", izdelekDTO.getOpis());
        assertEquals(9.99F, izdelekDTO.getCena());
        assertEquals(true, izdelekDTO.getAktiven());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_dodajanja());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_spremembe());
        assertEquals(100, izdelekDTO.getZaloga());
        assertEquals(1, izdelekDTO.getSlike().size());
        assertEquals(1, izdelekDTO.getLastnosti().size());

        verify(izdelekRepository).findById(5L);
        verify(slikaService).posodobiSlike(any(Izdelek.class), any(IzdelekDTO.class));
        verify(lastnostService).posodobiLastnosti(any(Izdelek.class), any(IzdelekDTO.class));
        verify(izdelekService).pridobiIzdelek(5L);
    }

    @Test
    void izbrisiIzdelek_test() {
        when(izdelekRepository.findById(5L)).thenReturn(izdelekEntity(5L));

        IzdelekDTO mockDTO = new IzdelekDTO();
        mockDTO.setId_izdelek(5L);
        mockDTO.setNaziv("Test");
        mockDTO.setOpis("Test");
        mockDTO.setCena(9.99F);
        mockDTO.setAktiven(false);
        mockDTO.setDatum_dodajanja(Date.valueOf(LocalDate.now()));
        mockDTO.setDatum_spremembe(Date.valueOf(LocalDate.now()));
        mockDTO.setZaloga(100);
        mockDTO.setSlike(List.of(new SlikaDTO(1L, "example.org")));
        mockDTO.setLastnosti(List.of(new LastnostDTO(1L, "Test", "Test")));

        doReturn(new PairDTO<>(mockDTO, null)).when(izdelekService).pridobiIzdelek(anyLong());

        PairDTO<IzdelekDTO, ErrorDTO> result = izdelekService.izbrisiIzdelek(5L);

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertNull(result.getError());

        IzdelekDTO izdelekDTO = result.getValue();

        assertEquals(5L, izdelekDTO.getId_izdelek());
        assertEquals("Test", izdelekDTO.getNaziv());
        assertEquals("Test", izdelekDTO.getOpis());
        assertEquals(9.99F, izdelekDTO.getCena());
        assertEquals(false, izdelekDTO.getAktiven());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_dodajanja());
        assertEquals(Date.valueOf(LocalDate.now()), izdelekDTO.getDatum_spremembe());
        assertEquals(100, izdelekDTO.getZaloga());
        assertEquals(1, izdelekDTO.getSlike().size());
        assertEquals(1, izdelekDTO.getLastnosti().size());

        verify(izdelekRepository).findById(5L);
        verify(izdelekService).pridobiIzdelek(5L);
    }
}
