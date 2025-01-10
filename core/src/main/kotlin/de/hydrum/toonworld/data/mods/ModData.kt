package de.hydrum.toonworld.data.mods

import de.hydrum.toonworld.player.database.model.PlayerUnitMod
import de.hydrum.toonworld.api.comlink.models.Mod as ComlinkMod

data class ModData(
    val statModSet: List<ModSet>,
    val statMod: List<StatMod>
)

data class ModSet(
    val id: String,
    val name: String,
    val icon: String,
    val completeBonus: ModBonus,
    val maxLevelBonus: ModBonus,
    val setCount: Int,
    val overclockBonus: Any?
)

data class ModBonus(
    val abilityId: Any,
    val stat: ModStat,
)

data class StatMod(
    val missionLookup: List<Any>,
    val raidLookup: List<Any>,
    val actionLinkLookup: List<Any>,
    val raidImmediateLookup: List<Any>,
    val id: String,
    val slot: Int,
    val setId: String,
    val rarity: Int,
    val nameKey: String,
    val descKey: String,
    val levelTableId: String,
    val promotionId: String,
    val promotionRecipeId: String,
    val tierUpRecipeTableId: String,
    val overclockRecipeTableId: String,
    val rerollCapTableId: String
)

data class ModStat(
    val unitStatId: UnitStat,
    val statValueDecimal: String,
    val unscaledDecimalValue: String,
    val uiDisplayOverrideValue: String,
    val scalar: String
)


@Suppress("SpellCheckingInspection")
enum class UnitStat(val displayName: String, val percentage: Boolean = false) {
    DEFAULT(""),
    MAXHEALTH("Health"),
    STRENGTH("Strength"),
    AGILITY("Agility"),
    INTELLIGENCE("Intelligence"),
    SPEED("Speed"),
    ATTACKDAMAGE("Attack Damage"),
    ABILITYPOWER("Ability Power"),
    ARMOR("Armor"),
    SUPPRESSION("Suppression"),
    ARMORPENETRATION("Armor Penetration"),
    SUPPRESSIONPENETRATION("Suppression Penetration"),
    DODGERATING("Dodge Rating"),
    DEFLECTIONRATING("Deflection Rating"),
    ATTACKCRITICALRATING("Attack Critical Rating"),
    ABILITYCRITICALRATING("Ability Critical Rating"),
    CRITICALDAMAGE("Critical Damage", true),
    ACCURACY("Potency", true),
    RESISTANCE("Tenacity", true),
    DODGEPERCENTADDITIVE("Dodge", true),
    DEFLECTIONPERCENTADDITIVE("Deflection", true),
    ATTACKCRITICALPERCENTADDITIVE("Attack Critical", true),
    ABILITYCRITICALPERCENTADDITIVE("Ability Critical", true),
    ARMORPERCENTADDITIVE("Armor", true),
    SUPPRESSIONPERCENTADDITIVE("Suppression", true),
    ARMORPENETRATIONPERCENTADDITIVE("Armor Penetration", true),
    SUPPRESSIONPENETRATIONPERCENTADDITIVE("Suppresion Penetration", true),
    HEALTHSTEAL("Health Steal"),
    MAXSHIELD("Protection"),
    SHIELDPENETRATION("Shield Penetration"),
    HEALTHREGEN("Health Regen"),
    ATTACKDAMAGEPERCENTADDITIVE("Attack Damage", true),
    ABILITYPOWERPERCENTADDITIVE("Ability Power", true),
    DODGENEGATEPERCENTADDITIVE("Dodge Negate", true),
    DEFLECTIONNEGATEPERCENTADDITIVE("Deflect Negate", true),
    ATTACKCRITICALNEGATEPERCENTADDITIVE("Attack Critical Negate", true),
    ABILITYCRITICALNEGATEPERCENTADDITIVE("Ability Critical Negate", true),
    DODGENEGATERATING("Dodge Negate"),
    DEFLECTIONNEGATERATING("Deflection Negate"),
    ATTACKCRITICALNEGATERATING("Attack Critical Negate"),
    ABILITYCRITICALNEGATERATING("Ability Critical Negate"),
    OFFENSE("Offense"),
    DEFENSE("Defense"),
    DEFENSEPENETRATION("Defense Penetration"),
    EVASIONRATING("Evasion"),
    CRITICALRATING("Critical"),
    EVASIONNEGATERATING("Evasion Negate"),
    CRITICALNEGATERATING("Critical Negate"),
    OFFENSEPERCENTADDITIVE("Offense", true),
    DEFENSEPERCENTADDITIVE("Defense", true),
    DEFENSEPENETRATIONPERCENTADDITIVE("Defense Penetration", true),
    EVASIONPERCENTADDITIVE("Evasion", true),
    EVASIONNEGATEPERCENTADDITIVE("Evasion Negate", true),
    CRITICALCHANCEPERCENTADDITIVE("Critical Chance", true),
    CRITICALNEGATECHANCEPERCENTADDITIVE("Critical Avoidance", true),
    MAXHEALTHPERCENTADDITIVE("Health", true),
    MAXSHIELDPERCENTADDITIVE("Protection", true),
    SPEEDPERCENTADDITIVE("Speed", true),
    COUNTERATTACKRATING("Counter Attack"),
    TAUNT("Taunt"),
    DEFENSEPENETRATIONTARGETPERCENTADDITIVE("Defense Penetration Target", true),
    MASTERY("Mastery"),
}

enum class ModTier {
    DEFAULT,
    E,
    D,
    C,
    B,
    A,
}

enum class ModSlot(val displayName: String) {
    DEFAULT("Default"),
    UNKNOWN("Unknown"),
    SQUARE("Square"),
    ARROW("Arrow"),
    DIAMOND("Diamond"),
    TRIANGLE("Triangle"),
    CIRCLE("Circle"),
    CROSS("Cross")
}

fun ComlinkMod.createModWith(modData: ModData): PlayerUnitMod {
    val statMod = requireNotNull(modData.statMod.find { it.id == this.definitionId })
    val modSet = requireNotNull(modData.statModSet.find { it.id == statMod.setId })

    return PlayerUnitMod(
        id = null,
        unit = null,
        playerUnitId = null,
        player = null,
        playerId = null,
        slot = ModSlot.entries[statMod.slot],
        level = this.level,
        rarity = statMod.rarity,
        tier = ModTier.entries[this.tier],
        modSet = modSet.completeBonus.stat.unitStatId,
        primaryStat = UnitStat.entries[this.primaryStat.stat.unitStatId],
        primaryValue = this.primaryStat.stat.statValueDecimal.toLong(),
        secondary1Stat = this.secondaryStat.elementAtOrNull(0)?.let { UnitStat.entries[it.stat.unitStatId] },
        secondary1Value = this.secondaryStat.elementAtOrNull(0)?.stat?.statValueDecimal?.toLong(),
        secondary1Roll = this.secondaryStat.elementAtOrNull(0)?.statRolls,
        secondary2Stat = this.secondaryStat.elementAtOrNull(1)?.let { UnitStat.entries[it.stat.unitStatId] },
        secondary2Value = this.secondaryStat.elementAtOrNull(1)?.stat?.statValueDecimal?.toLong(),
        secondary2Roll = this.secondaryStat.elementAtOrNull(1)?.statRolls,
        secondary3Stat = this.secondaryStat.elementAtOrNull(2)?.let { UnitStat.entries[it.stat.unitStatId] },
        secondary3Value = this.secondaryStat.elementAtOrNull(2)?.stat?.statValueDecimal?.toLong(),
        secondary3Roll = this.secondaryStat.elementAtOrNull(2)?.statRolls,
        secondary4Stat = this.secondaryStat.elementAtOrNull(3)?.let { UnitStat.entries[it.stat.unitStatId] },
        secondary4Value = this.secondaryStat.elementAtOrNull(3)?.stat?.statValueDecimal?.toLong(),
        secondary4Roll = this.secondaryStat.elementAtOrNull(3)?.statRolls,
    )
}

fun Triple<UnitStat, Long, Int>.toText(): String = "+${if (first.percentage) (second / 100f).toString() + "%" else (second / 10_000)} ${first.displayName} (${third})"