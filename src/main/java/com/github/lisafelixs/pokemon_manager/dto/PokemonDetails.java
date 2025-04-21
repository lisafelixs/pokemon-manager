package com.github.lisafelixs.pokemon_manager.dto;

import java.util.List;

import com.github.lisafelixs.pokemon_manager.api.response.AbilityWrapper;
import com.github.lisafelixs.pokemon_manager.api.response.TypeWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonDetails {

    private String name;
    private List<AbilityWrapper> abilities;
    private List<TypeWrapper> types;

}
