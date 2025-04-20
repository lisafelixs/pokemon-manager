package com.github.lisafelixs.pokemon_manager.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbilityWrapper {
    private NamedAPIResource ability;
    private Boolean is_hidden;
    private Integer slot;
}
