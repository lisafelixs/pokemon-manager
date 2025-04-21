package com.github.lisafelixs.pokemon_manager.exception;

public class PokeApiConnectionException extends RuntimeException{

    public PokeApiConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PokeApiConnectionException(String message) {
        super(message);
    }

}
