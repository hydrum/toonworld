package de.hydrum.toonworld

import de.hydrum.toonworld.unit.database.model.AbilityType
import de.hydrum.toonworld.unit.database.model.Alignment
import de.hydrum.toonworld.unit.database.model.CombatType
import de.hydrum.toonworld.unit.database.model.OmicronMode
import de.hydrum.toonworld.api.swgohgg.models.Ability as SwgohggAbility
import de.hydrum.toonworld.api.swgohgg.models.GearLevel as SwgohggGearLevel
import de.hydrum.toonworld.api.swgohgg.models.Unit as SwgohggUnit


val SWGOHGG_GEAR_LEVEL_1 = SwgohggGearLevel(tier = 1, gear = listOf("1", "2"))
val SWGOHGG_GEAR_LEVEL_2 = SwgohggGearLevel(tier = 2, gear = listOf("4", "8"))

val SWGOHGG_UNIT_1 = SwgohggUnit(
    name = "A",
    baseId = "A_BASE",
    url = "url",
    image = "image",
    power = 1000,
    description = "A_DESC",
    combatType = CombatType.CHARACTER.ordinal,
    gearLevels = listOf(SWGOHGG_GEAR_LEVEL_1, SWGOHGG_GEAR_LEVEL_2),
    alignment = Alignment.NEUTRAL.ordinal,
    categories = listOf("A", "C"),
    abilityClasses = listOf("X", "Y", "Z"),
    role = "Attacker",
    activateShardCount = 53,
    isCapitalShip = false,
    isGalacticLegend = false,
    madeAvailableOn = "2024-01-01",
    crewBaseIds = emptyList(),
    omicronAbilityIds = emptyList(),
    zetaAbilityIds = emptyList(),
    shipBaseId = null,
    shipSlot = null
)

val SWGOHGG_UNIT_2 = SwgohggUnit(
    name = "B",
    baseId = "B_BASE",
    url = "url",
    image = "image",
    power = 1000,
    description = "B_DESC",
    combatType = CombatType.SHIP.ordinal,
    gearLevels = listOf(SWGOHGG_GEAR_LEVEL_1, SWGOHGG_GEAR_LEVEL_2),
    alignment = Alignment.DARK.ordinal,
    categories = listOf("A", "DERP"),
    abilityClasses = listOf("X", "Y", "Z"),
    role = "Support",
    activateShardCount = 53,
    isCapitalShip = true,
    isGalacticLegend = false,
    madeAvailableOn = "2024-01-01",
    crewBaseIds = emptyList(),
    omicronAbilityIds = emptyList(),
    zetaAbilityIds = emptyList(),
    shipBaseId = null,
    shipSlot = null
)

val SWGOHGG_ABILITY_1 = SwgohggAbility(
    baseId = "ABILITY_A",
    abilityId = "ability_a",
    name = "A Name",
    image = "img",
    url = "url",
    tierMax = 6,
    isZeta = false,
    isOmega = false,
    isOmicron = true,
    isUltimate = false,
    description = "Fancy long text",
    combatType = CombatType.CHARACTER.ordinal,
    omicronMode = OmicronMode.CONQUEST.ordinal,
    type = AbilityType.LEADER.ordinal,
    omicronBattleTypes = listOf("whatever"),
    characterBaseId = SWGOHGG_UNIT_1.baseId,
    shipBaseId = null
)

val SWGOHGG_ABILITY_2 = SwgohggAbility(
    baseId = "ABILITY_B",
    abilityId = "ability_ab",
    name = "BName",
    image = "img",
    url = "url",
    tierMax = 6,
    isZeta = true,
    isOmega = false,
    isOmicron = true,
    isUltimate = false,
    description = "Fancy long text",
    combatType = CombatType.CHARACTER.ordinal,
    omicronMode = OmicronMode.TW.ordinal,
    type = AbilityType.UNIQUE.ordinal,
    omicronBattleTypes = listOf("whatever"),
    characterBaseId = SWGOHGG_UNIT_1.baseId,
    shipBaseId = null
)

val SWGOHGG_ABILITY_3 = SwgohggAbility(
    baseId = "ABILITY_C",
    abilityId = "ability_ca",
    name = "seeName",
    image = "img",
    url = "url",
    tierMax = 43,
    isZeta = true,
    isOmega = false,
    isOmicron = false,
    isUltimate = false,
    description = "Fancy long text",
    combatType = CombatType.CHARACTER.ordinal,
    omicronMode = OmicronMode.NONE.ordinal,
    type = AbilityType.BASIC.ordinal,
    omicronBattleTypes = emptyList(),
    characterBaseId = SWGOHGG_UNIT_1.baseId,
    shipBaseId = null
)

val SWGOHGG_ABILITY_4 = SwgohggAbility(
    baseId = "ABILITY_4",
    abilityId = "ability_four",
    name = "fancy4",
    image = "img",
    url = "url",
    tierMax = 3,
    isZeta = true,
    isOmega = true,
    isOmicron = false,
    isUltimate = false,
    description = "Fancy long text",
    combatType = CombatType.SHIP.ordinal,
    omicronMode = OmicronMode.NONE.ordinal,
    type = AbilityType.SHIP_SPECIAL.ordinal,
    omicronBattleTypes = emptyList(),
    characterBaseId = null,
    shipBaseId = SWGOHGG_UNIT_2.baseId
)