package de.hydrum.toonworld.sync

import de.hydrum.toonworld.util.ErrorHelper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.DisallowConcurrentExecution
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext
import org.springframework.stereotype.Service

@Service
@DisallowConcurrentExecution
class PlayerSyncJob(
    private val playerSyncService: PlayerSyncService,
    private val errorHelper: ErrorHelper
) : InterruptableJob {

    private lateinit var thread: Thread

    override fun execute(jobExecutionContext: JobExecutionContext?) {
        log.debug { "executing $JOB_NAME" }
        thread = Thread.currentThread()
        runCatching {
            playerSyncService.syncNextPlayer()
        }.onFailure { errorHelper.handleError(it) }

    }

    override fun interrupt() {
        thread.interrupt()
    }

    companion object {
        const val JOB_NAME = "PlayerSyncJob"

        private val log = KotlinLogging.logger { }
    }
}