package com.github.lisafelixs.pokemon_manager.api.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveWrapper {
    private NamedAPIResource move;
    private List<MoveLearnMethodWrapper> version_group_details;
}
