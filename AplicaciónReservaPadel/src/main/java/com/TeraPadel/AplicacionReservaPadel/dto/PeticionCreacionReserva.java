package com.TeraPadel.AplicacionReservaPadel.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter

public class PeticionCreacionReserva {

    @NotNull
    private String idPista;

    @NotNull
    private String idUsuario;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inicioReserva;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime finReserva;

    private String estadoReserva = "ACTIVA";

}
