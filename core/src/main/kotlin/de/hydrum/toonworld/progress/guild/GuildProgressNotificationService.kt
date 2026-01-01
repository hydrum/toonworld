package de.hydrum.toonworld.progress.guild

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
    private val unitCacheService: UnitCacheService
) {

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun sendJourneyProgressMessage(event: GuildProgressUpdateEvent) {
        val guild = discordGuildRepository.findBySwgohGuildId(event.swgohGuildId)
            ?: return
        event.progress
            .flatMap { progress ->
                val unlocks = PlayerUnlockProgress(progress)
                guild.farms
                    .mapNotNull { discordGuildFarm ->
                        val hasFarmCompleted = unlocks.getFarmCompletions().any { it.farmId == discordGuildFarm.farm.id }
                        val hasUnlockedToon = unlocks.getToonUnlocks().any { it.baseId == discordGuildFarm.farm.unlockBaseId }
                        val farmName = if (discordGuildFarm.farm.unlockBaseId == null) discordGuildFarm.farm.name else unitCacheService.findUnit(discordGuildFarm.farm.unlockBaseId!!)?.name ?: discordGuildFarm.farm.unlockBaseId
                        if (discordGuildFarm.announceChannelId != null && (hasFarmCompleted || hasUnlockedToon))
                            GuildProgressNotification(
                                guildId = event.swgohGuildId,
                                announceChannelId = discordGuildFarm.announceChannelId!!,
                                farmName = farmName ?: "Unknown",
                                isJourneyGuide = discordGuildFarm.farm.unlockBaseId != null,
                                playerName = progress.player.name,
                                farmCompleted = hasFarmCompleted,
                                toonUnlocked = hasUnlockedToon
                            )
                        else null
                    }
            }
            .let { sendDiscordNotificationForJourneyProgress(it) }
    }

    fun sendDiscordNotificationForJourneyProgress(guildNotifications: List<GuildProgressNotification>) {
        if (guildNotifications.isEmpty()) return // ignore empty progress
        guildNotifications.groupBy { it.announceChannelId }
            .forEach { (channelId, notifications) ->
                discordClient
                    .getChannelById(Snowflake.of(channelId))
                    .flatMap { channel ->
                        channel.restChannel.createMessage(
                            EmbedData.builder()
                                .color(Color.LIGHT_SEA_GREEN.rgb)
                                .title("Farm Progress")
                                .description(notifications.joinToString("\n") { it.unlockText() })
                                .build()
                        )
                    }
                    .subscribe()
            }
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

        fun getFarmCompletions() = progress.farmProgress
            .filter { it.totalProgressGain.toValue == 1.0 && it.totalProgressGain.fromValue != 1.0 }
    }

    data class GuildProgressNotification(val guildId: String, val announceChannelId: Long, val farmName: String, val isJourneyGuide: Boolean, val playerName: String, val farmCompleted: Boolean, val toonUnlocked: Boolean) {
        fun unlockText(): String {
            return if (isJourneyGuide && farmCompleted && toonUnlocked) "**$playerName** has completed the journey and unlocked **$farmName**"
            else if (isJourneyGuide && farmCompleted) "**$playerName** has completed the requirements of **$farmName**"
            else if (isJourneyGuide && toonUnlocked) "**$playerName** has unlocked **$farmName**"
            else if (farmCompleted) "**$playerName** has completed the farm of **$farmName**"
            else "Unknown notification type"
        }
    }
}