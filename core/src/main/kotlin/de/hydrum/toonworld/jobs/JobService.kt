package de.hydrum.toonworld.jobs

import de.hydrum.toonworld.sync.GuildSyncJob
import de.hydrum.toonworld.sync.PlayerSyncJob
import io.github.oshai.kotlinlogging.KotlinLogging
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class JobService(
    private val scheduler: Scheduler,
    @param:Qualifier(PlayerSyncJob.JOB_NAME) private val playerSyncJobDetail: JobDetail,
    @param:Qualifier(GuildSyncJob.JOB_NAME) private val guildSyncJobDetail: JobDetail
) {

    fun scheduleNextPlayerSyncJob(startTime: Instant) = scheduleNextSyncJob(startTime, playerSyncJobDetail, PlayerSyncJob.JOB_NAME)
    fun scheduleNextGuildSyncJob(startTime: Instant) = scheduleNextSyncJob(startTime, guildSyncJobDetail, GuildSyncJob.JOB_NAME)

    private fun scheduleNextSyncJob(startTime: Instant, jobDetail: JobDetail, identity: String) {
        log.debug { "scheduling next $identity job to $startTime" }
        TriggerBuilder.newTrigger()
            .forJob(jobDetail)
            .withIdentity(identity)
            .startAt(Date.from(startTime))
            .build()
            .let { if (scheduler.checkExists(it.key)) scheduler.rescheduleJob(it.key, it) else scheduler.scheduleJob(it) }
            .also { log.trace { "trigger set for $it" } }
    }

    fun listSyncJobs() =
        scheduler.getTriggersOfJob(playerSyncJobDetail.key)
            .union(scheduler.getTriggersOfJob(guildSyncJobDetail.key))
            .toList()

    companion object {
        private val log = KotlinLogging.logger { }
    }
}