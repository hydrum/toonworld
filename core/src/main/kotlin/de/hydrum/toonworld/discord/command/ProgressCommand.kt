package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.management.DiscordPlayerCacheService
import de.hydrum.toonworld.progress.guild.GuildProgressReportService
import de.hydrum.toonworld.progress.guild.toDiscordEmbed
import de.hydrum.toonworld.progress.player.PlayerProgressReportService
import de.hydrum.toonworld.progress.player.toDiscordEmbed
import de.hydrum.toonworld.util.utcNow
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class ProgressCommand(
    private val playerProgressReportService: PlayerProgressReportService,
    private val guildProgressReportService: GuildProgressReportService,
    private val playerCacheService: DiscordPlayerCacheService,
    @Value("\${commands.player-progress.default-from:#{null}}") private val defaultFrom: Long?,
    @Value("\${commands.player-progress.default-to:#{null}}") private val defaultTo: Long?,
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
                        .name("user")
                        .description("default is none. if provided, the users linked allyCode is used")
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
    override fun handle(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val isPlayerOption = getOption("player").getOrNull() != null
        fun getBaseOption() = getOption("player").getOrElse { getOption("guild").getOrNull() }

        val guildId = getOption("guild").getOrNull()?.getOption("id")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val allyCode = getBaseOption()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getBaseOption()?.getOption("slot")?.flatMap { it.value }?.map { it.asLong() }?.orElse(0L) ?: 0L
        val from = getBaseOption()?.getOption("from")?.flatMap { it.value }?.map { it.asLong() }?.orElse(defaultFrom)
        val to = getBaseOption()?.getOption("to")?.flatMap { it.value }?.map { it.asLong() }?.orElse(defaultTo)

        val userOption = getBaseOption()?.getOption("user")?.flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.orElse(null)
        val user = if (userOption?.isPresent == true) userOption.get() else interaction.user

        runCatching {

            if (isPlayerOption) {

                playerProgressReportService.reportProgress(
                    allyCode = allyCode ?: playerCacheService.getAllyCodeChecked(user, slot),
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
                    swgohGuildId = guildId ?: playerCacheService.getGuildIdChecked(user, slot),
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