package com.github.lisafelixs.pokemon_manager.service;

import java.util.List;

import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonListResponse;

public interface PokemonService {

    List<PokemonListResponse> getAll(String order);
    void saveFavorite(FavoritePokemonRequest favoritePokemonRequest);
    // List<PokemonListResponse> getFavorites(String order);
    void deleteFavorite(int pokemonId);
}
