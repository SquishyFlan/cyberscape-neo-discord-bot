package com.titaniumtemplar.discordbot.spring.config;

import com.titaniumtemplar.discordbot.discord.Myra;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.dv8tion.jda.core.AccountType.BOT;

@Configuration
public class DiscordConfig {

  @Value("${discord.token}")
  String botToken;

  @Bean
  JDA jda(Myra myra) throws Exception {
    return new JDABuilder(BOT)
	    .setToken(botToken)
	    .addEventListener(myra)
	    .build();
  }

  @Bean
  Myra myra(DSLContext dslContext) {
    return new Myra(dslContext);
  }
}
