package de.hydrum.toonworld.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.ninjasquad.springmockk.MockkBean
import de.hydrum.toonworld.discord.RegisterCommands
import de.hydrum.toonworld.unit.UnitCacheScheduler
import discord4j.core.GatewayDiscordClient
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Tag("integration")
abstract class AbstractIntegrationTest {

    @MockkBean
    @Suppress("unused")
    lateinit var gatewayDiscordClient: GatewayDiscordClient

    @MockkBean
    @Suppress("unused")
    lateinit var registerDiscordCommands: RegisterCommands

    @MockkBean
    @Suppress("unused")
    lateinit var unitCacheScheduler: UnitCacheScheduler

    protected val wireMockServer = WireMockServer(WireMockConfiguration.options().dynamicPort())

    @PostConstruct
    fun startWireMock() {
        wireMockServer.start()
        // Configure the WireMock client to use the server's port
        WireMock.configureFor("localhost", wireMockServer.port())

        // Set system property for the application to use
        System.setProperty("wiremock.server.port", wireMockServer.port().toString())

        // Setup default stubs
        setupComlinkApiStubs()
        setupSwgohGgApiStubs()
    }

    @PreDestroy
    fun stopWireMock() {
        wireMockServer.stop()
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            // Global setup if needed
        }
    }

    @BeforeEach
    fun resetWireMock() {
        // Reset WireMock stubs before each test
        wireMockServer.resetAll()
        setupComlinkApiStubs()
        setupSwgohGgApiStubs()
    }

    protected fun setupComlinkApiStubs() {
        // Setup default stubs for Comlink API
        // Example:
        // WireMock.stubFor(
        //     WireMock.get(WireMock.urlPathMatching("/comlink/player.*"))
        //         .willReturn(WireMock.aResponse()
        //             .withStatus(200)
        //             .withHeader("Content-Type", "application/json")
        //             .withBodyFile("comlink/player_response.json"))
        // )
    }

    protected fun setupSwgohGgApiStubs() {
        // Setup default stubs for swgoh.gg API
        // Example:
        // WireMock.stubFor(
        //     WireMock.get(WireMock.urlPathMatching("/swgohgg/api/player.*"))
        //         .willReturn(WireMock.aResponse()
        //             .withStatus(200)
        //             .withHeader("Content-Type", "application/json")
        //             .withBodyFile("swgohgg/player_response.json"))
        // )
    }
}
