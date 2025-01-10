package de.hydrum.toonworld.api.comlink

import de.hydrum.toonworld.api.comlink.apis.GuildApi
import de.hydrum.toonworld.api.comlink.apis.PlayerApi
import de.hydrum.toonworld.api.comlink.models.*
import de.hydrum.toonworld.config.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ComlinkConfiguration {

    @Bean
    fun comlinkApi(config: AppConfig): ComlinkApi = with(config.api.comlink) {
        ComlinkApi(
            playerApi = PlayerApi(basePath = baseUrl),
            guildApi = GuildApi(basePath = baseUrl)
        )
    }

}

class ComlinkApi(
    val playerApi: PlayerApi,
    val guildApi: GuildApi
) {
    fun findPlayerByAllyCode(allyCode: String): Player =
        playerApi.findPlayer(
            FindPlayerRequest(
                payload = PlayerPayload(allyCode = allyCode),
                enums = false
            )
        )

    fun findPlayerById(playerId: String): Player =
        playerApi.findPlayer(
            FindPlayerRequest(
                payload = PlayerPayload(playerId = playerId),
                enums = false
            )
        )

    fun findGuildById(guildId: String): Guild =
        guildApi.getGuild(
            GetGuildRequest(
                payload = RetrieveGuildPayload(guildId = guildId, includeRecentGuildActivityInfo = true),
                enums = false
            )
        ).guild
}