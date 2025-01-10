package de.hydrum.toonworld

import de.hydrum.toonworld.util.utcNow
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class UnitTest {


    @Test
    fun test() {
        log.info { "${utcNow()} vs ${ZonedDateTime.now().toInstant()} vs ${ZonedDateTime.now(ZoneId.of("UTC"))} " }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}