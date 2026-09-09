package com.TeraPadel.AplicacionReservaPadel.service;

import com.TeraPadel.AplicacionReservaPadel.dto.PeticionCreacionReserva;
import com.TeraPadel.AplicacionReservaPadel.dto.ReservaDto;
import com.TeraPadel.AplicacionReservaPadel.model.Pista;
import com.TeraPadel.AplicacionReservaPadel.model.Reserva;
import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import com.TeraPadel.AplicacionReservaPadel.repository.PistaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.ReservaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private UsuarioMongoRepository usuarioMongoRepository;

    @Mock
    private ReservaMongoRepository reservaMongoRepository;

    @Mock
    private PistaMongoRepository pistaMongoRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    private Usuario usuario;
    private Pista pista;
    private Reserva reserva;
    private PeticionCreacionReserva peticionCreacion;

    @BeforeEach
    void setUp() {
        LocalDateTime inicio = LocalDateTime.of(2026, 4, 30, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 4, 30, 11, 0);

        usuario = new Usuario();
        usuario.setIdUsuario("user1");

        pista = new Pista();
        pista.setIdPista("pista1");
        pista.setOcupadasPorDia(new HashMap<>());

        reserva = new Reserva();
        reserva.setIdReserva("reserva1");
        reserva.setIdUsuario("user1");
        reserva.setIdPista("pista1");
        reserva.setInicioReserva(inicio);
        reserva.setFinReserva(fin);
        reserva.setEstadoReserva("CONFIRMADA");

        peticionCreacion = new PeticionCreacionReserva();
        peticionCreacion.setIdUsuario("user1");
        peticionCreacion.setIdPista("pista1");
        peticionCreacion.setInicioReserva(inicio);
        peticionCreacion.setFinReserva(fin);
        peticionCreacion.setEstadoReserva("CONFIRMADA");
    }

    @Test
    void testGrabar_Success() {
        when(usuarioMongoRepository.findById("user1")).thenReturn(Optional.of(usuario));
        when(pistaMongoRepository.findById("pista1")).thenReturn(Optional.of(pista));
        when(reservaMongoRepository.save(any(Reserva.class))).thenReturn(reserva);
        when(pistaMongoRepository.save(any(Pista.class))).thenReturn(pista);

        ReservaDto result = reservaService.grabar(peticionCreacion);

        assertNotNull(result);
        assertEquals("reserva1", result.getIdReserva());
        verify(reservaMongoRepository).save(any(Reserva.class));
        verify(pistaMongoRepository).save(any(Pista.class));
    }

    @Test
    void testGrabar_UsuarioNotFound() {
        when(usuarioMongoRepository.findById("user1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservaService.grabar(peticionCreacion));
    }

    @Test
    void testGrabar_PistaNotFound() {
        when(usuarioMongoRepository.findById("user1")).thenReturn(Optional.of(usuario));
        when(pistaMongoRepository.findById("pista1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservaService.grabar(peticionCreacion));
    }

    @Test
    void testGrabar_HoraOcupada() {
        pista.getOcupadasPorDia().put(peticionCreacion.getInicioReserva().toLocalDate().toString(), List.of("10:00"));
        when(usuarioMongoRepository.findById("user1")).thenReturn(Optional.of(usuario));
        when(pistaMongoRepository.findById("pista1")).thenReturn(Optional.of(pista));

        assertThrows(RuntimeException.class, () -> reservaService.grabar(peticionCreacion));
    }

    @Test
    void testListar() {
        when(reservaMongoRepository.findAll()).thenReturn(List.of(reserva));

        List<ReservaDto> result = reservaService.listar();

        assertEquals(1, result.size());
        assertEquals("reserva1", result.get(0).getIdReserva());
    }

    @Test
    void testListarPorUsuario() {
        when(reservaMongoRepository.findByIdUsuario("user1")).thenReturn(List.of(reserva));

        List<ReservaDto> result = reservaService.listarPorUsuario("user1");

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getIdUsuario());
    }

    @Test
    void testEliminar_Success() {
        String fecha = reserva.getInicioReserva().toLocalDate().toString();
        List<String> bloques = List.of("10:00", "10:30");
        pista.getOcupadasPorDia().put(fecha, new ArrayList<>(bloques));
        when(reservaMongoRepository.findById("reserva1")).thenReturn(Optional.of(reserva));
        when(pistaMongoRepository.findById("pista1")).thenReturn(Optional.of(pista));

        reservaService.eliminar("reserva1");

        verify(reservaMongoRepository).deleteById("reserva1");
        verify(pistaMongoRepository).save(any(Pista.class));
    }

    @Test
    void testEliminar_ReservaNotFound() {
        when(reservaMongoRepository.findById("reserva1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reservaService.eliminar("reserva1"));
    }

    @Test
    void testEliminar_ReservaPasada() {
        reserva.setFinReserva(LocalDateTime.of(2026, 4, 29, 11, 0)); // Past reservation
        when(reservaMongoRepository.findById("reserva1")).thenReturn(Optional.of(reserva));

        assertThrows(RuntimeException.class, () -> reservaService.eliminar("reserva1"));
    }
}