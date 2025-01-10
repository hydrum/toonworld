CREATE TABLE `guilds` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`swgoh_guild_id` VARCHAR(255) NOT NULL,
	`update_time` DATETIME NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`banner_logo_id` VARCHAR(255) NOT NULL,
	`banner_color_id` VARCHAR(255) NOT NULL,
	`member_count` INT NOT NULL,
	`galactic_power` BIGINT NOT NULL,
	`next_reset_time` DATETIME NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_guilds_swgoh_guild_id` (`swgoh_guild_id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `guilds_members` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`guild_id` BIGINT NOT NULL,
	`swgoh_player_id` VARCHAR(255) NOT NULL,
	`name` VARCHAR(255) NOT NULL,
	`member_level` ENUM('PENDING', 'MEMBER', 'OFFICER', 'LEADER') NOT NULL,
	`join_time` DATETIME NOT NULL,
	`galactic_power` BIGINT NOT NULL,
	`last_activity_time` DATETIME NOT NULL,
	`raid_tickets_total` BIGINT NOT NULL,
	`guild_tokens_total` BIGINT NOT NULL,
	`donations_total` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_guilds_members_guild_id` (`guild_id`),
	INDEX `IDX_guilds_members_swgoh_player_id` (`swgoh_player_id`),
	CONSTRAINT `FK_guilds_members_guild_id` FOREIGN KEY (`guild_id`) REFERENCES `guilds` (`id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `guilds_raids` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`guild_id` BIGINT NOT NULL,
	`raid_id` VARCHAR(255) NOT NULL,
	`end_time` DATETIME NOT NULL,
	`score_total` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_guilds_raids_guild_id` (`guild_id`),
	CONSTRAINT `FK_guilds_raids_guild_id` FOREIGN KEY (`guild_id`) REFERENCES `guilds` (`id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `guilds_raids_members` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`guild_id` BIGINT NOT NULL,
	`guild_raid_id` BIGINT NOT NULL,
	`guild_member_id` BIGINT NULL,
	`swgoh_player_id` VARCHAR(255) NOT NULL,
	`score` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_guilds_raids_members_guild_id` (`guild_id`),
	INDEX `IDX_guilds_raids_members_guild_raid_id` (`guild_raid_id`),
	INDEX `IDX_guilds_raids_members_guild_member_id` (`guild_member_id`),
	INDEX `IDX_guilds_raids_members_swgoh_player_id` (`swgoh_player_id`),
	CONSTRAINT `FK_guilds_raids_members_guild_id` FOREIGN KEY (`guild_id`) REFERENCES `guilds` (`id`),
	CONSTRAINT `FK_guilds_raids_members_guild_raid_id` FOREIGN KEY (`guild_raid_id`) REFERENCES `guilds_raids` (`id`),
	CONSTRAINT `FK_guilds_raids_members_guild_member_id` FOREIGN KEY (`guild_member_id`) REFERENCES `guilds_members` (`id`)
) WITH SYSTEM VERSIONING;

CREATE TABLE `sync_guilds` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`swgoh_guild_id` VARCHAR(255) NOT NULL,

	`stats_sync_enabled` BIT NOT NULL,
	`player_sync_enabled` BIT NOT NULL,

	`last_success_sync` DATETIME NULL DEFAULT NULL,
	`next_sync` DATETIME NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_sync_guilds_swgoh_guild_id` (`swgoh_guild_id`),
    INDEX `IDX_sync_guilds_next_sync_enabled` (`stats_sync_enabled`, `next_sync`)
);