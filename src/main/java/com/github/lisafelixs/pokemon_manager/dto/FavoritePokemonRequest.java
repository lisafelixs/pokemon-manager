package com.github.lisafelixs.pokemon_manager.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavoritePokemonRequest {

    List<Integer> pokemonIds;
}
