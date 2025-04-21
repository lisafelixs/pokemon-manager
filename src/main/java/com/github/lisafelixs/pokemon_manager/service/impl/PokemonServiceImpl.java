package com.github.lisafelixs.pokemon_manager.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.lisafelixs.pokemon_manager.api.client.PokemonApiRestClient;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResponseDTO;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonDTO;
import com.github.lisafelixs.pokemon_manager.db.model.Favorite;
import com.github.lisafelixs.pokemon_manager.db.repository.FavoriteRepository;
import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetails;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetailsListResponse;
import com.github.lisafelixs.pokemon_manager.dto.PokemonListResponse;
import com.github.lisafelixs.pokemon_manager.exception.PokeApiConnectionException;
import com.github.lisafelixs.pokemon_manager.exception.PokemonNotFoundException;
import com.github.lisafelixs.pokemon_manager.service.PokemonService;

@Service
public class PokemonServiceImpl implements PokemonService {

    @Value("${pokemonapi.list.url}")
    private String URL;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
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
            String url = pokemon.getUrl().replace(URL, "");
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
            throw new PokemonNotFoundException("Pokémon não encontrado na lista de favoritos.");
        }
    }

    @Override
    public PokemonDetailsListResponse getFavorites(String order) {

        List<Favorite> favorites = favoriteRepository.findAll();
        List<CompletableFuture<PokemonDetails>> futures = new ArrayList<>();

        if (!favorites.isEmpty()) {
            for (Favorite favorite : favorites) {
                futures.add(fetchPokemonDetailsAsync(favorite.getId()));
            }

            List<PokemonDetails> details = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(pokemonDetails -> pokemonDetails != null)
                    .collect(Collectors.toList());

            if (order != null && !order.isEmpty()) {
                if (order.equalsIgnoreCase("desc")) {
                    details.sort(Comparator.comparing(PokemonDetails::getName).reversed());
                } else {
                    details.sort(Comparator.comparing(PokemonDetails::getName));
                }
            }

            PokemonDetailsListResponse pokemonDetailsListResponse = PokemonDetailsListResponse.builder()
                    .results(details)
                    .build();

            return pokemonDetailsListResponse;

        } else {
            throw new PokemonNotFoundException("Nenhum Pokémon encontrado na lista de favoritos.");
        }
    }

    @Async
    public CompletableFuture<PokemonDetails> fetchPokemonDetailsAsync(Integer pokemonId) {
        try {
            PokemonDTO pokemonDetails = pokemonApiRestClient.getDetails(pokemonId);
            if (pokemonDetails != null) {
                PokemonDetails detail = PokemonDetails.builder()
                        .name(pokemonDetails.getName())
                        .abilities(pokemonDetails.getAbilities())
                        .types(pokemonDetails.getTypes())
                        .build();
                return CompletableFuture.completedFuture(detail);
            } else {
                return CompletableFuture.completedFuture(null);
            }
        } catch (Exception e) {
            throw new PokeApiConnectionException("Erro ao buscar detalhes do Pokémon com ID: " + pokemonId, e);
        }

    }

}
