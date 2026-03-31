package com.TeraPadel.AplicacionReservaPadel.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioMongoRepository repo;

    public UserDetailsServiceImpl(UsuarioMongoRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = repo.findByEmailUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
                usuario.getEmailUsuario(),
                usuario.getContraseñaUsuario(),
                List.of());
    }
}
