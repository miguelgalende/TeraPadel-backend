package com.TeraPadel.AplicacionReservaPadel.config;

import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import com.TeraPadel.AplicacionReservaPadel.repository.UsuarioMongoRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsuarioMongoRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default-email}")
    private String adminEmail;

    @Value("${admin.default-password}")
    private String adminPassword;

    public AdminInitializer(UsuarioMongoRepository usuarioRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepo.findByEmailUsuario(adminEmail).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNombreUsuario("Admin");
            admin.setEmailUsuario(adminEmail);
            admin.setContraseñaUsuario(passwordEncoder.encode(adminPassword));
            admin.setRolUsuario("ADMIN");

            usuarioRepo.save(admin);

            if ("admin".equals(adminPassword)) {
                log.warn("Administrador creado con la contrasena por defecto ({}). " +
                        "Cambiala definiendo la variable de entorno ADMIN_PASSWORD.", adminEmail);
            } else {
                log.info("Administrador creado: {}", adminEmail);
            }
        }
    }
}