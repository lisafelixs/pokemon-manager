package com.github.lisafelixs.pokemon_manager.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PokemonListResponse implements Serializable{

    String name;
    Integer id;

}
