package de.hydrum.toonworld.progress.guild

import de.hydrum.toonworld.guild.database.model.Guild
import de.hydrum.toonworld.guild.database.repository.GuildHistoryRepository
import de.hydrum.toonworld.util.gainToLong
import de.hydrum.toonworld.util.utcNow
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class GuildProgressReportService(
    private val guildHistoryRepository: GuildHistoryRepository
) {

    @Transactional
    fun reportProgress(swgohGuildId: String, from: Instant?, to: Instant?): GuildProgressData {

        var fromDate = from ?: requireNotNull(guildHistoryRepository.findEarliestSyncDateTime(swgohGuildId = swgohGuildId)) { "No sync data found" }
        var toDate = to ?: utcNow()

        require(fromDate <= toDate) { "cannot find progress from future to past. please check your input." }

        log.trace { "trying to report progress for Guild $swgohGuildId in timeframe: $fromDate  ---  $toDate" }

        val fromGuild = guildHistoryRepository.findGuildAtTimestamp(swgohGuildId = swgohGuildId, instant = fromDate)
            ?: guildHistoryRepository.findEarliestSyncDateTime(swgohGuildId = swgohGuildId)
                .let { guildHistoryRepository.findGuildAtTimestamp(swgohGuildId = swgohGuildId, instant = requireNotNull(it) { "No sync data found" }) }
                .let { checkNotNull(it) { "fromGuild cannot be null as we ensured that there is an earliest time and therefore it should be able to retrieve such." } }

        val toGuild = checkNotNull(guildHistoryRepository.findGuildAtTimestamp(swgohGuildId = swgohGuildId, instant = toDate)) { "unexpected => please report with guildId $swgohGuildId and $toDate" }

        return compareProgress(fromGuild, toGuild)
    }

    fun compareProgress(guild1: Guild, guild2: Guild): GuildProgressData {
        val fromGuild = if (guild1.updateTime <= guild2.updateTime) guild1 else guild2
        val toGuild = if (guild1.updateTime > guild2.updateTime) guild1 else guild2

        return GuildProgressData(
            name = toGuild.name,
            guildId = toGuild.swgohGuildId,
            fromDateTime = fromGuild.updateTime,
            toDateTime = toGuild.updateTime,
            galacticPowerGain = fromGuild.galacticPower gainToLong toGuild.galacticPower,
            members = toGuild.members.map { toMember ->
                Pair(fromGuild.members.firstOrNull { fromMember -> fromMember.swgohPlayerId == toMember.swgohPlayerId }, toMember)
            }.map { (fromMember, toMember) ->
                GuildMemberProgress(
                    name = toMember.name,
                    joinDateTime = toMember.joinTime,
                    galacticPowerGain = fromMember?.galacticPower gainToLong toMember.galacticPower,
                    guildTickets = fromMember?.guildTokens gainToLong toMember.guildTokens,
                    raidTickets = fromMember?.raidTickets gainToLong toMember.raidTickets
                )
            }
        )
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}