package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.management.DiscordPlayerCacheService
import de.hydrum.toonworld.management.PlayerLinkService
import de.hydrum.toonworld.util.getNicknameOrNull
import de.hydrum.toonworld.util.returnNullOr
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.InteractionReplyEditSpec
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.rest.util.Color
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Component
class LinkCommand(
    private val playerLinkService: PlayerLinkService,
    private val playerCacheService: DiscordPlayerCacheService,
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
            .build(),
        ApplicationCommandOptionData.builder()
            .name("list")
            .description("show the linked accounts. it can either be from a user or from a swgoh guild")
            .type(Type.SUB_COMMAND.value)
            .options(
                listOf(
                    ApplicationCommandOptionData.builder()
                        .name("choice")
                        .description("please select whether to display all linked accounts of a player or a swgoh guild")
                        .type(Type.STRING.value)
                        .required(true)
                        .choices(
                            listOf(
                                ApplicationCommandOptionChoiceData.builder()
                                    .name("user - list all accounts that are registered for the user")
                                    .value("user")
                                    .build(),
                                ApplicationCommandOptionChoiceData.builder()
                                    .name("guild - list all accounts registered of a swgoh guild")
                                    .value("guild")
                                    .build()
                            )
                        )
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("user")
                        .description("user to be based of, if none provided, then your user is taken")
                        .type(Type.USER.value)
                        .required(false)
                        .build(),
                    ApplicationCommandOptionData.builder()
                        .name("slot")
                        .description("slot to be taken. if none provided, primary slot is used")
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

        val isAddOption = getOption("add").getOrNull() != null
        val isRemoveOption = getOption("remove").getOrNull() != null
        val isListUserOption = getOption("list").getOrNull()?.getOption("choice")?.getOrNull()?.value?.map { it.asString() }?.getOrNull() == "user"
        val isListGuildOption = getOption("list").getOrNull()?.getOption("choice")?.getOrNull()?.value?.map { it.asString() }?.getOrNull() == "guild"
        fun getBaseOption() = getOption("add").getOrElse { getOption("remove").orElseGet { getOption("list").getOrNull() } }

        val allyCode = getBaseOption()?.getOption("allycode")?.flatMap { it.value }?.map { it.asString() }?.orElse(null)
        val slot = getBaseOption()?.getOption("slot")?.flatMap { it.value }?.map { it.asLong() }?.orElse(null)

        val userOption = getBaseOption()?.getOption("user")?.flatMap { it.value }?.map { it.asUser().blockOptional(1.seconds.toJavaDuration()) }?.getOrNull()?.getOrNull()

        runCatching {
            val hasPermission = interaction.member.getOrNull()?.roleIds?.intersect(appConfig.discord.officerRoles.map { Snowflake.of(it) })?.isNotEmpty() == true
            if (userOption != null && !hasPermission) throw IllegalArgumentException("you don't have the permission to link other users.")

            val user = userOption ?: interaction.user
            val userName = user.getNicknameOrNull(interaction.guildId) ?: user.username

            when {
                isAddOption -> {
                    requireNotNull(allyCode)
                    val actualSlot = slot ?: 0L
                    playerLinkService.linkPlayer(user, allyCode, actualSlot)
                    editReply("linked `$allyCode` to `@${userName}` as ${if (actualSlot == 0L) "primary" else "$actualSlot"} slot").subscribe()
                }

                isRemoveOption -> {
                    if (allyCode == null && slot == null) {
                        editReply(":warning: please specify and ally code or a slot").subscribe()
                        return
                    }
                    val actualSlot = slot ?: 0L

                    val (unlinkedAllyCode, unlinkedSlot) = if (allyCode != null) playerLinkService.unlinkPlayer(user, allyCode) else playerLinkService.unlinkPlayer(user, actualSlot)
                    editReply("unlink `$unlinkedAllyCode` of ${if (actualSlot == 0L) "primary" else "$unlinkedSlot"} slot successful").subscribe()
                }

                isListUserOption -> {
                    if (slot != null) {
                        editReply(":warning: you cannot set a slot for the option `user`").subscribe()
                        return
                    }
                    val accounts = playerLinkService.listUserLinkedAccounts(user)
                    val text =
                        if (accounts.isEmpty()) "no accounts linked for @$userName"
                        else {
                            val maxNameLength = accounts.maxOf { it.playerName.length }
                            accounts.joinToString("\n") { "${it.slot}: ${it.allyCode} | ${it.playerName.padEnd(maxNameLength)} | ${it.guildName}" }
                        }
                    editReply(
                        InteractionReplyEditSpec.builder()
                            .addEmbed(
                                EmbedCreateSpec.builder()
                                    .title("Linked accounts")
                                    .color(Color.RUBY)
                                    .description("```$text```")
                                    .build()
                            )
                            .build()
                    ).subscribe()
                }

                isListGuildOption -> {
                    val accounts = playerLinkService.listGuildLinkAccounts(playerCacheService.getGuildIdChecked(user, slot ?: 0))
                    val linkedDiscordUserIds = accounts.mapNotNull { it.userId.returnNullOr { Snowflake.of(it) } }
                    val discordMembers = interaction.guild.flatMap { it.members.filter { it.id in linkedDiscordUserIds }.collectList() }.block(10.seconds.toJavaDuration())!!
                    val text = {
                        val maxPlayerNameLength = accounts.maxOf { it.playerName.length }
                        val maxDiscordUserNameLength = discordMembers.maxOf { it.displayName.length }
                        accounts.joinToString("\n") { info ->
                            val member = discordMembers.firstOrNull { member -> member.id.asLong() == info.userId }
                            val discordName = member?.displayName?.let { "@$it" } ?: ""
                            val slotText = member?.let { " [${info.slot ?: "--"}]" } ?: ""
                            "${info.allyCode} | ${info.playerName.padEnd(maxPlayerNameLength)} | ${discordName.padEnd(maxDiscordUserNameLength)}$slotText"
                        }
                    }
                    editReply(
                        InteractionReplyEditSpec.builder()
                            .addEmbed(
                                EmbedCreateSpec.builder()
                                    .title("Linked accounts for ${accounts.first { it.guildId.isNotBlank() }.guildName} on this server")
                                    .color(Color.RUBY)
                                    .description("```${text()}```")
                                    .build()
                            )
                            .build()
                    ).subscribe()
                }
            }
        }.onFailure { handleError(this, it, allyCode) }
    }
}