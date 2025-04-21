package com.github.lisafelixs.pokemon_manager.controller;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetails;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetailsListResponse;
import com.github.lisafelixs.pokemon_manager.dto.PokemonListResponse;
import com.github.lisafelixs.pokemon_manager.service.PokemonService;

@WebMvcTest(PokemonController.class)
public class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokemonService pokemonService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testDeleteFavorite() throws Exception {
        int pokemonIdToDelete = 10;

        mockMvc.perform(delete("/pokemon/{pokemonId}", pokemonIdToDelete)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(pokemonService, times(1)).deleteFavorite(pokemonIdToDelete);
    }

    @Test
    void testGetAllPokemon() throws Exception {
        List<PokemonListResponse> pokemonList = Arrays.asList(
                new PokemonListResponse("Bulbasaur", 1),
                new PokemonListResponse("Ivysaur", 2));

        when(pokemonService.getAll(null)).thenReturn(pokemonList);

        mockMvc.perform(get("/pokemon")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result[0].id").value(1))
                .andExpect(jsonPath("$.result[0].name").value("Bulbasaur"))
                .andExpect(jsonPath("$.result[1].id").value(2))
                .andExpect(jsonPath("$.result[1].name").value("Ivysaur"));

        verify(pokemonService, times(1)).getAll(null);
    }

    @Test
    void testGetFavoritesPokemonDetails() throws Exception {
        List<PokemonDetails> detailsList = Arrays.asList(
                new PokemonDetails("Bulbasaur", Collections.emptyList(), Collections.emptyList()),
                new PokemonDetails("Ivysaur", Collections.emptyList(), Collections.emptyList()));
        PokemonDetailsListResponse response = new PokemonDetailsListResponse(detailsList);

        when(pokemonService.getFavorites(null)).thenReturn(response);

        mockMvc.perform(get("/pokemon/details")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results[0].name").value("Bulbasaur"))
                .andExpect(jsonPath("$.results[1].name").value("Ivysaur"));

        verify(pokemonService, times(1)).getFavorites(null);

    }

    @Test
    void testSaveFavoritePokemon() throws Exception {
        FavoritePokemonRequest expectedRequest = new FavoritePokemonRequest(Arrays.asList(1, 2));

        mockMvc.perform(post("/pokemon/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expectedRequest)))
                .andExpect(status().isCreated());

        verify(pokemonService, times(1)).saveFavorite(argThat(actualRequest -> 
        actualRequest.getPokemonIds().equals(Arrays.asList(1, 2))));
    }
}
