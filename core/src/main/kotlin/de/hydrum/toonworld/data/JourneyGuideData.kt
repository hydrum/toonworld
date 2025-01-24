package de.hydrum.toonworld.data

import de.hydrum.toonworld.player.database.model.PlayerUnit
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.util.round
import kotlin.math.pow

data class JourneyGuides(
    val units: List<JourneyGuide>
)

data class JourneyGuide(
    val baseId: String,
    val requiredUnits: List<JourneyGuideUnit>
)

data class JourneyGuideUnit(
    val baseId: String,
    val rarity: Int,
    val gearLevel: Int,
    val relicTier: RelicTier
)

class JourneyGuideProgress(
    val journeyGuide: JourneyGuide,
    playerUnits: List<PlayerUnit>
) {
    val relatedPlayerUnits: List<JourneyGuideProgressUnit> = journeyGuide
        .requiredUnits
        .map { journeyUnit ->
            JourneyGuideProgressUnit(
                journeyUnit = journeyUnit,
                playerUnit = playerUnits.firstOrNull { journeyUnit.baseId == it.baseId }
            )
        }

    val totalProgress = relatedPlayerUnits.map { it.getWeightedProgress() }.average()
}

data class JourneyGuideProgressUnit(
    val journeyUnit: JourneyGuideUnit,
    val playerUnit: PlayerUnit?
) {
    companion object {
        const val WEIGHT_RARITY = 2.0
        const val WEIGHT_GEAR = 1.0
        const val WEIGHT_RELIC = 2.0

        const val EXPONENT_RARITY = 3.0
        const val EXPONENT_GEAR = 3.0
        const val EXPONENT_RELIC = 3.0
    }

    fun getWeightedProgress(): Double {
        var overallWeight = 0.0
        if (getRarityProgress() != null) overallWeight += WEIGHT_RARITY
        if (getGearProgress() != null) overallWeight += WEIGHT_GEAR
        if (getRelicProgress() != null) overallWeight += WEIGHT_RELIC

        return ((getRarityProgress() ?: .0) * WEIGHT_RARITY
                + (getGearProgress() ?: .0) * WEIGHT_GEAR
                + (getRelicProgress() ?: .0) * WEIGHT_RELIC
                ) / overallWeight
    }

    fun getRarityProgress(): Double? {
        val progress = if (journeyUnit.rarity == 0) null
        else if (playerUnit == null) 0.0
        else (playerUnit.rarity / journeyUnit.rarity.toDouble()).pow(EXPONENT_RARITY)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }

    fun getGearProgress(): Double? {
        val progress = if (journeyUnit.gearLevel == 0) null
        else if (playerUnit == null) 0.0
        else (playerUnit.gearLevel / journeyUnit.gearLevel.toDouble()).pow(EXPONENT_GEAR)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }

    fun getRelicProgress(): Double? {
        val progress = if (journeyUnit.relicTier in listOf(RelicTier.NONE, RelicTier.LOCKED)) null
        else if (playerUnit == null) 0.0
        else (playerUnit.relicTier.relicValue / journeyUnit.relicTier.relicValue.toDouble()).pow(EXPONENT_RELIC)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }
}

fun Double?.toPctText(): String = this?.let { it * 100 }?.round(2)?.toString() ?: "---"