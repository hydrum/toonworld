package de.hydrum.toonworld.sync

import de.hydrum.toonworld.api.comlink.*
import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.data.DataCacheService
import de.hydrum.toonworld.jobs.JobService
import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import de.hydrum.toonworld.sync.database.SyncPlayerRepository
import de.hydrum.toonworld.unit.UnitCacheService
import de.hydrum.toonworld.util.utcNow
import de.hydrum.toonworld.util.validateAllyCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT
import org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK
import org.springframework.transaction.event.TransactionalEventListener

@Service
class PlayerSyncService(
    private val appConfig: AppConfig,
    private val syncPlayerRepository: SyncPlayerRepository,
    private val playerRepository: PlayerRepository,
    private val unitCacheService: UnitCacheService,
    private val dataCacheService: DataCacheService,
    private val comlinkApi: ComlinkApi,
    private val jobService: JobService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun syncPlayer(allyCode: String, force: Boolean = false): Player? {
        // validation
        validateAllyCode(allyCode)

        var player = playerRepository.findPlayerByAllyCode(allyCode)
        if (!force && player != null && utcNow().isBefore(player.updateTime.plusSeconds(appConfig.sync.player.delayInSeconds))) {
            log.warn { "Sync skipped for $allyCode because it is too early." }
            return null
        }
        log.trace { "Start sync for $allyCode" }

        val comlinkPlayer = comlinkApi.findPlayerByAllyCode(allyCode)

        log.debug { "Player retrieved: ${comlinkPlayer.name} (${comlinkPlayer.allyCode})" }

        val dbPlayer = player ?: comlinkPlayer.toEntity()

        // if player is not new, update the reference
        if (dbPlayer.id != null) comlinkPlayer updates dbPlayer

        // we require the units to determine what abilities exist
        comlinkPlayer.updatesAbilitiesOf(dbPlayer, unitCacheService)
        comlinkPlayer.updatesModsOf(dbPlayer, dataCacheService.getModData())

        return playerRepository.save(dbPlayer)
    }

    @Transactional
    fun batchSyncPlayersBySwgohPlayerIds(swgohPlayerIds: List<String>): List<Player> =
        swgohPlayerIds
            .map { comlinkApi.findPlayerById(it) }
            .map { it to playerRepository.findPlayerBySwgohPlayerId(it.playerId) }
            .map { (comlinkPlayer, dbPlayer) -> comlinkPlayer to if (dbPlayer != null) comlinkPlayer updates dbPlayer else comlinkPlayer.toEntity() }
            .map { (comlinkPlayer, dbPlayer) -> comlinkPlayer to comlinkPlayer.updatesAbilitiesOf(dbPlayer, unitCacheService) }
            .map { (comlinkPlayer, dbPlayer) -> comlinkPlayer.updatesModsOf(dbPlayer, dataCacheService.getModData()) }
            .also { playerRepository.saveAll(it) }

    @Transactional
    fun syncNextPlayer() {
        syncPlayerRepository.findFirstByPlayerSyncEnabledOrderByNextSync(true)
            .also { log.trace { "player scheduled for syncing ${it?.allyCode}" } }
            ?.also { applicationEventPublisher.publishEvent(AfterPlayerSyncEvent(allyCode = it.allyCode)) }
            ?.let { syncPlayer -> syncPlayer to syncPlayer(allyCode = syncPlayer.allyCode, force = true) }
            ?.also { (syncPlayer, player) -> syncPlayer.lastSuccessSync = player?.updateTime }
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleSuccessSync(event: AfterPlayerSyncEvent) = with(event) {
        syncPlayerRepository.findByAllyCode(allyCode = allyCode)
            ?.also {
                it.nextSync = (it.lastSuccessSync ?: utcNow()).plusSeconds(appConfig.sync.player.delayInSeconds)
                log.trace { "player $allyCode successfully synced." }
            }
            .also { syncPlayerRepository.findNextSyncTime()?.let { jobService.scheduleNextPlayerSyncJob(it) } }
    }

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleFailureSync(event: AfterPlayerSyncEvent) = with(event) {
        syncPlayerRepository.findByAllyCode(allyCode = allyCode)
            ?.also {
                it.nextSync = utcNow().plusSeconds(appConfig.sync.player.errorDelayInSeconds)
                log.trace { "player $allyCode failed to sync." }
            }
            .also { syncPlayerRepository.findNextSyncTime()?.let { jobService.scheduleNextPlayerSyncJob(it) } }
    }

    class AfterPlayerSyncEvent(val allyCode: String)

    companion object {
        private val log = KotlinLogging.logger {}
    }
}