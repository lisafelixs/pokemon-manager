package com.github.lisafelixs.pokemon_manager.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveLearnMethodWrapper {
    private NamedAPIResource version_group;
    private NamedAPIResource move_learn_method;
    private Integer level_learned_at;
}