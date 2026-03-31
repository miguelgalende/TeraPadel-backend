package com.TeraPadel.AplicacionReservaPadel.controller;

import com.TeraPadel.AplicacionReservaPadel.config.Propiedades;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")

public class TestController {
    private final Propiedades propiedades;
    public TestController(final Propiedades propiedades) {
        this.propiedades = propiedades;
    }
    @GetMapping("/OK")
    public String test() {
        return "OK " + propiedades.getTitulo();
    }
}
