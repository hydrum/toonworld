SET system_versioning_alter_history = KEEP;

ALTER TABLE `players` ADD COLUMN IF NOT EXISTS `swgoh_player_id` VARCHAR(255);
CREATE INDEX IF NOT EXISTS `IDX_players_swgoh_player_id` ON `players` (`swgoh_player_id`);

ALTER TABLE `players` DROP INDEX `IDX_players_guild_id`;
ALTER TABLE `players` RENAME COLUMN `guild_id` TO `swgoh_guild_id`;
CREATE INDEX IF NOT EXISTS `IDX_players_swgoh_guild_id` ON `players` (`swgoh_guild_id`);

SET system_versioning_alter_history = ERROR;