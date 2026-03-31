package com.TeraPadel.AplicacionReservaPadel.service;

import com.TeraPadel.AplicacionReservaPadel.dto.*;
import com.TeraPadel.AplicacionReservaPadel.model.*;
import com.TeraPadel.AplicacionReservaPadel.repository.*;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service("servicioReservas")
public class ReservaServiceImpl implements ReservaService {

        private final UsuarioMongoRepository usuarioMongoRepository;
        private final ReservaMongoRepository reservaMongoRepository;
        private final PistaMongoRepository pistaMongoRepository;

        public ReservaServiceImpl(
                        UsuarioMongoRepository usuarioMongoRepository,
                        ReservaMongoRepository reservaMongoRepository,
                        PistaMongoRepository pistaMongoRepository) {
                this.usuarioMongoRepository = usuarioMongoRepository;
                this.reservaMongoRepository = reservaMongoRepository;
                this.pistaMongoRepository = pistaMongoRepository;
        }

        @Override
        public ReservaDto grabar(final PeticionCreacionReserva data) {

                usuarioMongoRepository.findById(data.getIdUsuario())
                                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

                Pista pista = pistaMongoRepository.findById(data.getIdPista())
                                .orElseThrow(() -> new NotFoundException("Pista no encontrada"));

                if (pista.getOcupadasPorDia() == null) {
                        pista.setOcupadasPorDia(new HashMap<>());
                }

                LocalDateTime inicioDT = data.getInicioReserva();
                LocalDateTime finDT = data.getFinReserva();

                String fecha = inicioDT.toLocalDate().toString();

                List<String> bloques = generarBloques(inicioDT, finDT);

                List<String> ocupadasDia = pista.getOcupadasPorDia()
                                .getOrDefault(fecha, new ArrayList<>());

                for (String bloque : bloques) {
                        if (ocupadasDia.contains(bloque)) {
                                throw new RuntimeException("La hora " + bloque + " ya está reservada ese día");
                        }
                }

                ocupadasDia.addAll(bloques);
                pista.getOcupadasPorDia().put(fecha, ocupadasDia);

                pistaMongoRepository.save(pista);

                Reserva reserva = new Reserva();
                reserva.setIdUsuario(data.getIdUsuario());
                reserva.setIdPista(data.getIdPista());
                reserva.setInicioReserva(data.getInicioReserva());
                reserva.setFinReserva(data.getFinReserva());
                reserva.setEstadoReserva(data.getEstadoReserva());

                Reserva guardada = reservaMongoRepository.save(reserva);
                return convert(guardada);
        }

        private List<String> generarBloques(LocalDateTime inicioDT, LocalDateTime finDT) {
                List<String> bloques = new ArrayList<>();

                LocalDateTime actual = inicioDT;

                while (!actual.isAfter(finDT.minusMinutes(1))) {
                        String hora = actual.toLocalTime()
                                        .format(DateTimeFormatter.ofPattern("HH:mm"));
                        bloques.add(hora);

                        actual = actual.plusMinutes(30);
                }

                return bloques;
        }

        @Override
        public ReservaDto actualizar(PeticionActualizacionReserva data) {

                Reserva reserva = reservaMongoRepository.findById(data.getIdReserva())
                                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

                usuarioMongoRepository.findById(data.getIdUsuario())
                                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

                pistaMongoRepository.findById(data.getIdPista())
                                .orElseThrow(() -> new NotFoundException("Pista no encontrada"));

                reserva.setIdUsuario(data.getIdUsuario());
                reserva.setIdPista(data.getIdPista());
                reserva.setInicioReserva(data.getInicioReserva());
                reserva.setFinReserva(data.getFinReserva());
                reserva.setEstadoReserva(data.getEstadoReserva());

                Reserva actualizada = reservaMongoRepository.save(reserva);
                return convert(actualizada);
        }

        @Override
        public List<ReservaDto> listar() {
                return reservaMongoRepository.findAll().stream()
                                .map(this::convert)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ReservaDto> listarPorUsuario(String idUsuario) {
                return reservaMongoRepository.findByIdUsuario(idUsuario)
                                .stream()
                                .map(this::convert)
                                .collect(Collectors.toList());
        }

        @Override
        public void eliminar(String id) {
                Reserva reserva = reservaMongoRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Reserva no encontrada"));

                if (reserva.getFinReserva().isBefore(LocalDateTime.now())) {
                        throw new RuntimeException("No se pueden cancelar reservas pasadas");
                }

                Pista pista = pistaMongoRepository.findById(reserva.getIdPista())
                                .orElseThrow(() -> new NotFoundException("Pista no encontrada"));

                String fecha = reserva.getInicioReserva().toLocalDate().toString();

                if (pista.getOcupadasPorDia() != null &&
                                pista.getOcupadasPorDia().containsKey(fecha)) {

                        List<String> bloquesReserva = generarBloques(
                                        reserva.getInicioReserva(),
                                        reserva.getFinReserva());

                        pista.getOcupadasPorDia()
                                        .get(fecha)
                                        .removeAll(bloquesReserva);

                        if (pista.getOcupadasPorDia().get(fecha).isEmpty()) {
                                pista.getOcupadasPorDia().remove(fecha);
                        }

                        pistaMongoRepository.save(pista);
                }

                reservaMongoRepository.deleteById(id);
        }

        private ReservaDto convert(Reserva reserva) {
                ReservaDto dto = new ReservaDto();
                dto.setIdReserva(reserva.getIdReserva());
                dto.setIdUsuario(reserva.getIdUsuario());
                dto.setIdPista(reserva.getIdPista());
                dto.setInicioReserva(reserva.getInicioReserva());
                dto.setFinReserva(reserva.getFinReserva());
                dto.setEstadoReserva(reserva.getEstadoReserva());
                return dto;
        }
}
