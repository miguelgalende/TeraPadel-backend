package com.TeraPadel.AplicacionReservaPadel.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter

public class PeticionActualizacionReserva {

    @NotNull
    private String idReserva;

    @NotNull
    private String idPista;

    @NotNull
    private String idUsuario;

    @NotNull
    private LocalDateTime inicioReserva;

    @NotNull
    private LocalDateTime finReserva;

    private String estadoReserva;

}
