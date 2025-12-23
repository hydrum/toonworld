-- remove constraints
ALTER TABLE discord_guilds_farm_roles
    DROP FOREIGN KEY FK_discord_guilds_farm_roles_farm_id;
ALTER TABLE discord_guilds_farm_roles
    DROP FOREIGN KEY FK_discord_guilds_farm_roles_parent;

-- create new table
CREATE TABLE `discord_guilds_farms`
(
    `id`                  BIGINT NOT NULL AUTO_INCREMENT,

    `discord_guild_id`    BIGINT NOT NULL,

    `discord_role_id`     BIGINT NULL,
    `announce_channel_id` BIGINT NULL,

    `farm_id`             BIGINT NOT NULL,

    PRIMARY KEY (`id`),
    INDEX `IDX_discord_guilds_farms_parent` (`discord_guild_id`),
    CONSTRAINT `FK_discord_guilds_farms_parent` FOREIGN KEY (`discord_guild_id`) REFERENCES `discord_guilds` (`id`),
    INDEX `IDX_discord_guilds_farms_farm_id` (`farm_id`),
    CONSTRAINT `FK_discord_guilds_farms_farm_id` FOREIGN KEY (`farm_id`) REFERENCES `farms` (`id`)
);

-- migrate data
INSERT INTO `discord_guilds_farms` (`discord_guild_id`, `discord_role_id`, `announce_channel_id`, `farm_id`)
SELECT d.discord_guild_id,
       d.discord_role_id,
       (SELECT dg.journey_progress_channel_id
        FROM discord_guilds dg
        WHERE dg.id = d.discord_guild_id),
       d.farm_id
FROM discord_guilds_farm_roles d;

-- drop old table
DROP TABLE `discord_guilds_farm_roles`;

-- adjust guild table
ALTER TABLE `discord_guilds`
    DROP COLUMN IF EXISTS `officer_info_channel_id`;
ALTER TABLE `discord_guilds`
    DROP COLUMN IF EXISTS `journey_progress_channel_id`;
