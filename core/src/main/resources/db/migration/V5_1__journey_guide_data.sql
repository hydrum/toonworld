-- add unlock base id to farms
ALTER TABLE `farms`
    ADD COLUMN IF NOT EXISTS `unlock_base_id` VARCHAR(255) NULL;

-- SUPREME LEADER KYLO REN
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('SLKR Journey Guide', 13, NULL, 'SUPREMELEADERKYLOREN');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('KYLORENUNMASKED', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FIRSTORDEROFFICERMALE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FIRSTORDERTROOPER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('KYLOREN', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('PHASMA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FIRSTORDEREXECUTIONER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('SMUGGLERHAN', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FOSITHTROOPER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FIRSTORDERSPECIALFORCESPILOT', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('GENERALHUX', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('FIRSTORDERTIEPILOT', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('EMPERORPALPATINE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN')),
       ('CAPITALFINALIZER', 1, 5, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'SUPREMELEADERKYLOREN'));

-- REY
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Rey Journey Guide', 13, NULL, 'GLREY');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('REYJEDITRAINING', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('FINN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('RESISTANCETROOPER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('REY', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('POE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('RESISTANCEPILOT', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('EPIXFINN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('AMILYNHOLDO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('ROSETICO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('EPIXPOE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('BB8', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('SMUGGLERCHEWBACCA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY')),
       ('CAPITALRADDUS', 1, 5, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'GLREY'));

-- JEDI KNIGHT LUKE SKYWALKER
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('JKL Journey Guide', 11, NULL, 'JEDIKNIGHTLUKE');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('C3POLEGENDARY', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('VADER', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('CHEWBACCALEGENDARY', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('WAMPA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('HOTHLEIA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('ADMINISTRATORLANDO', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('HOTHHAN', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('COMMANDERLUKESKYWALKER', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('HERMITYODA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('MILLENNIUMFALCON', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE')),
       ('XWINGRED2', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTLUKE'));

-- JEDI MASTER LUKE SKYWALKER
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('JML Journey Guide', 15, NULL, 'GRANDMASTERLUKE');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('MONMOTHMA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('C3POCHEWBACCA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('OLDBENKENOBI', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('REYJEDITRAINING', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('C3POLEGENDARY', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('R2D2_LEGENDARY', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('CHEWBACCALEGENDARY', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('HANSOLO', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('JEDIKNIGHTLUKE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('PRINCESSLEIA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('HERMITYODA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('WEDGEANTILLES', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('BIGGSDARKLIGHTER', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('ADMINISTRATORLANDO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE')),
       ('YWINGREBEL', 1, 6, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDMASTERLUKE'));

-- SITH ETERNAL EMPEROR
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('SEE Journey Guide', 15, NULL, 'SITHPALPATINE');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('ADMIRALPIETT', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('ROYALGUARD', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('VADER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('EMPERORPALPATINE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('DIRECTORKRENNIC', 1, 7, 13, 'TIER_4', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('COUNTDOOKU', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('SITHMARAUDER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('DARTHSIDIOUS', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('MAUL', 1, 7, 13, 'TIER_4', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('ANAKINKNIGHT', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('GRANDADMIRALTHRAWN', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('GRANDMOFFTARKIN', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('VEERS', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('COLONELSTARCK', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE')),
       ('TIEBOMBERIMPERIAL', 1, 6, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'SITHPALPATINE'));

-- JEDI MASTER KENOBI
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('JMK Journey Guide', 15, NULL, 'JEDIMASTERKENOBI');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('AAYLASECURA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('GENERALKENOBI', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('MACEWINDU', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('BOKATAN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('QUIGONJINN', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('MAGNAGUARD', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('CLONESERGEANTPHASEI', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('WATTAMBOR', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('GRIEVOUS', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('CADBANE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('CC2224', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('JANGOFETT', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('SHAAKTI', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('GRANDMASTERYODA', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI')),
       ('CAPITALNEGOTIATOR', 1, 6, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERKENOBI'));

-- LORD VADER
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('LV Journey Guide', 15, NULL, 'LORDVADER');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('BADBATCHHUNTER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('BADBATCHWRECKER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('BADBATCHTECH', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('TUSKENRAIDER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('PADMEAMIDALA', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('GENERALSKYWALKER', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('EMBO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('CT210408', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('BADBATCHECHO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('COUNTDOOKU', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('ZAMWESELL', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('NUTEGUNRAY', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('ARCTROOPER501ST', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('GRANDMOFFTARKIN', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER')),
       ('YWINGCLONEWARS', 1, 6, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'LORDVADER'));

-- STARKILLER
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('SK Journey Guide', 4, NULL, 'STARKILLER');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('DASHRENDAR', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'STARKILLER')),
       ('KYLEKATARN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'STARKILLER')),
       ('DARTHTALON', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'STARKILLER')),
       ('MARAJADE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'STARKILLER'));

-- GRAND INQUISITOR
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('GI Journey Guide', 5, NULL, 'GRANDINQUISITOR');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('SECONDSISTER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDINQUISITOR')),
       ('NINTHSISTER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDINQUISITOR')),
       ('SEVENTHSISTER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDINQUISITOR')),
       ('EIGHTHBROTHER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDINQUISITOR')),
       ('FIFTHBROTHER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GRANDINQUISITOR'));

-- JABBA THE HUTT
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Jabba Journey Guide', 15, NULL, 'JABBATHEHUTT');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('HANSOLO', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('GAMORREANGUARD', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('GREEDO', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('KRRSANTAN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('UNDERCOVERLANDO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('JEDIKNIGHTLUKE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('JAWA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('URORRURRR', 1, 7, 13, 'TIER_4', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('C3POLEGENDARY', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('BOUSHH', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('AURRA_SING', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('FENNECSHAND', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('BOBAFETT', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('HUMANTHUG', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT')),
       ('OUTRIDER', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'JABBATHEHUTT'));

-- EXECUTOR
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Executor Journey Guide', 14, NULL, 'CAPITALEXECUTOR');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('VADER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('ADMIRALPIETT', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('BOBAFETT', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('TIEFIGHTERPILOT', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('BOSSK', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('IG88', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('DENGAR', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('TIEBOMBERIMPERIAL', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('TIEADVANCED', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('TIEFIGHTERIMPERIAL', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('RAZORCREST', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('IG2000', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('SLAVE1', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR')),
       ('HOUNDSTOOTH', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALEXECUTOR'));

-- PROFUNDITY
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Profundity Journey Guide', 14, NULL, 'CAPITALPROFUNDITY');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('ADMIRALRADDUS', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('CASSIANANDOR', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('DASHRENDAR', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('MONMOTHMA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('BISTAN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('JYNERSO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('HERASYNDULLAS3', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('OUTRIDER', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('UWINGROGUEONE', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('UWINGSCARIF', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('XWINGRED2', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('XWINGRED3', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('YWINGREBEL', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY')),
       ('GHOST', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALPROFUNDITY'));

-- DOCTOR APHRA
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Aphra Journey Guide', 4, NULL, 'DOCTORAPHRA');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('TRIPLEZERO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'DOCTORAPHRA')),
       ('BT1', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'DOCTORAPHRA')),
       ('HONDO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'DOCTORAPHRA')),
       ('SANASTARROS', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'DOCTORAPHRA'));

-- JEDI KNIGHT CAL KESTIS
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('JKCK Journey Guide', 5, NULL, 'JEDIKNIGHTCAL');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('CALKESTIS', 1, 7, 12, 'LOCKED', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTCAL')),
       ('CEREJUNDA', 1, 7, 12, 'LOCKED', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTCAL')),
       ('MERRIN', 1, 7, 12, 'LOCKED', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTCAL')),
       ('TARFFUL', 1, 7, 12, 'LOCKED', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTCAL')),
       ('SAWGERRERA', 1, 7, 12, 'LOCKED', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIKNIGHTCAL'));

-- LEVIATHAN
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Leviathan Journey Guide', 16, NULL, 'CAPITALLEVIATHAN');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('DARTHREVAN', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('DARTHMALAK', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHTROOPER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('FOSITHTROOPER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('MAUL', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('HK47', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('BASTILASHAN', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHASSASSIN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('50RT', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHBOMBER', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHSUPREMACYCLASS', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('TIEDAGGER', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHINFILTRATOR', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('SITHFIGHTER', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('FURYCLASSINTERCEPTOR', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN')),
       ('EBONHAWK', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'CAPITALLEVIATHAN'));

-- LEIA ORGANA
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('GL Leia Journey Guide', 15, NULL, 'GLLEIA');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('CAPTAINREX', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('PRINCESSKNEESAA', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('WICKET', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('ADMINISTRATORLANDO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('ADMIRALACKBAR', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('SCOUTTROOPER_V3', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('R2D2_LEGENDARY', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('HOTHHAN', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('HOTHLEIA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('CHIEFCHIRPA', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('CAPTAINDROGAN', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('COMMANDERLUKESKYWALKER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('BOUSHH', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('C3POCHEWBACCA', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA')),
       ('LOBOT', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLLEIA'));

-- BO-KATAN (MANDALORE)
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('BKM Journey Guide', 4, NULL, 'MANDALORBOKATAN');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('THEMANDALORIANBESKARARMOR', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'MANDALORBOKATAN')),
       ('PAZVIZSLA', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'MANDALORBOKATAN')),
       ('IG12', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'MANDALORBOKATAN')),
       ('KELLERANBEQ', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'MANDALORBOKATAN'));

-- JAR JAR BINKS
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Jar Jar Journey Guide', 4, NULL, 'JARJARBINKS');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('BOSSNASS', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JARJARBINKS')),
       ('CAPTAINTARPALS', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JARJARBINKS')),
       ('BOOMADIER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JARJARBINKS')),
       ('GUNGANPHALANX', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JARJARBINKS'));

-- AHSOKA TANO
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('GLAT Journey Guide', 15, NULL, 'GLAHSOKATANO');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('AHSOKATANO', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('GENERALSKYWALKER', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('ASAJVENTRESS', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('NIGHTTROOPER', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('COMMANDERAHSOKA', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('CAPTAINENOCH', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('DEATHTROOPERPERIDEA', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('EZRABRIDGERS3', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('FULCRUMAHSOKA', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('CT7567', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('BARRISSOFFEE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('PADAWANSABINE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('HUYANG', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('GENERALSYNDULLA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO')),
       ('JEDISTARFIGHTERAHSOKATANO', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'GLAHSOKATANO'));

-- BAYLAN SKOLL
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Baylan Journey Guide', 5, NULL, 'BAYLANSKOLL');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('GRANDADMIRALTHRAWN', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'BAYLANSKOLL')),
       ('MORGANELSBETH', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'BAYLANSKOLL')),
       ('GREATMOTHERS', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'BAYLANSKOLL')),
       ('SHINHATI', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'BAYLANSKOLL')),
       ('MARROK', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'BAYLANSKOLL'));

-- JEDI MASTER MACE WINDU
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('Jedi Master Windu Journey Guide', 6, NULL, 'JEDIMASTERMACEWINDU');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('APPO', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU')),
       ('OPERATIVE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU')),
       ('DISGUISEDCLONETROOPER', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU')),
       ('SCORCH', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU')),
       ('VANGUARDTEMPLEGUARD', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU')),
       ('DEPABILLABA', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'JEDIMASTERMACEWINDU'));

-- HONDO OHNAKA
INSERT INTO farms (name, team_size, owner_discord_guild_id, unlock_base_id)
VALUES ('GL Hondo Journey Guide', 16, NULL, 'GLHONDO');
INSERT INTO farms_units (base_id, is_required, min_rarity, min_gear_level, min_relic_tier, farm_id)
VALUES ('ITHANO', 1, 7, 13, 'TIER_3', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('QUIGGOLD', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('KIX', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('BRUTUS', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('VANE', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('CAPTAINSILVO', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('HONDO', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('SMUGGLERCHEWBACCA', 1, 7, 13, 'TIER_7', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('EZRABRIDGERS3', 1, 7, 13, 'TIER_6', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('GENERALSKYWALKER', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('REY', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('GENERALKENOBI', 1, 7, 13, 'TIER_9', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('CHIRRUTIMWE', 1, 7, 13, 'TIER_5', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('COUNTDOOKU', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('BOSSK', 1, 7, 13, 'TIER_8', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO')),
       ('MILLENNIUMFALCONEP7', 1, 7, 0, 'NONE', (SELECT id FROM farms WHERE unlock_base_id = 'GLHONDO'));