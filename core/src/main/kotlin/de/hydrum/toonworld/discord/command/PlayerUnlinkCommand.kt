package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.management.PlayerService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component

@Component
class PlayerUnlinkCommand(
    private val playerService: PlayerService
) : BaseCommand(
    name = "unlink",
    description = "Unlink an allyCode. Requires allyCode OR slot. If both provided, allyCode will be used.",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("allycode")
            .description("allyCode of the account")
            .type(Type.STRING.value)
            .required(false)
            .build(),
        ApplicationCommandOptionData.builder()
            .name("slot")
            .description("slot number of the account. 0 is your primary account slot.")
            .type(Type.INTEGER.value)
            .required(false)
            .build()
    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()
        val allyCode = getOption("allycode").flatMap { it.value }.map { it.asString() }.orElse(null)
        val slot = getOption("slot").flatMap { it.value }.map { it.asLong() }.orElse(null)

        if (allyCode == null && slot == null) {
            editReply(":warning: please specify and allyCode or a slot").subscribe()
            return
        }

        runCatching {
            if (allyCode != null) playerService.unlinkPlayer(interaction.user, allyCode)
            else playerService.unlinkPlayer(interaction.user, slot)
            editReply("unlink successful").subscribe()
        }.onFailure { handleError(this, it) }
    }
}