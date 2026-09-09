package com.TeraPadel.AplicacionReservaPadel.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PeticionBusquedaReserva {

    private LocalDateTime fechaCreacionDesde;

    private LocalDateTime fechaCreacionHasta;

    private String id_usuario;

}
