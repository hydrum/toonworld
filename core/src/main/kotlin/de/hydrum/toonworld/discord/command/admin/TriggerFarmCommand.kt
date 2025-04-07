package de.hydrum.toonworld.discord.command.admin

import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.management.DiscordRoleAssignmentService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component

@Component
class TriggerFarmCommand(
    private val discordRoleAssignmentService: DiscordRoleAssignmentService
) : BaseCommand(
    name = "farm-trigger",
    description = "retrigger the assignment check of a farm",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("guild")
            .description("the guild id")
            .type(Type.STRING.value)
            .required(true)
            .build()
    ),
    isSuperAdminCmd = true
) {
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val guildId = getOption("guild").flatMap { it.value }?.map { it.asString() }?.orElse(null)

        runCatching {
            discordRoleAssignmentService.checkForAssignments(guildId!!)
            editReply("done").subscribe()
        }.onFailure { handleError(this, it) }
    }
}