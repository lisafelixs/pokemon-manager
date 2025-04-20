package com.github.lisafelixs.pokemon_manager.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "favorite")
@Setter
@Getter
public class Favorite {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

}
