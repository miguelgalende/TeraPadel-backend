package com.TeraPadel.AplicacionReservaPadel.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

import com.TeraPadel.AplicacionReservaPadel.model.Pista;
import com.TeraPadel.AplicacionReservaPadel.repository.PistaMongoRepository;
import org.webjars.NotFoundException;

@Service
public class PistaServiceImpl implements PistaService {
    private final PistaMongoRepository pistaRepo;

    public PistaServiceImpl(PistaMongoRepository pistaRepo) {
        this.pistaRepo = pistaRepo;
    }

    @Override
    public List<Pista> listarPorClub(String idClub) {
        return pistaRepo.findByIdClub(idClub);
    }

    @Override
    public Pista crear(Pista pista) {
        if (pista.getHorario() == null || pista.getHorario().isEmpty()) {
            pista.setHorario(generarHorario());
        }

        if (pista.getOcupadasPorDia() == null) {
            pista.setOcupadasPorDia(new HashMap<>());
        }

        return pistaRepo.save(pista);
    }

    private List<String> generarHorario() {
        List<String> horas = new ArrayList<>();
        int inicio = 9 * 60;
        int fin = 22 * 60;

        for (int m = inicio; m < fin; m += 30) {
            int h = m / 60;
            int min = m % 60;
            horas.add(String.format("%02d:%02d", h, min));
        }

        return horas;
    }

    @Override
    public void eliminar(String id) {
        if (!pistaRepo.existsById(id)) {
            throw new NotFoundException("Pista no encontrada");
        }
        pistaRepo.deleteById(id);
    }

    @Override
    public Pista obtenerPorId(String id) {
        return pistaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Pista no encontrada"));
    }
}
