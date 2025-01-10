CREATE SEQUENCE IF NOT EXISTS players_seq AS BIGINT START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS players_units_seq AS BIGINT START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS players_units_abilities_seq AS BIGINT START WITH 1 INCREMENT BY 50;

SET system_versioning_alter_history = KEEP;

ALTER TABLE `players_units` ADD UNIQUE INDEX IF NOT EXISTS `UNIQUE_players_units_player_id_base_id` (`player_id`, `base_id`);
ALTER TABLE `players_units_abilities` ADD UNIQUE INDEX IF NOT EXISTS `UNIQUE_players_units_abilities_player_unit_id_base_id` (`player_unit_id`, `base_id`);

SET system_versioning_alter_history = ERROR;

ALTER TABLE `discord_players` ADD COLUMN IF NOT EXISTS `swgoh_player_id` VARCHAR(255);