package com.github.lisafelixs.pokemon_manager.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lisafelixs.pokemon_manager.api.client.PokemonApiRestClient;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResponseDTO;
import com.github.lisafelixs.pokemon_manager.db.model.Favorite;
import com.github.lisafelixs.pokemon_manager.db.repository.FavoriteRepository;
import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonListResponse;
import com.github.lisafelixs.pokemon_manager.service.PokemonService;

@Service
public class PokemonServiceImpl implements PokemonService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final PokemonApiRestClient pokemonApiRestClient;

    public PokemonServiceImpl(PokemonApiRestClient pokemonRestClient) {
        this.pokemonApiRestClient = pokemonRestClient;
    }

    @Override
    @Cacheable("allPokemon")
    public List<PokemonListResponse> getAll(String order) {

        PokemonApiListResponseDTO responseApi = pokemonApiRestClient.getAll();

        List<PokemonListResponse> responseList = new ArrayList<>();

        responseList = responseApi.getResults().stream().map(pokemon -> {
            String url = pokemon.getUrl().replace("https://pokeapi.co/api/v2/pokemon/", "");
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            int id = Integer.parseInt(url);
            return PokemonListResponse.builder().name(pokemon.getName()).id(id).build();
        }).collect(Collectors.toList());

        if (order != null && !order.isEmpty()) {
            if (order.equalsIgnoreCase("desc")) {
                responseList.sort(Comparator.comparing(PokemonListResponse::getName).reversed());
            } else {
                responseList.sort(Comparator.comparing(PokemonListResponse::getName));
            }
        }

        return responseList;
    }

    @Override
    @Transactional
    public void saveFavorite(FavoritePokemonRequest favoritePokemonRequest) {
        // TODO: tratar exception

        List<Integer> pokemonIds = favoritePokemonRequest.getPokemonIds();
        List<PokemonListResponse> allPokemonList = null;

        String key = "allPokemon::SimpleKey [null]";
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        byte[] valueBytes = redisTemplate.execute((RedisConnection connection) -> connection.get(keyBytes));

        if (valueBytes != null) {
            JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
            allPokemonList = (List<PokemonListResponse>) serializer.deserialize(valueBytes);
        } else {
            allPokemonList = getAll(null);
        }

        if (allPokemonList != null && pokemonIds != null) {
            for (int pokemonId : pokemonIds) {
                Optional<PokemonListResponse> foundPokemon = allPokemonList.stream()
                        .filter(pokemon -> pokemon.getId() == pokemonId)
                        .findFirst();
                if (foundPokemon.isPresent()) {
                    if (!favoriteRepository.existsById(pokemonId)) {
                        String namePokemon = foundPokemon.get().getName();
                        Favorite favorite = new Favorite();
                        favorite.setId(pokemonId);
                        favorite.setName(namePokemon);
                        favoriteRepository.save(favorite);
                    }
                }
            }
        }

    }

    @Override
    @Transactional
    public void deleteFavorite(int pokemonId) {

        if (favoriteRepository.existsById(pokemonId)) {
            favoriteRepository.deleteById(pokemonId);
        } else {
            // TODO: tratar exception
            throw new RuntimeException("Pokemon not found in favorites list.");
        }
    }

}
