package com.github.lisafelixs.pokemon_manager.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.lisafelixs.pokemon_manager.api.client.PokemonApiRestClient;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResponseDTO;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonApiListResultDTO;
import com.github.lisafelixs.pokemon_manager.api.response.PokemonDTO;
import com.github.lisafelixs.pokemon_manager.db.model.Favorite;
import com.github.lisafelixs.pokemon_manager.db.repository.FavoriteRepository;
import com.github.lisafelixs.pokemon_manager.dto.FavoritePokemonRequest;
import com.github.lisafelixs.pokemon_manager.dto.PokemonDetailsListResponse;
import com.github.lisafelixs.pokemon_manager.dto.PokemonListResponse;
import com.github.lisafelixs.pokemon_manager.exception.PokeApiConnectionException;
import com.github.lisafelixs.pokemon_manager.exception.PokemonNotFoundException;

@ExtendWith(MockitoExtension.class)
public class PokemonServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private PokemonApiRestClient pokemonApiRestClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private PokemonServiceImpl pokemonService;

    @Value("${pokemonapi.list.url}")
    private String URL;

    @BeforeEach
    void setUp() {
        pokemonService = new PokemonServiceImpl(pokemonApiRestClient);
        ReflectionTestUtils.setField(pokemonService, "favoriteRepository", favoriteRepository);
        ReflectionTestUtils.setField(pokemonService, "URL", "https://pokeapi.co/api/v2/pokemon/");
        ReflectionTestUtils.setField(pokemonService, "redisTemplate", redisTemplate);
    }

    @Test
    void testDeleteFavorite() {

        int pokemonIdToDelete = 10;
        when(favoriteRepository.existsById(pokemonIdToDelete)).thenReturn(true);
        doNothing().when(favoriteRepository).deleteById(pokemonIdToDelete);

        assertDoesNotThrow(() -> pokemonService.deleteFavorite(pokemonIdToDelete));
        verify(favoriteRepository, times(1)).existsById(pokemonIdToDelete);
        verify(favoriteRepository, times(1)).deleteById(pokemonIdToDelete);
    }

    @Test
    void testDeleteFavoriteError() {

        int pokemonIdToDelete = 10;
        when(favoriteRepository.existsById(pokemonIdToDelete)).thenReturn(false);

        assertThrows(PokemonNotFoundException.class, () -> pokemonService.deleteFavorite(pokemonIdToDelete));
    }


    @Test
    void testGetAllSuccess() {
       PokemonApiListResponseDTO responseApi = new PokemonApiListResponseDTO();
        List<PokemonApiListResultDTO> results = Arrays.asList(
                new PokemonApiListResultDTO("Bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                new PokemonApiListResultDTO("Ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
        );
        responseApi.setResults(results);

        when(pokemonApiRestClient.getAll()).thenReturn(responseApi);

        List<PokemonListResponse> responseList = pokemonService.getAll(null);

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Bulbasaur", responseList.get(0).getName());
        assertEquals(1, responseList.get(0).getId());
        assertEquals("Ivysaur", responseList.get(1).getName());
        assertEquals(2, responseList.get(1).getId());
    }

    @Test
    void testGetAllSuccessOrderDesc() {
        PokemonApiListResponseDTO responseApi = new PokemonApiListResponseDTO();
        List<PokemonApiListResultDTO> results = Arrays.asList(
                new PokemonApiListResultDTO("Bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                new PokemonApiListResultDTO("Ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
        );
        responseApi.setResults(results);

        when(pokemonApiRestClient.getAll()).thenReturn(responseApi);

        List<PokemonListResponse> responseList = pokemonService.getAll("desc");

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Ivysaur", responseList.get(0).getName());
        assertEquals(2, responseList.get(0).getId());
        assertEquals("Bulbasaur", responseList.get(1).getName());
        assertEquals(1, responseList.get(1).getId());
    }

    @Test
    void testGetAllSuccessOrderAsc() {
        PokemonApiListResponseDTO responseApi = new PokemonApiListResponseDTO();
        List<PokemonApiListResultDTO> results = Arrays.asList(
                new PokemonApiListResultDTO("Bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                new PokemonApiListResultDTO("Ivysaur", "https://pokeapi.co/api/v2/pokemon/2/")
        );
        responseApi.setResults(results);

        when(pokemonApiRestClient.getAll()).thenReturn(responseApi);

        List<PokemonListResponse> responseList = pokemonService.getAll("asc");

        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Bulbasaur", responseList.get(0).getName());
        assertEquals(1, responseList.get(0).getId());
        assertEquals("Ivysaur", responseList.get(1).getName());
        assertEquals(2, responseList.get(1).getId());
    }

    @Test
    void testGetAllError() {
        when(pokemonApiRestClient.getAll()).thenThrow(new RuntimeException("API Error"));

        assertThrows(RuntimeException.class, () -> pokemonService.getAll(null));
    }

    @Test
    void testGetFavorites() {
        Favorite favorite = new Favorite();
        favorite.setId(1);
        Favorite favorite2 = new Favorite();
        favorite2.setId(2);

        List<Favorite> favorites = Arrays.asList(
                favorite,
                favorite2
        );
        when(favoriteRepository.findAll()).thenReturn(favorites);

        PokemonDTO pokemon1DTO = new PokemonDTO();
        pokemon1DTO.setName("Bulbasaur");
        pokemon1DTO.setAbilities(Collections.emptyList());
        pokemon1DTO.setTypes(Collections.emptyList());

        PokemonDTO pokemon2DTO = new PokemonDTO();
        pokemon2DTO.setName("Ivysaur");
        pokemon2DTO.setAbilities(Collections.emptyList());
        pokemon2DTO.setTypes(Collections.emptyList());

        when(pokemonApiRestClient.getDetails(1)).thenReturn(pokemon1DTO);
        when(pokemonApiRestClient.getDetails(2)).thenReturn(pokemon2DTO);

        PokemonDetailsListResponse response = pokemonService.getFavorites(null);

        assertNotNull(response);
        assertEquals(2, response.getResults().size());
        assertEquals("Bulbasaur", response.getResults().get(0).getName());
        assertEquals("Ivysaur", response.getResults().get(1).getName());
    }

    @Test
    void testGetFavoritesErrorEmptyList() {
        when(favoriteRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(PokemonNotFoundException.class, () -> pokemonService.getFavorites(null));
    }

    @Test
    void testGetFavoritesErrorApi() {
        Favorite favorite = new Favorite();
        favorite.setId(1);
       List<Favorite> favorites = Arrays.asList(
                favorite
        );
        when(favoriteRepository.findAll()).thenReturn(favorites);

        when(pokemonApiRestClient.getDetails(1)).thenThrow(new RuntimeException("API Error"));

        assertThrows(PokeApiConnectionException.class, () -> pokemonService.getFavorites(null));
    
    }

    @Test
    void testGetFavoritesOrderByDesc() {
        Favorite favorite = new Favorite();
        favorite.setId(1);
        Favorite favorite2 = new Favorite();
        favorite2.setId(2);

        List<Favorite> favorites = Arrays.asList(
                favorite,
                favorite2
        );
        when(favoriteRepository.findAll()).thenReturn(favorites);

        PokemonDTO pokemon1DTO = new PokemonDTO();
        pokemon1DTO.setName("Bulbasaur");
        pokemon1DTO.setAbilities(Collections.emptyList());
        pokemon1DTO.setTypes(Collections.emptyList());

        PokemonDTO pokemon2DTO = new PokemonDTO();
        pokemon2DTO.setName("Ivysaur");
        pokemon2DTO.setAbilities(Collections.emptyList());
        pokemon2DTO.setTypes(Collections.emptyList());

        when(pokemonApiRestClient.getDetails(1)).thenReturn(pokemon1DTO);
        when(pokemonApiRestClient.getDetails(2)).thenReturn(pokemon2DTO);

        PokemonDetailsListResponse response = pokemonService.getFavorites("desc");

        assertNotNull(response);
        assertEquals(2, response.getResults().size());
        assertEquals("Ivysaur", response.getResults().get(0).getName());
        assertEquals("Bulbasaur", response.getResults().get(1).getName());
    }

    @Test
    void testGetFavoritesOrderByAsc() {
        Favorite favorite = new Favorite();
        favorite.setId(1);
        Favorite favorite2 = new Favorite();
        favorite2.setId(2);

        List<Favorite> favorites = Arrays.asList(
                favorite,
                favorite2
        );
        when(favoriteRepository.findAll()).thenReturn(favorites);

        PokemonDTO pokemon1DTO = new PokemonDTO();
        pokemon1DTO.setName("Bulbasaur");
        pokemon1DTO.setAbilities(Collections.emptyList());
        pokemon1DTO.setTypes(Collections.emptyList());

        PokemonDTO pokemon2DTO = new PokemonDTO();
        pokemon2DTO.setName("Ivysaur");
        pokemon2DTO.setAbilities(Collections.emptyList());
        pokemon2DTO.setTypes(Collections.emptyList());

        when(pokemonApiRestClient.getDetails(1)).thenReturn(pokemon1DTO);
        when(pokemonApiRestClient.getDetails(2)).thenReturn(pokemon2DTO);

        PokemonDetailsListResponse response = pokemonService.getFavorites("asc");

        assertNotNull(response);
        assertEquals(2, response.getResults().size());
        assertEquals("Bulbasaur", response.getResults().get(0).getName());
        assertEquals("Ivysaur", response.getResults().get(1).getName());
    }

    @Test
    void testSaveFavorite() {
        FavoritePokemonRequest request = new FavoritePokemonRequest(Arrays.asList(1, 2));
        List<PokemonListResponse> allPokemonList = Arrays.asList(
                new PokemonListResponse("Bulbasaur",1),
                new PokemonListResponse("Ivysaur",2)
        );
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        byte[] serializedList = serializer.serialize(allPokemonList);

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(serializedList);
        when(favoriteRepository.existsById(1)).thenReturn(false);
        when(favoriteRepository.existsById(2)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(null); // void method, so just configure

        pokemonService.saveFavorite(request);

        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
        verify(pokemonApiRestClient, never()).getAll();
        verify(favoriteRepository, times(1)).save(argThat(favorite -> favorite.getId() == 1 && favorite.getName().equals("Bulbasaur")));
        verify(favoriteRepository, times(1)).save(argThat(favorite -> favorite.getId() == 2 && favorite.getName().equals("Ivysaur")));
    }

    
}
