package de.hydrum.toonworld.progress.guild

import de.hydrum.toonworld.util.Gain
import lombok.Data
import java.time.Instant


enum class ProgressType(val label: String) {
    GALACTIC_POWER("Galactic Power"),
    RAID_TICKETS("Raid Tickets")
}

@Data
data class GuildProgressData(
    val name: String,
    val guildId: String,
    val fromDateTime: Instant,
    val toDateTime: Instant,
    val nextResetTime: Instant,
    val galacticPowerGain: Gain<Long?>,
    val members: List<GuildMemberProgress>
)

@Data
data class GuildMemberProgress(
    val name: String,
    val joinDateTime: Instant,
    val galacticPowerGain: Gain<Long?>,
    val guildTickets: Gain<Long?>,
    val raidTickets: Gain<Long?>
)
