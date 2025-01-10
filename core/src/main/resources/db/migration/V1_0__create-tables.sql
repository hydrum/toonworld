CREATE TABLE `players` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`ally_code` VARCHAR(255) NOT NULL,
	`update_time` DATETIME NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`level` INT NOT NULL,
	`galactic_power` INT NOT NULL,
	`galactic_war_won` INT NOT NULL,
	`gac_division` INT NULL DEFAULT NULL,
	`gac_skill_rating` INT NULL DEFAULT NULL,
	`guild_tokens_earned` INT NOT NULL,
	`gac_league` ENUM('AURODIUM','BRONZIUM','CARBONITE','CHROMIUM','KYBER') NULL DEFAULT NULL,
	`fleet_arena_rank` INT NULL DEFAULT NULL,
	`squad_arena_rank` INT NULL DEFAULT NULL,
	`activity_time` DATETIME NOT NULL,
	`reset_time` TIME NOT NULL,
	`guild_id` VARCHAR(255) NULL DEFAULT NULL,
	`guild_name` VARCHAR(255) NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_players_ally_code` (`ally_code`),
	INDEX `IDX_players_guild_id` (`guild_id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `players_units` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`player_id` BIGINT NOT NULL,
	`base_id` VARCHAR(255) NOT NULL,
	`gear_level` INT NOT NULL,
	`has_ultimate` BIT NOT NULL,
	`level` INT NOT NULL,
	`rarity` INT NOT NULL,
	`relic_tier` ENUM('NONE', 'LOCKED', 'TIER_0', 'TIER_1', 'TIER_2', 'TIER_3', 'TIER_4', 'TIER_5', 'TIER_6', 'TIER_7', 'TIER_8', 'TIER_9', 'TIER_10') NOT NULL,
	`zetas` INT NOT NULL,
	`omicrons` INT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_players_units_player_id` (`player_id`),
	CONSTRAINT `FK_players_units_player` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `sync_players` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`ally_code` VARCHAR(255) NOT NULL,
	`gac_sync_enabled` BIT NOT NULL,
	`player_sync_enabled` BIT NOT NULL,
	`last_success_sync` DATETIME NULL DEFAULT NULL,
	`next_sync` DATETIME NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_sync_players_ally_code` (`ally_code`),
    INDEX `IDX_sync_players_next_sync_enabled` (`player_sync_enabled`, `next_sync`)
);