CREATE TABLE character (
  id UUID NOT NULL PRIMARY KEY,
  user_id VARCHAR NOT NULL,
  level INT NOT NULL,
  xp INT NOT NULL,
  hp_current INT NOT NULL,
  mp_current INT NOT NULL,
  fire INT NOT NULL,
  fire_spec1_name VARCHAR NOT NULL,
  fire_spec1_value INT NOT NULL,
  fire_spec2_name VARCHAR NOT NULL,
  fire_spec2_value INT NOT NULL,
  water INT NOT NULL,
  water_spec1_name VARCHAR NOT NULL,
  water_spec1_value INT NOT NULL,
  water_spec2_name VARCHAR NOT NULL,
  water_spec2_value INT NOT NULL,
  lightning INT NOT NULL,
  lightning_spec1_name VARCHAR NOT NULL,
  lightning_spec1_value INT NOT NULL,
  lightning_spec2_name VARCHAR NOT NULL,
  lightning_spec2_value INT NOT NULL,
  wind INT NOT NULL,
  wind_spec1_name VARCHAR NOT NULL,
  wind_spec1_value INT NOT NULL,
  wind_spec2_name VARCHAR NOT NULL,
  wind_spec2_value INT NOT NULL,
  earth INT NOT NULL,
  earth_spec1_name VARCHAR NOT NULL,
  earth_spec1_value INT NOT NULL,
  earth_spec2_name VARCHAR NOT NULL,
  earth_spec2_value INT NOT NULL,
  sonic INT NOT NULL,
  sonic_spec1_name VARCHAR NOT NULL,
  sonic_spec1_value INT NOT NULL,
  sonic_spec2_name VARCHAR NOT NULL,
  sonic_spec2_value INT NOT NULL,
  personal INT NOT NULL,
  personal_spec1_name VARCHAR NOT NULL,
  personal_spec1_value INT NOT NULL,
  personal_spec2_name VARCHAR NOT NULL,
  personal_spec2_value INT NOT NULL,
  material INT NOT NULL,
  material_spec1_name VARCHAR NOT NULL,
  material_spec1_value INT NOT NULL,
  material_spec2_name VARCHAR NOT NULL,
  material_spec2_value INT NOT NULL,
  shift INT NOT NULL,
  shift_spec1_name VARCHAR NOT NULL,
  shift_spec1_value INT NOT NULL,
  shift_spec2_name VARCHAR NOT NULL,
  shift_spec2_value INT NOT NULL,
  life INT NOT NULL,
  life_spec1_name VARCHAR NOT NULL,
  life_spec1_value INT NOT NULL,
  life_spec2_name VARCHAR NOT NULL,
  life_spec2_value INT NOT NULL,
  space INT NOT NULL,
  space_spec1_name VARCHAR NOT NULL,
  space_spec1_value INT NOT NULL,
  space_spec2_name VARCHAR NOT NULL,
  space_spec2_value INT NOT NULL,
  gravity INT NOT NULL,
  gravity_spec1_name VARCHAR NOT NULL,
  gravity_spec1_value INT NOT NULL,
  gravity_spec2_name VARCHAR NOT NULL,
  gravity_spec2_value INT NOT NULL
);

CREATE INDEX ON character (user_id);

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

GRANT SELECT,INSERT,UPDATE,DELETE ON character TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON skill_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON stat_level_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON stat_skill_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON vital_scale TO cyberscape;
GRANT SELECT,INSERT,UPDATE,DELETE ON monster TO cyberscape;
