package de.hydrum.toonworld.raid

import de.hydrum.toonworld.guild.database.repository.GuildRepository
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.abs

@Service
@Transactional
class RaidService(
    val guildRepository: GuildRepository,
    val playerRepository: PlayerRepository
) {

    fun getGuildRaidOverview(swgohGuildId: String): List<RaidDetails> =
        guildRepository.findBySwgohGuildId(swgohGuildId)
            ?.raids
            ?.map { guildRaid -> RaidDetails(guildName = guildRaid.guild!!.name, guildId = swgohGuildId, raidName = guildRaid.raidId, endTs = guildRaid.endTime, score = guildRaid.score) }
            ?: emptyList()

    fun getGuildRaidDetails(swgohGuildId: String, offset: Long): List<RaidMemberPerformance>? =
        guildRepository.findBySwgohGuildId(swgohGuildId)
            ?.raids
            ?.sortedByDescending { it.endTime }
            ?.getOrNull(abs(offset.toInt()))
            ?.let { raid ->
                val players = playerRepository.findBySwgohPlayerIdIn(raid.members.map { it.swgohPlayerId })
                raid.members.map { member ->
                    RaidMemberPerformance(player = players.find { it.swgohPlayerId == member.swgohPlayerId }, guildName = raid.guild!!.name, guildId = swgohGuildId, raidName = raid.raidId, endTs = raid.endTime, score = member.score)
                }
            }

    fun getGuildMemberRaidPerformance(swgohGuildId: String, allyCode: String): List<RaidMemberPerformance>? {
        val player = playerRepository.findPlayerByAllyCode(allyCode)
        return guildRepository.findBySwgohGuildId(swgohGuildId)
            ?.raids
            ?.flatMap { raid ->
                raid.members
                    .filter { it.swgohPlayerId == player?.swgohPlayerId }
                    .map { RaidMemberPerformance(player = player, guildName = raid.guild!!.name, guildId = swgohGuildId, raidName = raid.raidId, endTs = raid.endTime, score = it.score) }
            }
    }


}