package de.hydrum.toonworld.progress.guild

import de.hydrum.toonworld.data.DataCacheService
import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.progress.guild.GuildProgressService.GuildProgressUpdateEvent
import de.hydrum.toonworld.progress.player.PlayerProgressData
import de.hydrum.toonworld.unit.UnitCacheService
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.EmbedData
import discord4j.rest.util.Color
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class GuildProgressNotificationService(
    private val discordClient: GatewayDiscordClient,
    private val discordGuildRepository: DiscordGuildRepository,
    private val dataCacheService: DataCacheService,
    private val unitCacheService: UnitCacheService
) {

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun sendJourneyProgressMessage(event: GuildProgressUpdateEvent) {
        event.progress
            .flatMap { progress ->
                val unlocks = PlayerUnlockProgress(progress)
                dataCacheService.getJourneyData()
                    .map { journey ->
                        val requirementDone = unlocks.getJourneyRequirementCompletions().any { it.unitBaseId == journey.baseId }
                        val hasUnlockedToon = unlocks.getToonUnlocks().any { it.baseId == journey.baseId }
                        val journeyToonName = unitCacheService.findUnit(journey.baseId)?.name ?: journey.baseId
                        Triple(progress.player.name, journeyToonName, listOf(requirementDone, hasUnlockedToon))
                    }
                    .filter { (_, _, done) -> done[0] || done[1] }
                    .mapNotNull { (playerName, toonName, done) ->
                        when {
                            done[0] && done[1] -> "**$playerName** has completed the requirements and unlocked **$toonName**"
                            done[0] -> "**$playerName** has completed the requirements for **$toonName**"
                            done[1] -> "**$playerName** has unlocked **$toonName**"
                            else -> null
                        }
                    }
            }
            .let { sendDiscordNotificationForJourneyProgress(event.swgohGuildId, it) }
    }

    fun sendDiscordNotificationForJourneyProgress(swgohGuildId: String, unlockTexts: List<String>) {
        if (unlockTexts.isEmpty()) return // ignore empty progress
        var discordGuild = discordGuildRepository.findBySwgohGuildId(swgohGuildId = swgohGuildId)
        if (discordGuild?.journeyProgressReportChannelId == null) return // we should not report it.
        discordClient
            .getChannelById(Snowflake.of(discordGuild.journeyProgressReportChannelId!!))
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

    @JvmInline
    value class PlayerUnlockProgress(val progress: PlayerProgressData) {
        fun getAllToonUpdates() = progress.upgradedUnits
            .filter { it.hasChanged() }

        fun getToonUnlocks() = getAllToonUpdates()
            .filter { it.levelGain.fromValue == null && it.levelGain.toValue != null }

        fun getUltimateUnlocks() = getAllToonUpdates()
            .filter { it.ultimateGain.fromValue != true && it.ultimateGain.toValue == true }

        fun getAbilityUnlocks() = getAllToonUpdates()
            .flatMap { toon -> toon.abilityGains.map { Pair(toon, it) } }
            .filter { (_, ability) -> ability.hasChanged() }

        fun getZetaUnlocks() = getAbilityUnlocks()
            .filter { (_, ability) -> ability.hasZetaChanged() }

        fun getOmiUnlocks() = getAbilityUnlocks()
            .filter { (_, ability) -> ability.hasOmicronChanged() }

        fun getJourneyRequirementCompletions() = progress.journeyProgress
            .filter { it.totalProgressGain.toValue == 1.0 && it.totalProgressGain.fromValue != 1.0 }
    }
}