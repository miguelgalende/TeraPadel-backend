package com.TeraPadel.AplicacionReservaPadel.dto;

import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Usuario usuario;
}
