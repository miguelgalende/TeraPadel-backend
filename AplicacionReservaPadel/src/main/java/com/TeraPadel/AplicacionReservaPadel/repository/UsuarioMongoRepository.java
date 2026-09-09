package com.TeraPadel.AplicacionReservaPadel.repository;

import com.TeraPadel.AplicacionReservaPadel.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioMongoRepository extends MongoRepository<Usuario, String>{

    List<Usuario> findUsuarioByIdUsuario(final String id_usuario);

    Optional<Usuario> findByEmailUsuario(String email);

}
