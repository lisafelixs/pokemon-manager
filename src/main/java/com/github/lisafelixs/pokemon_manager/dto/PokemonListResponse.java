package com.github.lisafelixs.pokemon_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PokemonListResponse {

    String name;
    Integer id;

}
