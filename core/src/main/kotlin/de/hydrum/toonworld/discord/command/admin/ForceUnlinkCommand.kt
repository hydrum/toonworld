package de.hydrum.toonworld.discord.command.admin

import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.management.PlayerLinkService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component

@Component
class ForceUnlinkCommand(
    private val playerLinkService: PlayerLinkService
) : BaseCommand(
    name = "unlink-force",
    description = "Force the unlink of an allyCode.",
    isSuperAdminCmd = true,
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("allycode")
            .description("allyCode of the account")
            .type(Type.STRING.value)
            .required(true)
            .build()
    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()
        val allyCode = getOption("allycode").flatMap { it.value }.map { it.asString() }.get()
        runCatching {
            playerLinkService.unlinkPlayer(allyCode)
            editReply("forced-unlink successful").subscribe()
        }.onFailure { handleError(this, it) }
    }
}