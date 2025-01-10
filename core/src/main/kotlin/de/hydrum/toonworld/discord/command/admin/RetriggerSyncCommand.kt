package de.hydrum.toonworld.discord.command.admin

import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.jobs.JobService
import de.hydrum.toonworld.sync.database.SyncGuildRepository
import de.hydrum.toonworld.sync.database.SyncPlayerRepository
import de.hydrum.toonworld.util.toDiscordDateTime
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import org.springframework.stereotype.Component

@Component
class RetriggerSyncCommand(
    val syncPlayerRepository: SyncPlayerRepository,
    val syncGuildRepository: SyncGuildRepository,
    val jobService: JobService
) : BaseCommand(
    name = "sync-retrigger",
    description = "Retrigger the sync jobs based on DB values",
    isSuperAdminCmd = true
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        val nextPlayerSync = syncPlayerRepository.findNextSyncTime()?.also { jobService.scheduleNextPlayerSyncJob(it) }
        val nextGuildSync = syncGuildRepository.findNextSyncTime()?.also { jobService.scheduleNextGuildSyncJob(it) }
        reply()
            .withContent(
                """
                next Syncs:
                PlayerSync: ${nextPlayerSync?.toDiscordDateTime()} (${nextPlayerSync?.toDiscordRelativeDateTime()})
                GuildSync: ${nextGuildSync?.toDiscordDateTime()} (${nextGuildSync?.toDiscordRelativeDateTime()})
                """.trimIndent()
            )
            .subscribe()
    }
}