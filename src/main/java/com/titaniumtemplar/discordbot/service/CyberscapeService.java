package com.titaniumtemplar.discordbot.service;

import com.titaniumtemplar.discordbot.model.stats.StatConfig;
import com.titaniumtemplar.discordbot.repository.CyberscapeRepository;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class CyberscapeService
{
  private final CyberscapeRepository repo;

  // Caches
  private StatConfig statConfig;

  public StatConfig getStatConfig()
  {
    if (statConfig == null)
    {
      statConfig = repo.getStatConfig();
    }
    return statConfig;
  }
}
