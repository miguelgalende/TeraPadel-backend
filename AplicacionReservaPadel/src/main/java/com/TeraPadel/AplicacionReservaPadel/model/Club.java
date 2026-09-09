package com.TeraPadel.AplicacionReservaPadel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "clubes")
public class Club {

    @Id
    private String idClub;

    private String nombreClub;

    private String direccionClub;
    
    private String telefonoClub;

    private String imagenClub;

}
