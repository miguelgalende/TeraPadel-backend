package com.TeraPadel.AplicacionReservaPadel.repository;

import com.TeraPadel.AplicacionReservaPadel.model.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubMongoRepository extends MongoRepository<Club, String>{

}
