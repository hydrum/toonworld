CREATE TABLE `players_units_mods` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`player_unit_id` BIGINT NOT NULL,
	`player_id` BIGINT NOT NULL,

	`slot` ENUM('DEFAULT','UNKNOWN','SQUARE','ARROW','DIAMOND','TRIANGLE','CIRCLE','CROSS') NOT NULL,
	`level` INT NOT NULL,
	`rarity` INT NOT NULL,
	`tier` ENUM('DEFAULT','E','D','C','B','A') NOT NULL,

	`mod_set` VARCHAR(255) NOT NULL,

	`primary_stat` VARCHAR(255) NOT NULL,
	`secondary_1_stat` VARCHAR(255) NULL DEFAULT NULL,
	`secondary_2_stat` VARCHAR(255) NULL DEFAULT NULL,
	`secondary_3_stat` VARCHAR(255) NULL DEFAULT NULL,
	`secondary_4_stat` VARCHAR(255) NULL DEFAULT NULL,

	`primary_value` BIGINT NOT NULL,
	`secondary_1_value` BIGINT NULL DEFAULT NULL,
	`secondary_2_value` BIGINT NULL DEFAULT NULL,
	`secondary_3_value` BIGINT NULL DEFAULT NULL,
	`secondary_4_value` BIGINT NULL DEFAULT NULL,

	`secondary_1_roll` INT NULL DEFAULT NULL,
	`secondary_2_roll` INT NULL DEFAULT NULL,
	`secondary_3_roll` INT NULL DEFAULT NULL,
	`secondary_4_roll` INT NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_players_units_mods_player_unit_id` (`player_unit_id`),
	CONSTRAINT `FK_players_units_mods_player_unit_id` FOREIGN KEY (`player_unit_id`) REFERENCES `players_units` (`id`),
	INDEX `IDX_players_units_mods_player_id` (`player_id`)
) WITH SYSTEM VERSIONING;

CREATE SEQUENCE IF NOT EXISTS players_units_mods_seq AS BIGINT START WITH 1 INCREMENT BY 50;