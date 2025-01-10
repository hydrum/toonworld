CREATE TABLE `discord_players` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,

	`ally_code` VARCHAR(255) NOT NULL,
	`discord_user_id` BIGINT NOT NULL,
	`slot` BIGINT NOT NULL,

	PRIMARY KEY (`id`),
	UNIQUE INDEX `UNIQUE_discord_players_ally_code` (`ally_code`),
	UNIQUE INDEX `UNIQUE_discord_players_discord_user_id_slot` (`discord_user_id`, `slot`),
	UNIQUE INDEX `UNIQUE_discord_players_discord_user_id_ally_code` (`discord_user_id`, `ally_code`)
);