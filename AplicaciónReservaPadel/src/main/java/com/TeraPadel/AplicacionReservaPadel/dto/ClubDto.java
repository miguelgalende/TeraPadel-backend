package com.TeraPadel.AplicacionReservaPadel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClubDto {

    private String idClub;

    @NotBlank(message = "El nombre del club es obligatorio")
    private String nombre_club;

    @NotBlank(message = "La dirección del club es obligatoria")
    private String direccion_club;

    @NotBlank(message = "El teléfono del club es obligatorio")
    private String telefono_club;

    @NotBlank(message = "La iamgen del club es obligatoria")
    private String imagenClub;

}
