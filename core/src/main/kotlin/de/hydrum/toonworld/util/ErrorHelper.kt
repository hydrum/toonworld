package de.hydrum.toonworld.util

import de.hydrum.toonworld.api.comlink.infrastructure.ClientError
import de.hydrum.toonworld.api.comlink.infrastructure.ClientException
import de.hydrum.toonworld.api.comlink.infrastructure.ServerError
import de.hydrum.toonworld.api.comlink.infrastructure.ServerException
import de.hydrum.toonworld.config.AppConfig
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ErrorHelper(
    private val discordClient: GatewayDiscordClient,
    private val appConfig: AppConfig
) {
    fun handleError(e: Throwable) = when (e) {
        is ClientException -> when (e.response) {
            is ClientError<*> -> "${e.response.body}"
            else -> "${e.response}"
        }

        is ServerException -> when (e.response) {
            is ServerError<*> -> "${e.response.body}"
            else -> "${e.response}"
        }

        else -> ""
    }.also { response ->
        if (e is IllegalArgumentException) log.debug { "$e" } // is expected if user input is faulty
        else {
            log.error(e) { "$e ${if (response.isNotEmpty()) response else ""}" }
            discordClient
                .getChannelById(Snowflake.of(appConfig.discord.superAdmin.errorChannel))
                .flatMap { channel -> channel.restChannel.createMessage(":warning: $e ${if (response.isNotEmpty()) response else ""}") }
                .subscribe()
        }
    }

    fun sendErrorMessage(message: String) =
        log.warn { message }
            .also {
                discordClient
                    .getChannelById(Snowflake.of(appConfig.discord.superAdmin.errorChannel))
                    .flatMap { channel -> channel.restChannel.createMessage(message) }
                    .subscribe()
            }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}