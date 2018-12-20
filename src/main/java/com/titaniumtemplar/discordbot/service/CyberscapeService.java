package com.titaniumtemplar.discordbot.service;

import com.titaniumtemplar.discordbot.model.character.CharStats;
import com.titaniumtemplar.discordbot.model.monster.MonsterTemplate;
import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.repository.CyberscapeRepository;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeService {

  private final CyberscapeRepository repo;

  // Caches
  private StatConfig statConfig;
  private Map<String, CharStats> characterCache = new ConcurrentHashMap<>();
  private EnumeratedDistribution<MonsterTemplate> monsterCache;

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

  public MonsterTemplate getRandomMonster() {
    if (monsterCache == null) {
      monsterCache = new EnumeratedDistribution<>(
	  repo.getMonsters()
	      .stream()
	      .map((monster) -> new Pair<>(monster, 1D / monster.getMaxHp()))
	      .collect(toList()));
    }
    return monsterCache.sample();
  }

  public void awardXp(Collection<String> participantUids, int xp) {
    participantUids.stream()
	.map(this::getCharacter)
	.forEach((character) -> character.setXp(character.getXp() + xp));
    repo.awardXp(participantUids, xp);
  }
}
