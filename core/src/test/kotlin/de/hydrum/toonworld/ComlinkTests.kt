package de.hydrum.toonworld

import com.ninjasquad.springmockk.MockkBean
import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.discord.RegisterCommands
import discord4j.core.GatewayDiscordClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled
class ComlinkTests {

    @Autowired
    lateinit var comlinkApi: ComlinkApi

    @MockkBean
    lateinit var gatewayDiscordClient: GatewayDiscordClient

    @MockkBean
    lateinit var registerDiscordCommands: RegisterCommands

    @Test
    fun contextLoads() {
    }

    @Test
    fun getComlinkClient() {
        val player = comlinkApi.findPlayerByAllyCode(allyCode = "154962211")
        log.info { player.toString() }
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }

}
