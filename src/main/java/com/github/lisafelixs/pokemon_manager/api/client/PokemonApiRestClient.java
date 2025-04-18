package com.github.lisafelixs.pokemon_manager.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResponseDTO;

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

    public PokemonApiListResponseDTO getDetails(int id){
        //TODO : arrumar o responseDTO para receber o retorno correto
        String url = baseUrl + "/pokemon/{id}";
        return restTemplate.getForObject(url, PokemonApiListResponseDTO.class, id);
    }
    
    

}
