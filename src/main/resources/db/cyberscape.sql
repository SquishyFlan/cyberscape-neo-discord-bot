CREATE TABLE character (
  id UUID NOT NULL PRIMARY KEY,
  user_id VARCHAR NOT NULL,
  name VARCHAR NOT NULL,
  level INT NOT NULL,
  xp INT NOT NULL,
  hp_current INT NOT NULL,
  mp_current INT NOT NULL
);

CREATE UNIQUE INDEX ON character (user_id);

CREATE TYPE skill_type AS ENUM (
  'fire',
  'water',
  'lightning',
  'wind',
  'earth',
  'sonic',
  'personal',
  'material',
  'shift',
  'life',
  'space',
  'gravity'
);

CREATE TYPE stat_type AS ENUM (
  'str',
  'vit',
  'spd',
  'dex',
  'int',
  'wis'
);

CREATE TABLE character_skill (
  character_id UUID NOT NULL,
  skill skill_type NOT NULL,
  ranks INT NOT NULL,
  spec1_name VARCHAR NOT NULL,
  spec1_ranks INT NOT NULL,
  spec2_name VARCHAR NOT NULL,
  spec2_ranks INT NOT NULL,
  PRIMARY KEY (character_id, skill)
);

CREATE TABLE skill_scale (
  spec1_start INT NOT NULL,
  spec2_start INT NOT NULL,
  sp_per_level INT NOT NULL,
  sp_per_level_gap INT NOT NULL,
  sp_inc_amount INT NOT NULL,
  sp_cost_per_rank INT NOT NULL,
  sp_cost_per_rank_gap INT NOT NULL,
  sp_cost_inc_amount INT NOT NULL
);

CREATE TABLE stat_level_scale (
  stat stat_type NOT NULL,
  base INT NOT NULL,
  per_level INT NOT NULL,
  per_level_gap INT NOT NULL,
  inc_amount INT NOT NULL
);

CREATE TABLE stat_skill_scale (
  skill skill_type NOT NULL,
  stat stat_type NOT NULL,
  per_rank INT NOT NULL
);

CREATE TABLE vital_scale (
  hp_base INT NOT NULL,
  hp_per_vit INT NOT NULL,
  mp_base INT NOT NULL,
  mp_per_int INT NOT NULL,
  mp_per_wis INT NOT NULL
);

CREATE TABLE monster (
  id UUID NOT NULL PRIMARY KEY,
  name VARCHAR NOT NULL,
  hp INT NOT NULL,
  xp INT NOT NULL
);

CREATE TABLE guild_settings (
  guild_id VARCHAR NOT NULL PRIMARY KEY,
  default_role_id VARCHAR
);

CREATE TABLE guild_combat_channels (
  guild_id VARCHAR NOT NULL,
  channel_id VARCHAR NOT NULL,
  PRIMARY KEY (guild_id, channel_id)
);

GRANT SELECT,INSERT,UPDATE,DELETE ON character TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON character_skill TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON skill_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON stat_level_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON stat_skill_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON vital_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON monster TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON guild_settings TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON guild_combat_channels TO cyberscape;
