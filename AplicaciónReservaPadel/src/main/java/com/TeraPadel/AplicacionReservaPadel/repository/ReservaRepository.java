package com.TeraPadel.AplicacionReservaPadel.repository;

import java.util.List;

import com.TeraPadel.AplicacionReservaPadel.dto.PeticionBusquedaReserva;
import com.TeraPadel.AplicacionReservaPadel.model.Reserva;

public interface ReservaRepository {

    List<Reserva> buscar(PeticionBusquedaReserva filtro);

}
