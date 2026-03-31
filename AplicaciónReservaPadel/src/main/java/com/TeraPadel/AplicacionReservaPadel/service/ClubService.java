package com.TeraPadel.AplicacionReservaPadel.service;

import java.util.List;
import com.TeraPadel.AplicacionReservaPadel.model.Club;

public interface ClubService {
    List<Club> listar();

    Club crear(Club club);

    void eliminar(String id);
}