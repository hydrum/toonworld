package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.farm.FarmService
import de.hydrum.toonworld.farm.toDiscordEmbed
import de.hydrum.toonworld.management.DiscordPlayerCacheService
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
    private val playerCacheService: DiscordPlayerCacheService,
    private val farmService: FarmService,
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
                farmService.getJourneyGuideFarms()
                    .filter { it.unlockBaseId != null }
                    .map {
                        ApplicationCommandOptionChoiceData
                            .builder()
                            .value(it.unlockBaseId)
                            .name(unitCacheService.findUnit(it.unlockBaseId!!)?.name ?: it.unlockBaseId)
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
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val journey = requireNotNull(getOption("journey").flatMap { it.value }?.map { it.asString() }?.orElse(null)) { "you have to provide a journey" }
        val allyCode = getOption("allycode").flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getOption("slot").flatMap { it.value }?.map { it.asLong() }?.orElse(0L) ?: 0L

        runCatching {
            val userOption = getOption("user").flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.orElse(null)
            val user = if (userOption?.isPresent == true) userOption.get() else interaction.user

            farmService.getFarmStatus(
                allyCode = allyCode ?: playerCacheService.getAllyCodeChecked(user, slot),
                baseId = journey
            ).also {
                it.toDiscordEmbed()
                    .also {
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