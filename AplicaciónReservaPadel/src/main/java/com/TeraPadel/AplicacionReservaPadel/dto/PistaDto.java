package com.TeraPadel.AplicacionReservaPadel.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PistaDto {

    private String idPista;

    @NotBlank(message = "El nombre de la pista es obligatorio")
    private String nombrePista;

    @NotBlank(message = "El id del club es obligatorio")
    private String idClub;

    @NotBlank(message = "La imagen de la pista es obligatoria")
    private String imagenPista;

    private List<String> horario;

    private Map<String, List<String>> ocupadasPorDia;
}
