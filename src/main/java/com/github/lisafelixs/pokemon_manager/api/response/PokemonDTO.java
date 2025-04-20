package com.github.lisafelixs.pokemon_manager.api.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PokemonDTO {
    
    private Integer id;
    private String name;
    private Integer base_experience;
    private Integer height;
    private Integer weight;
    private Boolean is_default;
    private Integer order;
    private NamedAPIResource species;
    private Sprites sprites;
    private List<AbilityWrapper> abilities;
    private List<TypeWrapper> types;
    private List<StatWrapper> stats;
    private List<MoveWrapper> moves;
    private List<NamedAPIResource> forms;
    private List<GameIndex> game_indices;
    private List<HeldItemWrapper> held_items;
    private String location_area_encounters;
}