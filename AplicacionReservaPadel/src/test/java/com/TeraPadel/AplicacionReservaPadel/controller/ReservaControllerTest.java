package com.TeraPadel.AplicacionReservaPadel.controller;

import com.TeraPadel.AplicacionReservaPadel.dto.PeticionCreacionReserva;
import com.TeraPadel.AplicacionReservaPadel.dto.ReservaDto;
import com.TeraPadel.AplicacionReservaPadel.security.JwtUtil;
import com.TeraPadel.AplicacionReservaPadel.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.TeraPadel.AplicacionReservaPadel.security.JwtFilter;

@WebMvcTest(controllers = ReservaController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { JwtFilter.class, JwtUtil.class }) })
@AutoConfigureMockMvc(addFilters = false)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrearReserva() throws Exception {
        PeticionCreacionReserva peticion = new PeticionCreacionReserva();
        peticion.setIdUsuario("user1");
        peticion.setIdPista("pista1");
        peticion.setInicioReserva(LocalDateTime.now().plusHours(1));
        peticion.setFinReserva(LocalDateTime.now().plusHours(2));
        peticion.setEstadoReserva("CONFIRMADA");

        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setIdReserva("reserva1");

        when(reservaService.grabar(any(PeticionCreacionReserva.class))).thenReturn(reservaDto);

        mockMvc.perform(post("/api/reservas/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(peticion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value("reserva1"));
    }

    @Test
    void testListarReservas() throws Exception {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setIdReserva("reserva1");

        when(reservaService.listar()).thenReturn(List.of(reservaDto));

        mockMvc.perform(get("/api/reservas/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value("reserva1"));
    }

    @Test
    void testEliminarReserva() throws Exception {
        mockMvc.perform(delete("/api/reservas/eliminar/reserva1"))
                .andExpect(status().isNoContent());
    }
}