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
            .name("guild")
            .description("guild farm commands")
            .type(Type.SUB_COMMAND_GROUP.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("list")
                        .description("shows all available farms for the current guild.")
                        .type(Type.SUB_COMMAND.value)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("status")
                        .description("shows the status of a farm for the entire guild.")
                        .type(Type.SUB_COMMAND.value)
                        .options(
                            listOf(
                                ApplicationCommandOptionData.builder()
                                    .name("farm")
                                    .description("the farm to show the status for")
                                    .type(Type.INTEGER.value)
                                    .required(true)
                                    .autocomplete(true)
                                    .build()
                            )
                        )
                        .build()
                )
            )
            .build()
    )
) {
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val guildGroup = getOption("guild").getOrNull()
        val listSub = guildGroup?.getOption("list")?.getOrNull()
        val statusSub = guildGroup?.getOption("status")?.getOrNull()

        runCatching {
            val discordGuildId = requireNotNull(interaction.guildId.getOrNull()) { "guildId not present" }.asLong()

            if (listSub != null) {
                farmService.getAllFarmsForGuildId(discordGuildId)
                    .also { if (it.isNullOrEmpty()) createFollowup("nothing found").subscribe() }
                    ?.toDiscordEmbed()
                    ?.forEach {
                        createFollowup(
                            InteractionFollowupCreateSpec
                                .builder()
                                .addEmbed(it)
                                .build()
                        ).subscribe()
                    }
            } else if (statusSub != null) {
                val farmId = requireNotNull(statusSub.getOption("farm").flatMap { it.value }.map { it.asLong() }.getOrNull()) { "farmId not present" }
                val status = farmService.getGuildFarmStatus(discordGuildId, farmId)
                createFollowup(
                    InteractionFollowupCreateSpec
                        .builder()
                        .addEmbed(status.toDiscordEmbed())
                        .build()
                ).subscribe()
            }
        }.onFailure { handleError(this, it) }
    }

    override fun autocomplete(event: ChatInputAutoCompleteEvent): Unit = with(event) {
        val discordGuildId = interaction.guildId.map { it.asLong() }.getOrNull() ?: return@with
        val value = getValueAsStringOrBlank().lowercase()

        val guildFarms = farmService.getAllFarmsForGuildId(discordGuildId)?.map { it.farm } ?: emptyList()
        val journeyFarms = farmService.getJourneyGuideFarms()

        (guildFarms + journeyFarms)
            .filter { it.name.lowercase().contains(value) }
            .take(25)
            .map {
                ApplicationCommandOptionChoiceData.builder()
                    .name(it.name)
                    .value(it.id!!)
                    .build()
            }
            .sortedBy { it.name() }
            .let { respondWithSuggestions(it).subscribe() }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}