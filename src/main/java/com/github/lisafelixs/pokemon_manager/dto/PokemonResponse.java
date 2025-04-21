package com.github.lisafelixs.pokemon_manager.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PokemonResponse {
    List<PokemonListResponse> result;
}
