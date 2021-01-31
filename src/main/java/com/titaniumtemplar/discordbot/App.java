package com.titaniumtemplar.discordbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
	Class: App
	Description: Utility functions for Discord operations
*/
@SpringBootApplication
public class App {

	/*
		Method: main
		Description: Starts the app up
		Input: Array of Strings containing configurations
	*/
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}