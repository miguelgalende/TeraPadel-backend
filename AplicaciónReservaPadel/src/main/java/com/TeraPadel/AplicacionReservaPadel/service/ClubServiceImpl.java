package com.TeraPadel.AplicacionReservaPadel.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.TeraPadel.AplicacionReservaPadel.model.Club;
import com.TeraPadel.AplicacionReservaPadel.repository.ClubMongoRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ClubServiceImpl implements ClubService {

    private final ClubMongoRepository clubRepo;

    public ClubServiceImpl(ClubMongoRepository clubRepo) {
        this.clubRepo = clubRepo;
    }

    @Override
    public List<Club> listar() {
        return clubRepo.findAll();
    }

    @Override
    public Club crear(Club club) {
        return clubRepo.save(club);
    }

    @Override
    public void eliminar(String id) {
        if (!clubRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Club no encontrado");
        }
        clubRepo.deleteById(id);
    }
}
