package com.TeraPadel.AplicacionReservaPadel.service;

import java.util.List;
import com.TeraPadel.AplicacionReservaPadel.model.Pista;

public interface PistaService {

    List<Pista> listarPorClub(String idClub);

    Pista crear(Pista pista);

    void eliminar(String id);

    Pista obtenerPorId(String id);
}
