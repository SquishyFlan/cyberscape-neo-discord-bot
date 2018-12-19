package com.titaniumtemplar.discordbot.model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class NoSuchCharacterException extends RuntimeException {

}
