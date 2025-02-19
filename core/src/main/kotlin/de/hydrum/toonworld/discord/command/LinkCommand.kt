package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.management.PlayerService
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.discordjson.json.ApplicationCommandOptionData
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class LinkCommand(
    private val playerService: PlayerService,
    private val appConfig: AppConfig
) : BaseCommand(
    name = "link",
    description = "link an ally code to a user. alternate accounts are supported",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("add")
            .description("link an ally code to a player. you may specify a slot to link multiple accounts")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("allycode")
                        .description("ally code of the account")
                        .type(Type.STRING.value)
                        .required(true)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("user")
                        .description("OPTIONAL. link another user to an ally code. requires to officer permissions")
                        .type(Type.USER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("slot")
                        .description("OPTIONAL. default is 0 (primary slot). specify a number to have shortcuts for multiple accounts")
                        .type(Type.INTEGER.value)
                        .required(false)
                        .build()
                )
            )
            .build(),
        ApplicationCommandOptionData.builder()
            .name("remove")
            .description("unlink an ally code. requires ally code OR slot. if both provided, ally code will be used")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("user")
                        .description("OPTIONAL. unlink another user of an ally code. requires to officer permissions")
                        .type(Type.USER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("allycode")
                        .description("OPTIONAL. ally code of the account")
                        .type(Type.STRING.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("slot")
                        .description("OPTIONAL. slot number of the account. 0 is the primary account slot")
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

        val isAddOption = getOption("add").getOrNull() != null
        fun getBaseOption() = getOption("add").getOrElse { getOption("remove").getOrNull() }


        val allyCode = getBaseOption()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getBaseOption()?.getOption("slot")?.flatMap { it.value }?.map { it.asLong() }?.orElse(null)

        val userOption = getBaseOption()?.getOption("user")?.flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.orElse(null)

        runCatching {
            val hasPermission = interaction.member.getOrNull()?.roleIds?.intersect(appConfig.discord.officerRoles.map { Snowflake.of(it) })?.isNotEmpty() == true
            if (userOption?.isPresent == true && !hasPermission) throw IllegalArgumentException("you don't have the permission to link other users.")

            val user = if (userOption?.isPresent == true) userOption.get() else interaction.user
            val userName = (if (interaction.guildId.isPresent) user.asMember(interaction.guildId.get()).blockOptional(1.seconds.toJavaDuration()).getOrNull()?.nickname?.getOrNull() else null) ?: user.username

            if (isAddOption) {
                requireNotNull(allyCode)
                val actualSlot = slot ?: 0L
                playerService.linkPlayer(user, allyCode, actualSlot)
                editReply("linked `$allyCode` to `@${userName}` as ${if (actualSlot == 0L) "primary" else "$actualSlot"} slot").subscribe()
            } else {
                if (allyCode == null && slot == null) {
                    editReply(":warning: please specify and allyCode or a slot").subscribe()
                    return
                }
                val actualSlot = slot ?: 0L

                val (unlinkedAllyCode, unlinkedSlot) = if (allyCode != null) playerService.unlinkPlayer(user, allyCode) else playerService.unlinkPlayer(user, actualSlot)
                editReply("unlink `$unlinkedAllyCode` of ${if (actualSlot == 0L) "primary" else "$unlinkedSlot"} slot successful").subscribe()
            }
        }.onFailure { handleError(this, it, allyCode) }
    }
}