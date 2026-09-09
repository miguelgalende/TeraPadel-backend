package com.TeraPadel.AplicacionReservaPadel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String idUsuario;

    private String nombreUsuario;

    private String apellidosUsuario;

    private String telefonoUsuario;

    private String emailUsuario;

    private String contraseñaUsuario;

    private String rolUsuario = "USER";

}
