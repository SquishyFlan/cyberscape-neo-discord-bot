package com.titaniumtemplar.discordbot.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RootController {
    @RequestMapping("/")
    String index(Authentication auth) {
        return "Welcome to Cyberscape Neo!";
    }

}
