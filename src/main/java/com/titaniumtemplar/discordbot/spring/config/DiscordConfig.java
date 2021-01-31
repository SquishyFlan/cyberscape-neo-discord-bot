package com.titaniumtemplar.discordbot.spring.config;

import com.titaniumtemplar.discordbot.discord.Myra;
import com.titaniumtemplar.discordbot.service.CyberscapeService;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static net.dv8tion.jda.core.AccountType.BOT;

/*
	Class: DiscordConfig
	Description: Utility functions for Discord operations
*/
@Configuration
public class DiscordConfig {

  @Value("${discord.token}")
  String botToken;

  @Value("${game.baseUrl}")
  String baseUrl;

  @Bean
  JDA jda() throws Exception {
    return new JDABuilder(BOT)
	    .setToken(botToken)
	    .build();
  }

	/*
		Method: myra
		Description: Creates a new Myra after connecting services and JDA
		Input: CyberscapeService object, JDA for discord
		Output: New Myra - Devoid of emotions and memories
	*/
  @Bean
  Myra myra(CyberscapeService service, JDA discord) {
    ScheduledExecutorService combatThreadPool = newScheduledThreadPool(
        1,
        (f) -> new Thread(f, "CombatThread"));

    return new Myra(service, discord, combatThreadPool, baseUrl);
  }
}
