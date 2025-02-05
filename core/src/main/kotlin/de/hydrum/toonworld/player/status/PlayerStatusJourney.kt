package de.hydrum.toonworld.player.status

import de.hydrum.toonworld.data.JourneyGuideProgress
import de.hydrum.toonworld.data.toPctText
import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.unit.UnitCacheService
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant

data class PlayerStatusJourney(
    val playerName: String,
    val playerAllyCode: String,
    val playerUpdateTime: Instant,
    val unitName: String,
    val totalProgress: Double,
    val requirements: List<PlayerStatusJourneyUnit>
)

data class PlayerStatusJourneyUnit(
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

fun JourneyGuideProgress.toStatus(player: Player, unitCacheService: UnitCacheService): PlayerStatusJourney =
    PlayerStatusJourney(
        playerName = player.name,
        playerAllyCode = player.allyCode,
        playerUpdateTime = player.updateTime,
        unitName = checkNotNull(unitCacheService.findUnit(journeyGuide.baseId)).name,
        totalProgress = totalProgress,
        requirements = relatedPlayerUnits.map {
            PlayerStatusJourneyUnit(
                unitName = checkNotNull(unitCacheService.findUnit(it.journeyUnit.baseId)).name,
                rarityProgress = it.getRarityProgress() ?: 1.0,
                gearProgress = it.getRarityProgress() ?: 1.0,
                relicProgress = it.getRarityProgress() ?: 1.0,
                totalProgress = it.getWeightedProgress(),
                rarityRequirement = it.journeyUnit.rarity,
                gearRequirement = it.journeyUnit.gearLevel,
                relicRequirement = it.journeyUnit.relicTier,
                rarityStatus = it.playerUnit?.rarity,
                gearStatus = it.playerUnit?.gearLevel,
                relicStatus = it.playerUnit?.relicTier,
            )
        }
    )

fun List<PlayerStatusJourney>.toDiscordEmbed(): List<EmbedCreateSpec> {
    if (isEmpty()) return emptyList()
    return map {
        """     
            **Player:**         `${it.playerName}`
            **AllyCode:**       `${it.playerAllyCode}`
            **UpdateTime:**      ${it.playerUpdateTime.toDiscordRelativeDateTime()}
            
            ${it.toDiscordText()}
        """.trimIndent()
    }
        .map {
            EmbedCreateSpec.builder()
                .color(Color.RUBY)
                .title("Status - Journey")
                .description(it)
                .build()
        }
}

fun PlayerStatusJourney.toDiscordText(): String {
    val units = requirements.sortedByDescending { it.totalProgress }
    val maxToonLength = units.maxOf { it.unitName.length }
    val maxStatusText = units.maxOf { it.toStatusText().length }
    val maxTotalProgressLength = units.maxOf { it.totalProgress.toPctText().length }
    return """> $unitName **${totalProgress.toPctText()} %**
    ```${
        units.joinToString("\n") {
            "${it.unitName.padEnd(maxToonLength)} | ${it.toStatusText().padEnd(maxStatusText)} | ${it.totalProgress.toPctText().padEnd(maxTotalProgressLength)} %"
        }
    }```
    """.trimIndent()
}

fun PlayerStatusJourneyUnit.toMostNeededText() = when {
    relicRequirement.ordinal > RelicTier.LOCKED.ordinal -> relicRequirement.label
    gearRequirement > 0 -> "G$gearRequirement"
    rarityRequirement > 0 -> "$rarityRequirement*"
    else -> "nothing to do ?!"
}

fun PlayerStatusJourneyUnit.toCurrentText() = when {
    (relicStatus?.ordinal ?: 0) > RelicTier.LOCKED.ordinal -> relicStatus?.label
    (rarityStatus ?: 0) < 7 -> "${rarityStatus ?: 0}*"
    (gearStatus ?: 0) < gearRequirement -> "G${gearStatus ?: 0}"
    else -> "nothing to do ?!"
}

fun PlayerStatusJourneyUnit.toStatusText() = if (totalProgress == 1.0) toMostNeededText() else "${toCurrentText()} / ${toMostNeededText()}"
