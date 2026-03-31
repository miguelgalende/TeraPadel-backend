package com.TeraPadel.AplicacionReservaPadel.controller;

import com.TeraPadel.AplicacionReservaPadel.dto.*;
import com.TeraPadel.AplicacionReservaPadel.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin("*")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<ReservaDto> crearReserva(@Valid @RequestBody PeticionCreacionReserva peticion) {
        return ResponseEntity.ok(reservaService.grabar(peticion));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ReservaDto>> listarReservas() {
        return ResponseEntity.ok(reservaService.listar());
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ReservaDto> actualizarReserva(@Valid @RequestBody PeticionActualizacionReserva peticion) {
        return ResponseEntity.ok(reservaService.actualizar(peticion));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable String id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservaDto>> listarReservasPorUsuario(@PathVariable String idUsuario) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(idUsuario));
    }

}
