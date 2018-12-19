package com.titaniumtemplar.discordbot.service;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.repository.CyberscapeRepository;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeService {

  private final CyberscapeRepository repo;

  // Caches
  private StatConfig statConfig;
  private Map<String, CharStats> characterCache = new HashMap<>();

  public StatConfig getStatConfig() {
    if (statConfig == null) {
      statConfig = repo.getStatConfig();
    }
    return statConfig;
  }

  public CharStats getCharacter(String userId) {
    CharStats character = characterCache.computeIfAbsent(
	userId,
	(uid) -> calcStats(repo.getCharacter(uid)));

    return character;
  }

  public CharStats createCharacter(String userId) {
    CharStats character = calcStats(repo.createCharacter(userId));
    characterCache.put(userId, character);
    return character;
  }

  private CharStats calcStats(CharStats character) {
    StatConfig statConfig = getStatConfig();
    character.calcStats(statConfig);
    return character;
  }
}
