package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.management.DiscordPlayerCacheService
import de.hydrum.toonworld.raid.RaidService
import de.hydrum.toonworld.raid.toDiscordEmbed
import de.hydrum.toonworld.raid.toRaidDiscordEmbed
import de.hydrum.toonworld.raid.toSinglePlayerDiscordEmbed
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionFollowupCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class RaidCommand(
    private val playerCacheService: DiscordPlayerCacheService,
    private val raidService: RaidService
) : BaseCommand(
    name = "raid",
    description = "Retrieve a raid report",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("player")
            .description("Retrieve a the last raid performances of a player")
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
                        .name("csv")
                        .description("default is false. if true, the report is returned as csv")
                        .type(Type.BOOLEAN.value)
                        .required(false)
                        .build()
                )
            )
            .build(),
        ApplicationCommandOptionData.builder()
            .name("guild")
            .description("Retrieve a raid report of a guild")
            .type(Type.SUB_COMMAND_GROUP.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("overview")
                        .description("Retrieve the overall raid performances of a guild")
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
                                    .name("csv")
                                    .description("default is false. if true, the report is returned as csv")
                                    .type(Type.BOOLEAN.value)
                                    .required(false)
                                    .build()
                            )
                        )
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("last")
                        .description("Retrieve the last raid performance of a guild (offset-based)")
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
                                    .name("offset")
                                    .description("default is 0 (latest raid)")
                                    .type(Type.INTEGER.value)
                                    .required(false)
                                    .build(),
                                ApplicationCommandOptionData.builder()
                                    .name("csv")
                                    .description("default is false. if true, the report is returned as csv")
                                    .type(Type.BOOLEAN.value)
                                    .required(false)
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

        val isPlayerOption = getOption("player").getOrNull() != null
        val isOverviewOption = getOption("guild").getOrNull()?.getOption("overview")?.getOrNull() != null
        val isLastOption = getOption("guild").getOrNull()?.getOption("last")?.getOrNull() != null

        fun getGuildBaseOption() = getOption("guild").getOrNull()?.getOption("last")
            ?.orElseGet { getOption("guild").getOrNull()?.getOption("overview")?.getOrNull() }

        fun getBaseOption() = if (isPlayerOption) getOption("player").getOrNull() else getGuildBaseOption()

        val guildId = getGuildBaseOption()?.getOption("id")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val allyCode = getBaseOption()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getBaseOption()?.getOption("slot")?.flatMap { it.value }?.map { it.asLong() }?.orElse(null) ?: 0
        val offset = getGuildBaseOption()?.getOption("offset")?.flatMap { it.value }?.map { it.asLong() }?.orElse(null) ?: 0
        val csv = getBaseOption()?.getOption("csv")?.flatMap { it.value }?.map { it.asBoolean() }?.orElse(null) ?: false

        val userOption = getBaseOption()?.getOption("user")?.flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.orElse(null)
        val user = if (userOption?.isPresent == true) userOption.get() else interaction.user

        runCatching {
            if (isPlayerOption) {
                raidService.getGuildMemberRaidPerformance(
                    swgohGuildId = playerCacheService.getGuildIdChecked(interaction.user, slot),
                    allyCode = allyCode ?: playerCacheService.getAllyCodeChecked(user, slot)
                ).also {
                    it?.toSinglePlayerDiscordEmbed(csv)?.forEach {
                        createFollowup(
                            InteractionFollowupCreateSpec
                                .builder()
                                .addEmbed(it)
                                .build()
                        ).subscribe()
                    } ?: createFollowup("no raid performance found").subscribe()
                }

            } else if (isOverviewOption) {
                raidService.getGuildRaidOverview(
                    swgohGuildId = guildId ?: playerCacheService.getGuildIdChecked(user, slot)
                ).also {
                    if (it.isEmpty()) createFollowup("no raids found").subscribe()
                    else it.toDiscordEmbed(csv).forEach {
                        createFollowup(
                            InteractionFollowupCreateSpec
                                .builder()
                                .addEmbed(it)
                                .build()
                        ).subscribe()
                    }
                }
            } else if (isLastOption) {
                raidService.getGuildRaidDetails(
                    swgohGuildId = guildId ?: playerCacheService.getGuildIdChecked(user, slot),
                    offset = offset
                ).also {
                    it?.toRaidDiscordEmbed(csv)?.forEach {
                        createFollowup(
                            InteractionFollowupCreateSpec
                                .builder()
                                .addEmbed(it)
                                .build()
                        ).subscribe()
                    } ?: createFollowup("no raid found").subscribe()
                }
            }


        }.onFailure { handleError(this, it) }
    }
}