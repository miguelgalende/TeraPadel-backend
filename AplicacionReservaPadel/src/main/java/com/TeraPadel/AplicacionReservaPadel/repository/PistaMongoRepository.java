package com.TeraPadel.AplicacionReservaPadel.repository;

import com.TeraPadel.AplicacionReservaPadel.model.Pista;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PistaMongoRepository extends MongoRepository<Pista, String>{

    List<Pista> findByIdClub(String idClub);

}