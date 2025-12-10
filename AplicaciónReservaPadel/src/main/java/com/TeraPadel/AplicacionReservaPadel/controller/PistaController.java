package com.TeraPadel.AplicacionReservaPadel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeraPadel.AplicacionReservaPadel.model.Pista;
import com.TeraPadel.AplicacionReservaPadel.model.Reserva;
import com.TeraPadel.AplicacionReservaPadel.repository.PistaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.ReservaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.service.PistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pistas")
@CrossOrigin("*")
public class PistaController {
    private final PistaService pistaService;
    @Autowired
    private ReservaMongoRepository reservaMongoRepository;

    @Autowired
    private PistaMongoRepository pistaMongoRepository;

    public PistaController(PistaService pistaService) {
        this.pistaService = pistaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pista> obtenerPorId(@PathVariable String id) {
        Pista pista = pistaService.obtenerPorId(id);
        return ResponseEntity.ok(pista);
    }

    @PostMapping("/crear")
    public ResponseEntity<Pista> crear(@Valid @RequestBody Pista pista) {
        return ResponseEntity.ok(pistaService.crear(pista));
    }

    @GetMapping("/club/{idClub}")
    public List<Pista> listarPorClub(@PathVariable String idClub) {
        return pistaService.listarPorClub(idClub);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            List<Reserva> reservas = reservaMongoRepository.findByIdPista(id);
            if (!reservas.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se puede eliminar la pista porque tiene reservas asociadas."));
            }

            pistaMongoRepository.deleteById(id);

            return ResponseEntity.ok(Map.of("message", "Pista eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando la pista: " + e.getMessage()));
        }
    }
}
