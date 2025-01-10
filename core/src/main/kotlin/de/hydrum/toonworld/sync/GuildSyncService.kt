package de.hydrum.toonworld.sync

import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.api.comlink.toEntity
import de.hydrum.toonworld.api.comlink.updatedBy
import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.guild.database.model.Guild
import de.hydrum.toonworld.guild.database.repository.GuildRepository
import de.hydrum.toonworld.jobs.JobService
import de.hydrum.toonworld.sync.database.SyncGuildRepository
import de.hydrum.toonworld.util.utcNow
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK
import org.springframework.transaction.event.TransactionalEventListener

@Service
class GuildSyncService(
    private val appConfig: AppConfig,
    private val syncGuildRepository: SyncGuildRepository,
    private val guildRepository: GuildRepository,
    private val comlinkApi: ComlinkApi,
    private val playerSyncService: PlayerSyncService,
    private val jobService: JobService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun syncGuild(swgohGuildId: String, force: Boolean = false): Guild? {
        var guild = guildRepository.findBySwgohGuildId(swgohGuildId)
        if (!force && guild != null && utcNow().isBefore(guild.updateTime.plusSeconds(appConfig.sync.guild.delayInSeconds))) {
            log.warn { "Sync skipped for guild $swgohGuildId because it is too early." }
            return null
        }
        log.debug { "Start sync for guild $swgohGuildId" }

        val comlinkGuild = comlinkApi.findGuildById(swgohGuildId)
        log.debug { "Guild retrieved: ${comlinkGuild.profile.name} (${comlinkGuild.profile.id})" }

        guild = guild ?: comlinkGuild.toEntity()

        // if player is not new, update the reference
        if (guild.id != null) guild updatedBy comlinkGuild

        return guildRepository.save(guild)
    }

    @Transactional
    fun syncNextGuild() {
        syncGuildRepository.findFirstByStatsSyncEnabledOrderByNextSync(true)
            .also { log.trace { "guild scheduled for syncing ${it?.swgohGuildId}" } }
            ?.also { applicationEventPublisher.publishEvent(AfterGuildSyncEvent(swgohGuildId = it.swgohGuildId)) }
            ?.let { syncGuild -> syncGuild to syncGuild(swgohGuildId = syncGuild.swgohGuildId, force = true) }
            ?.also { (syncGuild, guild) ->
                syncGuild.lastSuccessSync = guild?.updateTime
                if (syncGuild.playerSyncEnabled) {
                    guild?.members
                        ?.map { it.swgohPlayerId }
                        ?.let { playerSyncService.batchSyncPlayersBySwgohPlayerIds(it) }
                }
            }

    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleSuccessSync(event: AfterGuildSyncEvent) = with(event) {
        syncGuildRepository.findBySwgohGuildId(swgohGuildId = swgohGuildId)
            ?.also {
                it.nextSync = (it.lastSuccessSync ?: utcNow()).plusSeconds(appConfig.sync.guild.delayInSeconds)
                log.trace { "Guild $swgohGuildId successfully synced." }
            }
            .also { syncGuildRepository.findNextSyncTime()?.let { jobService.scheduleNextGuildSyncJob(it) } }
    }

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFailureSync(event: AfterGuildSyncEvent) = with(event) {
        syncGuildRepository.findBySwgohGuildId(swgohGuildId = swgohGuildId)
            ?.also {
                it.nextSync = utcNow().plusSeconds(appConfig.sync.guild.errorDelayInSeconds)
                log.trace { "Guild $swgohGuildId failed to sync." }
            }
            .also { syncGuildRepository.findNextSyncTime()?.let { jobService.scheduleNextGuildSyncJob(it) } }
    }

    class AfterGuildSyncEvent(val swgohGuildId: String)

    companion object {
        private val log = KotlinLogging.logger {}
    }
}