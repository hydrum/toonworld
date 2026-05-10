ALTER TABLE `farms_units`
    MODIFY COLUMN `min_rarity` INT NULL;

UPDATE `farms_units` fu
SET fu.min_rarity = NULL
WHERE fu.farm_id IN (
    SELECT `id` FROM `farms` f WHERE f.unlock_base_id  NOT IN (
        'JEDIKNIGHTLUKE', -- 7 stars + r3
        'MANDALORBOKATAN', -- 7 stars + r7
        'GRANDINQUISITOR', -- 7 stars + r5
        'STARKILLER', -- 7 stars + r5
        'JARJARBINKS', -- 7 stars + r5
        'DOCTORAPHRA', -- 7 stars + r5
        'JEDIMASTERMACEWINDU', -- 7 stars + r5
        'JEDIKNIGHTCAL' -- 7 stars + g12
        )
    )
  -- update only character units, ships always needs a rarity
AND EXISTS (SELECT id FROM units u WHERE u.base_id = fu.base_id AND u.combat_type = 'CHARACTER')