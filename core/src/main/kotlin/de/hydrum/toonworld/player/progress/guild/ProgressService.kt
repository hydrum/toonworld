package de.hydrum.toonworld.player.progress.guild

import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.player.database.repository.PlayerHistoryRepository
import de.hydrum.toonworld.player.progress.player.PlayerProgressReportService
import de.hydrum.toonworld.sync.GuildSyncService.AfterGuildSyncEvent
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.EmbedData
import discord4j.rest.util.Color
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Instant

@Service
class ProgressService(
    private val playerHistoryRepository: PlayerHistoryRepository,
    private val playerProgressReportService: PlayerProgressReportService,
    private val discordClient: GatewayDiscordClient,
    private val discordGuildRepository: DiscordGuildRepository
) {

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun sendJourneyProgressMessage(event: AfterGuildSyncEvent) {
        Pair(
            playerHistoryRepository.findPlayersOfGuildAtTimestamp(guildId = event.swgohGuildId, Instant.now()),
            playerHistoryRepository.findPlayersOfGuildAtTimestamp(guildId = event.swgohGuildId, Instant.now().minusSeconds(60 * 5))
        )
            .let { (newPlayers, oldPlayers) -> newPlayers.map { newPlayer -> Pair(newPlayer, oldPlayers.find { it.swgohPlayerId == newPlayer.swgohPlayerId }) } }
            .filter { (_, oldPlayer) -> oldPlayer != null }
            .map { (newPlayer, oldPlayer) -> playerProgressReportService.compareProgress(oldPlayer!!, newPlayer) }
            .flatMap { progress ->
                progress.journeyProgress
                    .map { journeyProgress ->
                        val requirementDone = journeyProgress.totalProgressGain.toValue == 1.0 && journeyProgress.totalProgressGain.fromValue != 1.0
                        val unlockedToon = progress.upgradedUnits.any { it.name == journeyProgress.unitName } && progress.upgradedUnits.first { it.name == journeyProgress.unitName }.levelGain.fromValue == null
                        Triple(progress.player.name, journeyProgress.unitName, listOf(requirementDone, unlockedToon))
                    }
                    .filter { (_, _, done) -> done[0] || done[1] }
                    .mapNotNull { (playerName, unitName, done) ->
                        when {
                            done[0] && done[1] -> "**$playerName** has completed the requirements and unlocked **$unitName**"
                            done[0] -> "**$playerName** has completed the requirements for **$unitName**"
                            done[1] -> "**$playerName** has unlocked **$unitName**"
                            else -> null
                        }
                    }
            }
            .let { unlockTexts ->
                if (unlockTexts.isEmpty()) return // ignore empty progress
                var discordGuild = discordGuildRepository.findBySwgohGuildId(swgohGuildId = event.swgohGuildId)
                if (discordGuild?.journeyProgressReportChannelId == null) return // we should not report it.
                discordClient
                    .getChannelById(Snowflake.of(discordGuild.journeyProgressReportChannelId))
                    .flatMap { channel ->
                        channel.restChannel.createMessage(
                            EmbedData.builder()
                                .color(Color.LIGHT_SEA_GREEN.rgb)
                                .title("Journey Progress")
                                .description(unlockTexts.joinToString("\n"))
                                .build()
                        )
                    }
                    .subscribe()
            }

    }
}