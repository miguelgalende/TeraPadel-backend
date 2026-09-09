package com.TeraPadel.AplicacionReservaPadel.service;

import org.springframework.stereotype.Service;

import com.TeraPadel.AplicacionReservaPadel.dto.DashboardDto;
import com.TeraPadel.AplicacionReservaPadel.repository.ClubMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.PistaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.ReservaMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;

@Service
public class AdminService {

    private final UsuarioMongoRepository usuarioRepository;
    private final ClubMongoRepository clubRepository;
    private final PistaMongoRepository pistaRepository;
    private final ReservaMongoRepository reservaRepository;

    public AdminService(
            UsuarioMongoRepository usuarioRepository,
            ClubMongoRepository clubRepository,
            PistaMongoRepository pistaRepository,
            ReservaMongoRepository reservaRepository) {

        this.usuarioRepository = usuarioRepository;
        this.clubRepository = clubRepository;
        this.pistaRepository = pistaRepository;
        this.reservaRepository = reservaRepository;
    }

    public DashboardDto obtenerDashboard() {

        DashboardDto dto = new DashboardDto();

        dto.setUsuarios(usuarioRepository.count());

        dto.setClubes(clubRepository.count());

        dto.setPistas(pistaRepository.count());

        dto.setReservas(reservaRepository.count());

        return dto;

    }

}
