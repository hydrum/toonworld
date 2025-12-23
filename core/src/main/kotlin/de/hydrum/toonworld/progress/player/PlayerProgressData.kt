package de.hydrum.toonworld.progress.player

import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.util.Gain
import de.hydrum.toonworld.util.GainRelicTier
import de.hydrum.toonworld.util.GainUnitAbility
import lombok.Data
import java.time.Instant


@Data
data class PlayerProgressData(
    val player: Player,
    val fromDateTime: Instant,
    val toDateTime: Instant,
    val galacticPowerGain: Gain<Long?>,
    val zetaGain: Gain<Long?>,
    val omicronGain: Gain<Long?>,
    val upgradedUnits: List<PlayerProgressUnit>,
    val modsSixRarity: Gain<Int?>,
    val modsSpeed25: Gain<Int?>,
    val modsSpeed20: Gain<Int?>,
    val modsSpeed15: Gain<Int?>,
    val mods25SpeedAverage: Gain<Double?>,
    val mods500SpeedAverage: Gain<Double?>,
    val farmProgress: List<PlayerProgressFarm>
)

@Data
data class PlayerProgressUnit(
    val baseId: String,
    val name: String,
    val gearLevelGain: Gain<Int?>,
    val levelGain: Gain<Int?>,
    val rarityGain: Gain<Int?>,
    val relicTierGain: GainRelicTier,
    val ultimateGain: Gain<Boolean?>,
    val abilityGains: List<GainUnitAbility>
) {
    fun hasChanged(): Boolean =
        gearLevelGain.hasChanged()
                || levelGain.hasChanged()
                || rarityGain.hasChanged()
                || relicTierGain.hasChanged()
                || ultimateGain.hasChanged()
                || abilityGains.any { it.hasChanged() }
}

data class PlayerProgressFarm(
    val farmId: Long,
    val farmName: String,
    val totalProgressGain: Gain<Double?>,
    val unitGains: List<PlayerProgressFarmUnit>
)

data class PlayerProgressFarmUnit(
    val unitName: String,
    val rarityProgress: Gain<Double?>,
    val gearProgress: Gain<Double?>,
    val relicProgress: Gain<Double?>,
    val totalProgress: Gain<Double?>,
)