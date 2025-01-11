package de.hydrum.toonworld.discord

import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.discord.command.BaseCommand
import de.hydrum.toonworld.util.ErrorHelper
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.ReactiveEventAdapter
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.entity.Guild
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.interaction.GuildCommandRegistrar
import io.github.oshai.kotlinlogging.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Configuration
class RegisterCommands(
    private val appConfig: AppConfig,
    private val discordClient: GatewayDiscordClient,
    private val commands: List<BaseCommand>
) : InitializingBean {

    override fun afterPropertiesSet() {
        log.trace { "Registering Commands" }
        discordClient
            .guilds
            .collectList()
            .block()
            ?.filterNotNull()
            .let { it!! }
            .map { registerCommandForGuild(it) }
            .also { discordClient.on(CommandReactiveEventAdapter(commands, discordClient, appConfig)).subscribe() }
    }

    private fun registerCommandForGuild(guild: Guild) =
        with(guild) {
            commands
                .filter { !it.isSuperAdminCmd || id == Snowflake.of(appConfig.discord.superAdmin.guild) }
                .map { cmd ->
                    ApplicationCommandRequest
                        .builder()
                        .name(cmd.name)
                        .description(cmd.description)
                        .options(cmd.options)
                        .build()
                        .also { log.trace { "Register command \"${cmd.name}\" for $id | $name" } }
                }
                .let {
                    GuildCommandRegistrar
                        .create(discordClient.rest(), it)
                        .registerCommands(id)
                        .doOnError { e -> log.error(e) { "Unable to register commands" } }
                        .onErrorResume { Mono.empty() }
                        .blockLast()
                }
        }

    class CommandReactiveEventAdapter(private val commands: List<BaseCommand>, private val discordClient: GatewayDiscordClient, private val appConfig: AppConfig) : ReactiveEventAdapter() {
        override fun onChatInputInteraction(event: ChatInputInteractionEvent): Publisher<*> =
            Mono.just(event)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext {
                    runCatching {
                        commands.firstOrNull { event.commandName == it.name }?.callback(event)
                    }.onFailure { ErrorHelper(discordClient, appConfig).handleError(it) }
                }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }

}