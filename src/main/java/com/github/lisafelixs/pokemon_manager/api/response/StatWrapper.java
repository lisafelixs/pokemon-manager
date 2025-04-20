package com.github.lisafelixs.pokemon_manager.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatWrapper {
    private Integer base_stat;
    private Integer effort;
    private NamedAPIResource stat;
}