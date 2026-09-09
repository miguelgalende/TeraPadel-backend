package com.TeraPadel.AplicacionReservaPadel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.TeraPadel.AplicacionReservaPadel.repository")
public class AplicacionReservaPadelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AplicacionReservaPadelApplication.class, args);
	}

}
