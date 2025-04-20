package com.github.lisafelixs.pokemon_manager.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeldItemVersionDetail {
    private Integer rarity;
    private NamedAPIResource version;
}