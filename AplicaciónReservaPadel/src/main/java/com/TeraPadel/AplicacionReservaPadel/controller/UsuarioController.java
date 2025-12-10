package com.TeraPadel.AplicacionReservaPadel.controller;

import com.TeraPadel.AplicacionReservaPadel.dto.LoginRequest;
import com.TeraPadel.AplicacionReservaPadel.dto.LoginResponse;
import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;
import com.TeraPadel.AplicacionReservaPadel.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE, RequestMethod.OPTIONS })
public class UsuarioController {
    private final UsuarioMongoRepository usuarioMongoRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioController(UsuarioMongoRepository usuarioMongoRepository, JwtUtil jwtUtil) {
        this.usuarioMongoRepository = usuarioMongoRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {

        Optional<Usuario> existente = usuarioMongoRepository.findByEmailUsuario(nuevoUsuario.getEmailUsuario());
        if (existente.isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        String contraseñaEncriptada = passwordEncoder.encode(nuevoUsuario.getContraseñaUsuario());
        nuevoUsuario.setContraseñaUsuario(contraseñaEncriptada);

        Usuario guardado = usuarioMongoRepository.save(nuevoUsuario);
        return ResponseEntity.ok(guardado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        Optional<Usuario> usuarioOpt = usuarioMongoRepository.findByEmailUsuario(loginRequest.getEmailUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(loginRequest.getContraseñaUsuario(), usuario.getContraseñaUsuario())) {
            return ResponseEntity.status(401).body("Contraseña incorrecta");
        }

        String token = jwtUtil.generarToken(usuario.getEmailUsuario());

        return ResponseEntity.ok(new LoginResponse(token, usuario));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioMongoRepository.findAll());
    }
}
