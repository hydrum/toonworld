package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.management.PlayerService
import de.hydrum.toonworld.player.progress.guild.GuildProgressReportService
import de.hydrum.toonworld.player.progress.guild.toDiscordEmbed
import de.hydrum.toonworld.player.progress.player.PlayerProgressReportService
import de.hydrum.toonworld.player.progress.player.toDiscordEmbed
import de.hydrum.toonworld.util.utcNow
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Component
class ProgressCommand(
    private val playerProgressReportService: PlayerProgressReportService,
    private val guildProgressReportService: GuildProgressReportService,
    @Value("\${commands.player-progress.default-from:#{null}}") private val defaultFrom: Long?,
    @Value("\${commands.player-progress.default-to:#{null}}") private val defaultTo: Long?,
    private val playerService: PlayerService,
) : BaseCommand(
    name = "progress",
    description = "Retrieve a progress report",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("player")
            .description("Retrieve a progress report of a player")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
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
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("from")
                        .description("default is ${defaultFrom ?: "first sync"}. If set, it uses now() - x days")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("to")
                        .description("default is ${defaultTo ?: "now"}. If set, it uses now() - x days")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build()
                )
            )
            .build(),
        ApplicationCommandOptionData.builder()
            .name("guild")
            .description("Retrieve a progress report of a guild")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("id")
                        .description("default is none. if none provided, the guild of the registered allyCode is used if available")
                        .type(Type.STRING.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("slot")
                        .description("default is 0 (primary slot). If no id is provided, it's guild is used")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("from")
                        .description("default is ${defaultFrom ?: "first sync"}. If set, it uses now() - x days")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("to")
                        .description("default is ${defaultTo ?: "now"}. If set, it uses now() - x days")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build()
                )
            )
            .build()
    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val isPlayerOption = getOption("player").getOrNull() != null

        val allyCode = getOption("player").getOrNull()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val guildId = getOption("guild").getOrNull()?.getOption("id")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getOption("player").getOrElse { getOption("guild").getOrNull() }?.getOption("slot")?.flatMap { it.value }?.map { it.asLong() }?.orElse(0L) ?: 0L
        val from = getOption("player").getOrElse { getOption("guild").getOrNull() }?.getOption("from")?.flatMap { it.value }?.map { it.asLong() }?.orElse(defaultFrom)
        val to = getOption("player").getOrElse { getOption("guild").getOrNull() }?.getOption("to")?.flatMap { it.value }?.map { it.asLong() }?.orElse(defaultTo)

        runCatching {

            if (isPlayerOption) {

                playerProgressReportService.reportProgress(
                    allyCode = allyCode ?: playerService.getAllyCodeChecked(interaction.user, slot),
                    from = from?.let { utcNow().minusSeconds(24 * 60 * 60 * it) },
                    to = to?.let { utcNow().minusSeconds(24 * 60 * 60 * it) }
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

            } else {
                guildProgressReportService.reportProgress(
                    swgohGuildId = guildId ?: playerService.getGuildIdChecked(interaction.user, slot),
                    from = from?.let { utcNow().minusSeconds(24 * 60 * 60 * it) },
                    to = to?.let { utcNow().minusSeconds(24 * 60 * 60 * it) }
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
            }


        }.onFailure { handleError(this, it) }
    }
}