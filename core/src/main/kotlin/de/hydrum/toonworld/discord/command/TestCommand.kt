package de.hydrum.toonworld.discord.command

import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption.Type
import discord4j.core.spec.InteractionReplyEditSpec
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData
import discord4j.discordjson.json.ApplicationCommandOptionData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class TestCommand(private val gatewayDiscordClient: GatewayDiscordClient) : BaseCommand(
    name = "test",
    description = "Test command, logs info and replies test",
    options = listOf(
        ApplicationCommandOptionData.builder()
            .name("option1")
            .type(Type.BOOLEAN.value)
            .description("required option 1")
            .required(true)
            .choices(
                listOf(
                    ApplicationCommandOptionChoiceData.builder().name("TRUE").value("true").build(),
                    ApplicationCommandOptionChoiceData.builder().name("FALSE").value("false").build()
                )
            )
            .build(),

        ApplicationCommandOptionData.builder()
            .name("option2")
            .type(Type.STRING.value)
            .description("option 2")
            .build()
    ),
    isSuperAdminCmd = true
) {
    override fun callback(event: ChatInputInteractionEvent): Unit = with(event) {
        log.info {
            """
            Command "$commandName" arrived from ${interaction.user.username}
            Option1: ${this.getOption("option1").flatMap { it.value }.map { it.asBoolean() }.get()}
            Option2: ${this.getOption("option2").flatMap { it.value }.map { it.asString() }.getOrNull()}
            """
        }

        log.info { "instance: ${this.hashCode()}" }

        // normal:
//        event
//            .reply()
//            .withContent("test")
//            .subscribe()

        // delayed:
        event.deferReply().subscribe()

        Thread.sleep(20000)

        event.editReply(
            InteractionReplyEditSpec
                .builder()
                .build()
                .withContentOrNull("followup")
        ).subscribe()

//        gatewayDiscordClient.getChannelById(Snowflake.of(1230542316242993224))
//            .doOnError { log.info { "error while retrieving channel: $it" } }
//            .flatMap { it.restChannel.createMessage("my test") }
//            .doOnError { log.info { "error while sending message: $it" } }
//            .subscribe { log.info { "completed $it" } }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}