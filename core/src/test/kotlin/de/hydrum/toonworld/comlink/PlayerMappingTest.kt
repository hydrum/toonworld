package de.hydrum.toonworld.comlink

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import de.hydrum.toonworld.TestUtil
import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.api.comlink.models.Player
import de.hydrum.toonworld.api.comlink.toEntity
import de.hydrum.toonworld.api.comlink.updates
import de.hydrum.toonworld.discord.RegisterCommands
import de.hydrum.toonworld.jobs.JobStartupService
import discord4j.core.GatewayDiscordClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZonedDateTime

@SpringBootTest
@Disabled
class PlayerMappingTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var comlinkApi: ComlinkApi

    @MockkBean
    lateinit var jobStartupService: JobStartupService

    @MockkBean
    lateinit var gatewayDiscordClient: GatewayDiscordClient

    @MockkBean
    lateinit var registerDiscordCommands: RegisterCommands

    @Test
    fun `times are correctly parsed`() {
        val file = TestUtil().loadResource("/data/hydrum_20241021.json")
        val player = objectMapper.readValue(file, Player::class.java)

        val newPlayer = player.toEntity()
        val updatedPlayer = player updates player.toEntity()

        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1729534661000), ZoneId.of("UTC")), newPlayer.lastActivityTime)
        assertEquals(ZonedDateTime.ofInstant(Instant.ofEpochMilli(1729534661000), ZoneId.of("UTC")), updatedPlayer.lastActivityTime)

        assertEquals(OffsetTime.parse("21:00Z"), newPlayer.resetTime)
        assertEquals(OffsetTime.parse("21:00Z"), updatedPlayer.resetTime)
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}