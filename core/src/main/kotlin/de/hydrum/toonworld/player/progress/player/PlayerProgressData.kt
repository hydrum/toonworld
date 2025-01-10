package de.hydrum.toonworld.player.progress.player

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
    val upgradedUnits: List<PlayerProgressUnit>
)

@Data
data class PlayerProgressUnit(
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
