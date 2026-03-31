package com.TeraPadel.AplicacionReservaPadel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest {
    private String emailUsuario;
    private String contraseñaUsuario;
}