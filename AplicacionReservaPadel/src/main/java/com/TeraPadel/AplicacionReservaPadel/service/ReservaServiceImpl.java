package com.TeraPadel.AplicacionReservaPadel.service;

import com.TeraPadel.AplicacionReservaPadel.dto.*;
import com.TeraPadel.AplicacionReservaPadel.exception.RecursoNoEncontradoException;
import com.TeraPadel.AplicacionReservaPadel.exception.ReservaConflictoException;
import com.TeraPadel.AplicacionReservaPadel.model.*;
import com.TeraPadel.AplicacionReservaPadel.repository.*;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("servicioReservas")
public class ReservaServiceImpl implements ReservaService {

        private final UsuarioMongoRepository usuarioMongoRepository;
        private final ReservaMongoRepository reservaMongoRepository;
        private final PistaMongoRepository pistaMongoRepository;
        private final MongoTemplate mongoTemplate;

        public ReservaServiceImpl(
                        UsuarioMongoRepository usuarioMongoRepository,
                        ReservaMongoRepository reservaMongoRepository,
                        PistaMongoRepository pistaMongoRepository,
                        MongoTemplate mongoTemplate) {
                this.usuarioMongoRepository = usuarioMongoRepository;
                this.reservaMongoRepository = reservaMongoRepository;
                this.pistaMongoRepository = pistaMongoRepository;
                this.mongoTemplate = mongoTemplate;
        }

        @Override
        public ReservaDto grabar(final PeticionCreacionReserva data) {

                Usuario actual = obtenerUsuarioAutenticado();

                pistaMongoRepository.findById(data.getIdPista())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Pista no encontrada"));

                LocalDateTime inicioDT = data.getInicioReserva();
                LocalDateTime finDT = data.getFinReserva();
                validarRangoHorario(inicioDT, finDT);

                String fecha = inicioDT.toLocalDate().toString();
                List<String> bloques = generarBloques(inicioDT, finDT);

                if (!reclamarBloques(data.getIdPista(), fecha, bloques)) {
                        throw new ReservaConflictoException(
                                        "Alguno de los horarios seleccionados ya está reservado ese día");
                }

                Reserva reserva = new Reserva();
                reserva.setIdUsuario(actual.getIdUsuario());
                reserva.setIdPista(data.getIdPista());
                reserva.setInicioReserva(inicioDT);
                reserva.setFinReserva(finDT);
                reserva.setEstadoReserva(data.getEstadoReserva() != null ? data.getEstadoReserva() : "ACTIVA");

                try {
                        Reserva guardada = reservaMongoRepository.save(reserva);
                        return convert(guardada);
                } catch (RuntimeException ex) {
                        liberarBloques(data.getIdPista(), fecha, bloques);
                        throw ex;
                }
        }

        @Override
        public ReservaDto actualizar(PeticionActualizacionReserva data) {

                Usuario actual = obtenerUsuarioAutenticado();

                Reserva reserva = reservaMongoRepository.findById(data.getIdReserva())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

                verificarPropietarioOAdmin(actual, reserva.getIdUsuario());

                pistaMongoRepository.findById(data.getIdPista())
                                .orElseThrow(() -> new RecursoNoEncontradoException("Pista no encontrada"));

                LocalDateTime nuevoInicio = data.getInicioReserva();
                LocalDateTime nuevoFin = data.getFinReserva();
                validarRangoHorario(nuevoInicio, nuevoFin);

                String fechaAntigua = reserva.getInicioReserva().toLocalDate().toString();
                List<String> bloquesAntiguos = generarBloques(reserva.getInicioReserva(), reserva.getFinReserva());
                String idPistaAntigua = reserva.getIdPista();

                String fechaNueva = nuevoInicio.toLocalDate().toString();
                List<String> bloquesNuevos = generarBloques(nuevoInicio, nuevoFin);
                String idPistaNueva = data.getIdPista();

                liberarBloques(idPistaAntigua, fechaAntigua, bloquesAntiguos);

                if (!reclamarBloques(idPistaNueva, fechaNueva, bloquesNuevos)) {
                        reclamarBloques(idPistaAntigua, fechaAntigua, bloquesAntiguos);
                        throw new ReservaConflictoException("Alguno de los nuevos horarios ya está reservado ese día");
                }

                reserva.setIdPista(idPistaNueva);
                reserva.setInicioReserva(nuevoInicio);
                reserva.setFinReserva(nuevoFin);
                if (data.getEstadoReserva() != null) {
                        reserva.setEstadoReserva(data.getEstadoReserva());
                }

                try {
                        Reserva actualizada = reservaMongoRepository.save(reserva);
                        return convert(actualizada);
                } catch (RuntimeException ex) {
                        liberarBloques(idPistaNueva, fechaNueva, bloquesNuevos);
                        reclamarBloques(idPistaAntigua, fechaAntigua, bloquesAntiguos);
                        throw ex;
                }
        }

        @Override
        public List<ReservaDto> listar() {
                return reservaMongoRepository.findAll().stream()
                                .map(this::convert)
                                .collect(Collectors.toList());
        }

        @Override
        public List<ReservaDto> listarPorUsuario(String idUsuario) {
                Usuario actual = obtenerUsuarioAutenticado();
                verificarPropietarioOAdmin(actual, idUsuario);

                return reservaMongoRepository.findByIdUsuario(idUsuario)
                                .stream()
                                .map(this::convert)
                                .collect(Collectors.toList());
        }

        @Override
        public void eliminar(String id) {
                Usuario actual = obtenerUsuarioAutenticado();

                Reserva reserva = reservaMongoRepository.findById(id)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva no encontrada"));

                verificarPropietarioOAdmin(actual, reserva.getIdUsuario());

                if (reserva.getFinReserva().isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("No se pueden cancelar reservas pasadas");
                }

                String fecha = reserva.getInicioReserva().toLocalDate().toString();
                List<String> bloques = generarBloques(reserva.getInicioReserva(), reserva.getFinReserva());

                liberarBloques(reserva.getIdPista(), fecha, bloques);
                reservaMongoRepository.deleteById(id);
        }

        private boolean reclamarBloques(String idPista, String fecha, List<String> bloques) {
                Query query = new Query(Criteria.where("_id").is(idPista)
                                .and("ocupadasPorDia." + fecha).nin(bloques));

                Update update = new Update();
                update.addToSet("ocupadasPorDia." + fecha).each(bloques.toArray());

                Pista resultado = mongoTemplate.findAndModify(
                                query, update, FindAndModifyOptions.options().returnNew(true), Pista.class);

                return resultado != null;
        }

        private void liberarBloques(String idPista, String fecha, List<String> bloques) {
                Query query = new Query(Criteria.where("_id").is(idPista));
                Update update = new Update().pullAll("ocupadasPorDia." + fecha, bloques.toArray());
                mongoTemplate.updateFirst(query, update, Pista.class);
        }

        private Usuario obtenerUsuarioAutenticado() {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                return usuarioMongoRepository.findByEmailUsuario(email)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        }

        private void verificarPropietarioOAdmin(Usuario actual, String idUsuarioReserva) {
                boolean esPropietario = actual.getIdUsuario().equals(idUsuarioReserva);
                boolean esAdmin = "ADMIN".equals(actual.getRolUsuario());
                if (!esPropietario && !esAdmin) {
                        throw new AccessDeniedException("No puedes gestionar reservas de otro usuario");
                }
        }

        private void validarRangoHorario(LocalDateTime inicio, LocalDateTime fin) {
                if (inicio == null || fin == null || !fin.isAfter(inicio)) {
                        throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
                }
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
