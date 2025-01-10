CREATE INDEX IF NOT EXISTS `IDX_players_units_base_id` ON `players_units` (`base_id`);

CREATE TABLE `players_units_abilities` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`player_unit_id` BIGINT NOT NULL,
	`player_id` BIGINT NOT NULL,
	`base_id` VARCHAR(255) NOT NULL,
	`tier` INT NOT NULL,
	`has_omega` BIT NOT NULL,
	`has_zeta` BIT NOT NULL,
	`has_omicron` BIT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_players_units_abilities_player_unit_id` (`player_unit_id`),
	CONSTRAINT `FK_players_units_abilities_player_unit_id` FOREIGN KEY (`player_unit_id`) REFERENCES `players_units` (`id`),
	INDEX `IDX_players_units_abilities_base_id` (`base_id`),
	INDEX `IDX_players_units_abilities_player_id` (`player_id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `units` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`base_id` VARCHAR(255) NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`image` VARCHAR(255) NOT NULL,
	`alignment` ENUM('NEUTRAL', 'LIGHT', 'DARK') NOT NULL,
	`role` VARCHAR(255) NOT NULL,
	`combat_type` ENUM('CHARACTER', 'SHIP') NOT NULL,
	`ship_base_id` VARCHAR(255) NULL,
	`ship_slot` INT NULL,
	`is_capital_ship` BIT NOT NULL,
	`is_galactic_legend` BIT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_units_base_id` (`base_id`),
	INDEX `IDX_units_ship_base_id` (`ship_base_id`)
);

CREATE TABLE `units_categories` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`unit_id` BIGINT NOT NULL,
	`unit_base_id` VARCHAR(255) NOT NULL,
	`category` VARCHAR(255) NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_units_categories_base_id` (`unit_base_id`),
	INDEX `IDX_units_categories_unit_id` (`unit_id`),
	CONSTRAINT `FK_units_categories_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
);

CREATE TABLE `units_abilities` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`unit_id` BIGINT NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`base_id` VARCHAR(255) NOT NULL,
	`type` ENUM('BASIC', 'SPECIAL', 'LEADER', 'UNIQUE', 'SHIP_SPECIAL', 'ULTIMATE') NOT NULL,
	`omicron_mode` ENUM('NONE', 'RAID', 'TB', 'TW', 'GAC', 'CONQUEST', 'GC', 'GAC_3V3', 'GAC_5V5') NOT NULL,
	`tier_max` INT NULL,
	`is_omega` BIT NOT NULL,
	`is_zeta` BIT NOT NULL,
	`is_omicron` BIT NOT NULL,
	`is_ultimate` BIT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_units_abilities_base_id` (`base_id`),
	INDEX `IDX_units_abilities_unit_id` (`unit_id`),
	CONSTRAINT `FK_units_abilities_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
);

CREATE TABLE `units_aliases` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`unit_id` BIGINT NOT NULL,
	`unit_base_id` VARCHAR(255) NOT NULL,
	`alias` VARCHAR(255) NOT NULL,
	`is_primary` BIT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_units_categories_base_id` (`unit_base_id`),
	INDEX `IDX_units_categories_unit_id` (`unit_id`),
	CONSTRAINT `FK_units_aliases_unit_id` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`)
);