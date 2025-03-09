package de.hydrum.toonworld.progress.guild

import de.hydrum.toonworld.player.database.repository.PlayerHistoryRepository
import de.hydrum.toonworld.progress.player.PlayerProgressData
import de.hydrum.toonworld.progress.player.PlayerProgressReportService
import de.hydrum.toonworld.sync.GuildSyncService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import java.time.Instant

@Service
class GuildProgressService(
    private val playerHistoryRepository: PlayerHistoryRepository,
    private val playerProgressReportService: PlayerProgressReportService,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun reportGuildUpdate(event: GuildSyncService.AfterGuildSyncEvent) {
        Pair(
            playerHistoryRepository.findPlayersOfGuildAtTimestamp(guildId = event.swgohGuildId, Instant.now()),
            playerHistoryRepository.findPlayersOfGuildAtTimestamp(guildId = event.swgohGuildId, Instant.now().minusSeconds(60 * 5))
        )
            .let { (newPlayers, oldPlayers) -> newPlayers.map { newPlayer -> Pair(newPlayer, oldPlayers.find { it.swgohPlayerId == newPlayer.swgohPlayerId }) } }
            .filter { (_, oldPlayer) -> oldPlayer != null }
            .map { (newPlayer, oldPlayer) -> playerProgressReportService.compareProgress(oldPlayer!!, newPlayer) }
            .let { applicationEventPublisher.publishEvent(GuildProgressUpdateEvent(swgohGuildId = event.swgohGuildId, progress = it)) }

    }

    class GuildProgressUpdateEvent(val swgohGuildId: String, val progress: List<PlayerProgressData>)
}