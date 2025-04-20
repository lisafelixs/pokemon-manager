package com.github.lisafelixs.pokemon_manager.api.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeldItemWrapper {
    private NamedAPIResource item;
    private List<HeldItemVersionDetail> version_details;
}
