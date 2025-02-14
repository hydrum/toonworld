CREATE TABLE `discord_guilds` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`swgoh_guild_id` VARCHAR(255) NOT NULL,
	`discord_guild_id` BIGINT NOT NULL,
	`journey_progress_channel_id` BIGINT NULL DEFAULT NULL,

	PRIMARY KEY (`id`),
	INDEX `IDX_discord_guilds_swgoh_guild_id` (`swgoh_guild_id`)
);