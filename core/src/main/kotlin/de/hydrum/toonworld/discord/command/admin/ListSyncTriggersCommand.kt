package de.hydrum.toonworld.discord.command.admin

import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.jobs.JobService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ListSyncTriggersCommand(
    private val jobService: JobService,
) : BaseCommand(
    name = "sync-list",
    description = "List all active sync information to log",
    isSuperAdminCmd = true
) {
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        jobService.listSyncJobs()
            .also { log.info { "Running Syncs: $it" } }
            .also { reply().withContent("done, check logs").withEphemeral(true).subscribe() }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}