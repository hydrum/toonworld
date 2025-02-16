package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.data.DataCacheService
import de.hydrum.toonworld.management.PlayerService
import de.hydrum.toonworld.player.status.PlayerStatusService
import de.hydrum.toonworld.player.status.toDiscordEmbed
import de.hydrum.toonworld.unit.UnitCacheService
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class JourneyCommand(
    private val playerService: PlayerService,
    private val playerStatusService: PlayerStatusService,
    dataCacheService: DataCacheService,
    unitCacheService: UnitCacheService
) : BaseCommand(
    name = "journey",
    description = "Retrieve an overview of all ongoing journeys",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("journey")
            .description("choose the journey guide to get a summary for")
            .type(Type.STRING.value)
            .addAllChoices(
                dataCacheService.getJourneyData()
                    .map {
                        ApplicationCommandOptionChoiceData
                            .builder()
                            .value(it.baseId)
                            .name(unitCacheService.findUnit(it.baseId)?.name ?: it.baseId)
                            .build()
                    })
            .required(true)
            .build(),
        ApplicationCommandOptionData.builder()
            .name("user")
            .description("default is none. if provided, the user's linked allyCode is used")
            .type(Type.USER.value)
            .required(false)
            .build(),
        ApplicationCommandOptionData.builder()
            .name("allycode")
            .description("default is none. if provided, a report for that allyCode is compiled")
            .type(Type.STRING.value)
            .required(false)
            .build(),
        ApplicationCommandOptionData.builder()
            .name("slot")
            .description("default is 0 (primary slot). If no allyCode is provided, it's allyCode is used")
            .type(Type.INTEGER.value)
            .required(false)
            .build()
    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val journey = requireNotNull(getOption("journey").flatMap { it.value }?.map { it.asString() }?.orElse(null)) { "you have to provide a journey" }
        val allyCode = getOption("allycode").flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getOption("slot").flatMap { it.value }?.map { it.asLong() }?.orElse(0L) ?: 0L

        runCatching {
            val userOption = getOption("user").flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.orElse(null)
            val user = if (userOption?.isPresent == true) userOption.get() else interaction.user

            playerStatusService.getJourneyStatus(
                allyCode = allyCode ?: playerService.getAllyCodeChecked(user, slot),
                journey = journey
            ).also {
                it.toDiscordEmbed().forEach {
                    createFollowup(
                        InteractionFollowupCreateSpec
                            .builder()
                            .addEmbed(it)
                            .build()
                    ).subscribe()
                }
            }
        }.onFailure { handleError(this, it) }
    }
}