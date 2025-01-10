package de.hydrum.toonworld

import com.ninjasquad.springmockk.MockkBean
import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.discord.RegisterCommands
import de.hydrum.toonworld.jobs.JobStartupService
import de.hydrum.toonworld.player.database.repository.PlayerHistoryRepository
import de.hydrum.toonworld.player.progress.player.PlayerProgressReportService
import discord4j.core.GatewayDiscordClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled
class ToonWorldApplicationTests {

    @MockkBean
    lateinit var comlinkApi: ComlinkApi

    @MockkBean
    lateinit var gatewayDiscordClient: GatewayDiscordClient

    @MockkBean
    lateinit var registerDiscordCommands: RegisterCommands

    @MockkBean
    lateinit var jobStartupService: JobStartupService

    @Autowired
    lateinit var playerProgressReportService: PlayerProgressReportService

    @Autowired
    lateinit var playerHistoryRepository: PlayerHistoryRepository

    @Test
    fun test() {
        val result = playerProgressReportService.reportProgress(allyCode = "154962211", from = null, to = null)

        log.info { "1 ${result.player.allyCode} (update: ${result.player.updateTime}) ${result.upgradedUnits.size}" }
    }


    companion object {
        private val log = KotlinLogging.logger {}
    }

}
