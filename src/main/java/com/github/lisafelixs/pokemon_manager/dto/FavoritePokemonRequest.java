package com.github.lisafelixs.pokemon_manager.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePokemonRequest {

    List<Integer> pokemonIds;
}
