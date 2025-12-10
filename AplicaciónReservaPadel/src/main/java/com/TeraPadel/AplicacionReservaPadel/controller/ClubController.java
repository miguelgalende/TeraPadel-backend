package com.TeraPadel.AplicacionReservaPadel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.TeraPadel.AplicacionReservaPadel.model.Club;
import com.TeraPadel.AplicacionReservaPadel.repository.ClubMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.service.ClubService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "*")
public class ClubController {

    private final ClubService clubService;

    @Autowired
    private ClubMongoRepository clubMongoRepository;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Club> crear(@Valid @RequestBody Club club) {
        return ResponseEntity.ok(clubService.crear(club));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Club>> listar() {
        return ResponseEntity.ok(clubService.listar());
    }

    @DeleteMapping("/eliminar/{idClub}")
    public ResponseEntity<?> eliminar(@PathVariable String idClub) {
        try {
            clubMongoRepository.deleteById(idClub);
            return ResponseEntity.ok(Map.of("message", "Club eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando el club: " + e.getMessage()));
        }
    }
}