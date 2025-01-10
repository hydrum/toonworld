package de.hydrum.toonworld.discord.command.admin

import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.sync.GuildSyncService
import de.hydrum.toonworld.sync.PlayerSyncService
import de.hydrum.toonworld.util.round
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class SyncCommand(
    val guildSyncService: GuildSyncService,
    val playerSyncService: PlayerSyncService
) : BaseCommand(
    name = "sync",
    description = "Forcing the sync of a guild or player",
    isSuperAdminCmd = true,
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("player")
            .description("force syncing a player")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("allycode")
                        .description("the ally code")
                        .type(Type.STRING.value)
                        .required(true)
                        .build()
                )
            )
            .build(),
        ApplicationCommandOptionData.builder()
            .name("guild")
            .description("force syncing a guild")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("id")
                        .description("the guild id")
                        .type(Type.STRING.value)
                        .required(true)
                        .build()
                )
            )
            .build()

    )
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        deferReply().subscribe()

        val allyCode = getOption("player").getOrNull()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val guildId = getOption("guild").getOrNull()?.getOption("id")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)

        if (allyCode != null) syncPlayer(this, allyCode)
        else if (guildId != null) syncGuild(this, guildId)
        else editReply().withContentOrNull("invalid selection").subscribe()
    }

    private fun syncPlayer(event: ChatInputInteractionEvent, allyCode: String) = with(event) {
        var duration = 0L
        runCatching {
            val startTime = System.currentTimeMillis()
            val result = playerSyncService.syncPlayer(allyCode = allyCode, force = true)
            duration = (System.currentTimeMillis() - startTime)
            return@runCatching result
        }.onSuccess {
            editReply("Player `${it?.name} (${it?.allyCode})` synced in ${duration}ms").subscribe()
        }.onFailure { handleError(this, it, allyCode) }
    }

    private fun syncGuild(event: ChatInputInteractionEvent, guildId: String) = with(event) {
        var duration = 0L
        runCatching {
            val startTime = System.currentTimeMillis()
            val result = guildSyncService.syncGuild(swgohGuildId = guildId, force = true)
            result?.members
                ?.map { it.swgohPlayerId }
                ?.also { playerSyncService.batchSyncPlayersBySwgohPlayerIds(it) }
            duration = (System.currentTimeMillis() - startTime)
            return@runCatching result
        }.onSuccess {
            editReply("Guild `${it?.name} (${it?.swgohGuildId})` synced in ${(duration / 1000.0).round(3)}s").subscribe()
        }.onFailure { handleError(this, it, guildId) }
    }
}