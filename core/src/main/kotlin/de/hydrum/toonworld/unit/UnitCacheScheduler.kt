package de.hydrum.toonworld.unit

import de.hydrum.toonworld.sync.UnitSyncService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UnitCacheScheduler(
    private val unitSyncService: UnitSyncService,
    private val unitCacheService: UnitCacheService
) {

    @Scheduled(fixedDelayString = "\${app.sync.unit.fixed-delay-in-milliseconds}", initialDelayString = "\${app.sync.unit.initial-delay-in-milliseconds}")
    fun updateUnits() {
        runCatching {
            unitSyncService.updateUnits()
            unitCacheService.invalidateAndUpdate()
        }.onSuccess {
            log.info { "Unit & Abilities updated" }
        }.onFailure {
            log.error(it) { "updating units failed" }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}