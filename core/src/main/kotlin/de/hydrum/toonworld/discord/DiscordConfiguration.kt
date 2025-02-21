package de.hydrum.toonworld.discord

import de.hydrum.toonworld.config.AppConfig
import discord4j.core.DiscordClient
import discord4j.core.GatewayDiscordClient
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfiguration {

    @Bean
    fun gatewayDiscordClient(config: AppConfig): GatewayDiscordClient = with(config.discord) {
        requireNotNull(
            DiscordClient.create(botToken)
                .gateway()
                .setEnabledIntents(IntentSet.of(Intent.GUILD_MEMBERS))
                .login()
                .block()
        ).also {
            it.eventDispatcher
            it.guilds.collectList().block()
                ?.forEach {
                    log.info { "Guilds: ${it.id} | ${it.name}" }
                }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}