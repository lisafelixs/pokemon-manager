package com.github.lisafelixs.pokemon_manager.controller;


import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetailsListResponse;
import com.github.lisafelixs.pokemon_manager.dto.PokemonResponse;
import com.github.lisafelixs.pokemon_manager.service.PokemonService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    @Autowired
    PokemonService pokemonService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokemonResponse> getAllPokemon(@RequestParam(value = "order", required = false) String order) {
        PokemonResponse pokemonResponse = PokemonResponse.builder()
                .result(pokemonService.getAll(order))
                .build();
        return ResponseEntity.ok(pokemonResponse);
    }


    @PostMapping(path = "/favorites",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> saveFavoritePokemon(@RequestBody FavoritePokemonRequest favoritePokemonRequest) {
        pokemonService.saveFavorite(favoritePokemonRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping(path = "/details",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PokemonDetailsListResponse> getFavoritesPokemonDetails(@RequestParam(value = "order", required = false) String order) {
        return ResponseEntity.ok(pokemonService.getFavorites(order));
    }


    @DeleteMapping(path = "/{pokemonId}")
    @ResponseBody
    public ResponseEntity<Void> deleteFavorite(@PathVariable int pokemonId) {
        pokemonService.deleteFavorite(pokemonId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    
    

}
