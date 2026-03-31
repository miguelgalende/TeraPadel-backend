package com.TeraPadel.AplicacionReservaPadel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@ConfigurationProperties("reserva.padel")
@Configuration
@Getter
@Setter

public class Propiedades {
    private String titulo;
}
