package com.titaniumtemplar.discordbot.model.character;

import java.util.UUID;
import lombok.Data;

@Data
public class Character
{
  private UUID id;
  private String userId;
  private String name;
}
