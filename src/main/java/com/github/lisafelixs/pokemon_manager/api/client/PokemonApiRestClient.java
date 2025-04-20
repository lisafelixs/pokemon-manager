package com.github.lisafelixs.pokemon_manager.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResponseDTO;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonDTO;

@Component
public class PokemonApiRestClient {

    private final RestTemplate restTemplate;

    @Value("${pokemonapi.base.url}")
    private String baseUrl;

    public PokemonApiRestClient (RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public PokemonApiListResponseDTO getAll(){
        String url = baseUrl + "/pokemon";
        return restTemplate.getForObject(url, PokemonApiListResponseDTO.class);
    }

    public PokemonDTO getDetails(int id){
        String url = baseUrl + "/pokemon/{id}";
        return restTemplate.getForObject(url, PokemonDTO.class, id);
    }
    
    

}
