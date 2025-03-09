CREATE TABLE `farms` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`name` VARCHAR(255) NOT NULL,
	`team_size` INT NOT NULL,

	`owner_discord_guild_id` BIGINT NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_farms_owner_discord_guild_id` (`owner_discord_guild_id`),
	CONSTRAINT `FK_farms_owner_discord_guild_id` FOREIGN KEY (`owner_discord_guild_id`) REFERENCES `discord_guilds` (`id`)
);

CREATE TABLE `farms_units` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`base_id` VARCHAR(255) NOT NULL,
	`is_required` BIT NOT NULL,

	`min_rarity` INT NOT NULL,
	`min_gear_level` INT NOT NULL,
	`min_relic_tier` ENUM('NONE', 'LOCKED', 'TIER_0', 'TIER_1', 'TIER_2', 'TIER_3', 'TIER_4', 'TIER_5', 'TIER_6', 'TIER_7', 'TIER_8', 'TIER_9', 'TIER_10') NOT NULL,
	`min_stat_speed` BIGINT NULL DEFAULT NULL,
	`min_stat_health` BIGINT NULL DEFAULT NULL,
	`min_stat_protection` BIGINT NULL DEFAULT NULL,
	`min_stat_offense` BIGINT NULL DEFAULT NULL,

	`farm_id` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_farms_units_farm_id` (`farm_id`),
	CONSTRAINT `FK_farms_units_farm_id` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`),
	INDEX `IDX_farms_units_base_id` (`base_id`)
);

CREATE TABLE `discord_guilds_farm_roles` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`discord_guild_id` BIGINT NOT NULL,
	`discord_role_id` BIGINT NOT NULL,

	`farm_id` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDK_discord_guilds_farm_roles_parent` (`discord_guild_id`),
	CONSTRAINT `FK_discord_guilds_farm_roles_parent` FOREIGN KEY (`discord_guild_id`) REFERENCES `discord_guilds` (`id`),
    INDEX `IDX_discord_guilds_farm_roles_farm_id` (`farm_id`),
    CONSTRAINT `FK_discord_guilds_farm_roles_farm_id` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
);

ALTER TABLE `discord_guilds` ADD COLUMN IF NOT EXISTS `officer_info_channel_id` BIGINT;