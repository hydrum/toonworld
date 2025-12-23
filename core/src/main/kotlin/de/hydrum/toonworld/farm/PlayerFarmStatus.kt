package de.hydrum.toonworld.farm

import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.unit.UnitCacheService
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant

data class PlayerFarmStatus(
    val playerName: String,
    val playerAllyCode: String,
    val playerUpdateTime: Instant,
    val farmName: String,
    val totalProgress: Double,
    val requirements: List<PlayerFarmUnitStatus>
)

data class PlayerFarmUnitStatus(
    val unitName: String,
    val rarityRequirement: Int,
    val gearRequirement: Int,
    val relicRequirement: RelicTier,
    val rarityStatus: Int?,
    val gearStatus: Int?,
    val relicStatus: RelicTier?,
    val rarityProgress: Double?,
    val gearProgress: Double?,
    val relicProgress: Double?,
    val totalProgress: Double,
)

fun FarmProgress.toStatus(player: Player, unitCacheService: UnitCacheService): PlayerFarmStatus =
    PlayerFarmStatus(
        playerName = player.name,
        playerAllyCode = player.allyCode,
        playerUpdateTime = player.updateTime,
        farmName = if (farm.unlockBaseId == null) farm.name else checkNotNull(unitCacheService.findUnit(farm.unlockBaseId!!)).name,
        totalProgress = totalProgress,
        requirements = relatedPlayerUnits.map {
            PlayerFarmUnitStatus(
                unitName = checkNotNull(unitCacheService.findUnit(it.farmUnit.baseId)).name,
                rarityProgress = it.getRarityProgress() ?: 1.0,
                gearProgress = it.getRarityProgress() ?: 1.0,
                relicProgress = it.getRarityProgress() ?: 1.0,
                totalProgress = it.getWeightedProgress(),
                rarityRequirement = it.farmUnit.minRarity,
                gearRequirement = it.farmUnit.minGearLevel,
                relicRequirement = it.farmUnit.minRelicTier,
                rarityStatus = it.playerUnit?.rarity,
                gearStatus = it.playerUnit?.gearLevel,
                relicStatus = it.playerUnit?.relicTier,
            )
        }
    )

fun PlayerFarmStatus.toDiscordEmbed(): EmbedCreateSpec =
    """     
            **Player:**         `${playerName}`
            **AllyCode:**       `${playerAllyCode}`
            **UpdateTime:**      ${playerUpdateTime.toDiscordRelativeDateTime()}
            
            ${toDiscordText()}
        """.trimIndent()
        .let {
            EmbedCreateSpec.builder()
                .color(Color.RUBY)
                .title("Status - Journey")
                .description(it)
                .build()
        }

fun PlayerFarmStatus.toDiscordText(): String {
    val units = requirements.sortedByDescending { it.totalProgress }
    val maxToonLength = units.maxOf { it.unitName.length }
    val maxStatusText = units.maxOf { it.toStatusText().length }
    val maxTotalProgressLength = units.maxOf { it.totalProgress.toPctText().length }
    return """> $farmName **${totalProgress.toPctText()} %**
    ```${
        units.joinToString("\n") {
            "${it.unitName.padEnd(maxToonLength)} | ${it.toStatusText().padEnd(maxStatusText)} | ${it.totalProgress.toPctText().padEnd(maxTotalProgressLength)} %"
        }
    }```
    """.trimIndent()
}

fun PlayerFarmUnitStatus.toMostNeededText() = when {
    relicRequirement.ordinal > RelicTier.LOCKED.ordinal -> relicRequirement.label
    gearRequirement > 0 -> "G$gearRequirement"
    rarityRequirement > 0 -> "$rarityRequirement*"
    else -> "nothing to do ?!"
}

fun PlayerFarmUnitStatus.toCurrentText() = when {
    (relicStatus?.ordinal ?: 0) > RelicTier.LOCKED.ordinal -> relicStatus?.label
    (rarityStatus ?: 0) < 7 -> "${rarityStatus ?: 0}*"
    (gearStatus ?: 0) < gearRequirement -> "G${gearStatus ?: 0}"
    else -> "nothing to do ?!"
}

fun PlayerFarmUnitStatus.toStatusText() = if (totalProgress == 1.0) toMostNeededText() else "${toCurrentText()} / ${toMostNeededText()}"
