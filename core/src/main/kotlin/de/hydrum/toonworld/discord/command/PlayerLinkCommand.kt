package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.management.PlayerService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component

@Component
class PlayerLinkCommand(
    private val playerService: PlayerService
) : BaseCommand(
    name = "link",
    description = "Link an ally code to your player. You may specify a number for alternate accounts.",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("allycode")
            .description("allyCode of the account")
            .type(Type.STRING.value)
            .required(true)
            .build(),
        ApplicationCommandOptionData.builder()
            .name("slot")
            .description("default is 0 (primary slot). specify a number to have shortcuts for multiple accounts.")
            .type(Type.INTEGER.value)
            .required(false)
            .build()
    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()
        val allyCode = getOption("allycode").flatMap { it.value }.map { it.asString() }.get()
        val slot = getOption("slot").flatMap { it.value }.map { it.asLong() }.orElse(0L)
        runCatching {
            playerService.linkPlayer(interaction.user, allyCode, slot)
            editReply("Linked $allyCode to ${interaction.user.mention} as ${if (slot == 0L) "primary" else "$slot"} slot").subscribe()
        }.onFailure { handleError(this, it, allyCode) }
    }
}