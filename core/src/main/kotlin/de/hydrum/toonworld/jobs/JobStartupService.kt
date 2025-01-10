package de.hydrum.toonworld.jobs

import de.hydrum.toonworld.sync.database.SyncGuildRepository
import de.hydrum.toonworld.sync.database.SyncPlayerRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobStartupService(
    val syncPlayerRepository: SyncPlayerRepository,
    val syncGuildRepository: SyncGuildRepository,
    val jobService: JobService
) : InitializingBean {

    @Transactional
    override fun afterPropertiesSet() {
        syncPlayerRepository.findNextSyncTime()
            ?.also { log.info { "Initially scheduling PlayerSync to $it" } }
            ?.also { jobService.scheduleNextPlayerSyncJob(it) }
        syncGuildRepository.findNextSyncTime()
            ?.also { log.info { "Initially scheduling GuildSync to $it" } }
            ?.also { jobService.scheduleNextGuildSyncJob(it) }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}