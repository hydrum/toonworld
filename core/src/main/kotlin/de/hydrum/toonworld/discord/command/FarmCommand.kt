package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.farm.FarmService
import de.hydrum.toonworld.farm.toDiscordEmbed
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class FarmCommand(
    private val farmService: FarmService
) : BaseCommand(
    name = "farm",
    description = "manage farms",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("list")
            .description("shows all available farms for the current guild.")
            .type(Type.SUB_COMMAND.value)
//            .options(
//                listOf(
//                    ApplicationCommandOptionData.builder()
//                        .name("name")
//                        .description("the name of the farm")
//                        .type(Type.INTEGER.value)
//                        .required(false)
//                        .autocomplete(true)
//                        .build()
//                )
//            )
            .build()
    )
) {
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val name = getOption("list").getOrNull()?.getOption("name")?.flatMap { it.value }?.map { it.asLong() }?.getOrNull()

        runCatching {
            val discordGuildId = interaction.guildId.orElseThrow { IllegalArgumentException("guildId not present") }
            if (name == null) {
                farmService.getAllFarmsForGuildId(discordGuildId.asLong())
                    .also { if (it == null || it.isEmpty()) createFollowup("nothing found").subscribe() }
                    ?.toDiscordEmbed()
                    ?.forEach {
                        createFollowup(
                            InteractionFollowupCreateSpec
                                .builder()
                                .addEmbed(it)
                                .build()
                        ).subscribe()
                    }
            } else {
                createFollowup("current not implemented").subscribe()
            }
        }.onFailure { handleError(this, it) }
    }

    override fun autocomplete(event: ChatInputAutoCompleteEvent): Unit = with(event) {
        val value = getValueAsStringOrBlank()
        respondWithSuggestions(
            listOf(
                ApplicationCommandOptionChoiceData
                    .builder()
                    .name("test")
                    .value(123)
                    .build()
            )
        ).subscribe()
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}