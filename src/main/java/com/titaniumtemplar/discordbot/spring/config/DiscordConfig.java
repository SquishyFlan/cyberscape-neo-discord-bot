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

@Configuration
public class DiscordConfig {

  @Value("${discord.token}")
  String botToken;

  @Value("${game.baseUrl}")
  String baseUrl;

  @Bean
  JDA jda(Myra myra) throws Exception {
    return new JDABuilder(BOT)
	    .setToken(botToken)
	    .addEventListener(myra)
	    .build();
  }

  @Bean
  Myra myra(CyberscapeService service) {
    ScheduledExecutorService combatThreadPool = newScheduledThreadPool(
        1,
        (f) -> new Thread(f, "CombatThread"));

    return new Myra(service, combatThreadPool, baseUrl);
  }
}
