package com.TeraPadel.AplicacionReservaPadel.repository;

import com.TeraPadel.AplicacionReservaPadel.model.Reserva;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaMongoRepository extends MongoRepository<Reserva, String> {

    List<Reserva> findByInicioReserva(LocalDateTime inicioReserva);

    List<Reserva> findByIdUsuario(String idUsuario);

    List<Reserva> findByIdPista(String idPista);

}
