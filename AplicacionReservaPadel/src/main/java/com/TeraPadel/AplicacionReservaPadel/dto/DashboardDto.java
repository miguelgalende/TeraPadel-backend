package com.TeraPadel.AplicacionReservaPadel.dto;

import lombok.Data;

@Data
public class DashboardDto {

    private long usuarios;

    private long clubes;

    private long pistas;

    private long reservas;

    private long reservasConfirmadas;

    private long reservasCanceladas;

}
