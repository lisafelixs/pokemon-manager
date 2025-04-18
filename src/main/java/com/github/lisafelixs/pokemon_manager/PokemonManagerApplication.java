package com.github.lisafelixs.pokemon_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PokemonManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokemonManagerApplication.class, args);
	}

}
