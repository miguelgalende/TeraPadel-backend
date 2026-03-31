package com.TeraPadel.AplicacionReservaPadel.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ReservaDto {

    private String idReserva;

    private String idUsuario;

    private String idPista;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime inicioReserva;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime finReserva;

    private String estadoReserva;

}
