package com.TeraPadel.AplicacionReservaPadel.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "pistas")
public class Pista {

    @Id
    private String idPista;

    private String nombrePista;

    private String idClub;

    private String imagenPista;

    private List<String> horario;

    private Map<String, List<String>> ocupadasPorDia;

}
