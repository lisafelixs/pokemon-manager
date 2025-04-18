package com.github.lisafelixs.pokemon_manager.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.lisafelixs.pokemon_manager.db.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

}
