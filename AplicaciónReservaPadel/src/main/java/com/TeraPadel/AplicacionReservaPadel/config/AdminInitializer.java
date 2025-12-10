package com.TeraPadel.AplicacionReservaPadel.config;

import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioMongoRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UsuarioMongoRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepo.findByEmailUsuario("admin@terapadel.com").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombreUsuario("Admin");
            admin.setEmailUsuario("admin@terapadel.com");
            admin.setContraseñaUsuario(passwordEncoder.encode("admin"));
            admin.setRolUsuario("ADMIN");

            usuarioRepo.save(admin);
            System.out.println("Administrador creado: admin@terapadel.com");
        }
    }
}