SET system_versioning_alter_history = KEEP;

ALTER TABLE `players` MODIFY COLUMN `galactic_power` BIGINT;

SET system_versioning_alter_history = ERROR;