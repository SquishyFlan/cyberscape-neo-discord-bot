package com.titaniumtemplar.discordbot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @RequestMapping("/")
    String index() {
        return "Welcome to Cyberscape Neo!";
    }

}
