package com.TeraPadel.AplicacionReservaPadel.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Documentacion / swagger
                        .requestMatchers("/reserva-padel", "/reserva-padel-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/test/**").permitAll()

                        // Autenticacion publica
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios/registro").permitAll()

                        // Lectura publica: se necesita para explorar clubs/pistas sin cuenta
                        .requestMatchers(HttpMethod.GET, "/api/clubs/listar", "/api/clubs/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pistas/**").permitAll()

                        // Solo administradores
                        .requestMatchers(HttpMethod.GET, "/api/usuarios/listar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/clubs/crear").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clubs/eliminar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/pistas/crear").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pistas/eliminar/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/listar").hasRole("ADMIN")

                        // El resto de reservas: cualquier usuario autenticado
                        .requestMatchers("/api/reservas/**").authenticated()

                        // Cualquier otra ruta no contemplada explicitamente requiere login
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}