package de.hydrum.toonworld.farm

import de.hydrum.toonworld.farm.database.Farm
import de.hydrum.toonworld.farm.database.FarmUnit
import de.hydrum.toonworld.player.database.model.PlayerUnit
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.util.round
import kotlin.math.pow

class FarmProgress(
    val farm: Farm,
    playerUnits: List<PlayerUnit>
) {
    val relatedPlayerUnits: List<FarmProgressUnit> = farm
        .units
        .map { farmUnit ->
            FarmProgressUnit(
                farmUnit = farmUnit,
                playerUnit = playerUnits.firstOrNull { farmUnit.baseId == it.baseId }
            )
        }

    val totalProgress = 1.0.coerceAtMost(relatedPlayerUnits.sumOf { it.getWeightedProgress() } / farm.teamSize)
}

data class FarmProgressUnit(
    val farmUnit: FarmUnit,
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
        val progress = if (farmUnit.minRarity == 0) null
        else if (playerUnit == null) 0.0
        else (playerUnit.rarity / farmUnit.minRarity.toDouble()).pow(EXPONENT_RARITY)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }

    fun getGearProgress(): Double? {
        val progress = if (farmUnit.minGearLevel == 0) null
        else if (playerUnit == null) 0.0
        else (playerUnit.gearLevel / farmUnit.minGearLevel.toDouble()).pow(EXPONENT_GEAR)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }

    fun getRelicProgress(): Double? {
        val progress = if (farmUnit.minRelicTier in listOf(RelicTier.NONE, RelicTier.LOCKED)) null
        else if (playerUnit == null) 0.0
        else (playerUnit.relicTier.relicValue / farmUnit.minRelicTier.relicValue.toDouble()).pow(EXPONENT_RELIC)

        return if (progress != null) 1.0.coerceAtMost(progress) else progress
    }
}

fun Double?.toPctText(): String = this?.let { it * 100 }?.round(2)?.toString() ?: "---"