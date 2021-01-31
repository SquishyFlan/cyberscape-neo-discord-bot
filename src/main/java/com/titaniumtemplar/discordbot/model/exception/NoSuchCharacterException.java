package com.titaniumtemplar.discordbot.model.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/*
	Class: NoSuchCharacterException
	Description: Handles if a character does not exist
*/
@ResponseStatus(NOT_FOUND)
public class NoSuchCharacterException extends RuntimeException {

}
