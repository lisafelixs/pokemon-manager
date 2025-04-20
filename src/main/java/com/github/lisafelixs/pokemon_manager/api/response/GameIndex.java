package com.github.lisafelixs.pokemon_manager.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameIndex {
    private Integer game_index;
    private NamedAPIResource version;
}
