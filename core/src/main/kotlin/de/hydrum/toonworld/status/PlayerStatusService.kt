package de.hydrum.toonworld.status

import de.hydrum.toonworld.data.DataCacheService
import de.hydrum.toonworld.data.JourneyGuideProgress
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import de.hydrum.toonworld.unit.UnitCacheService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerStatusService(
    val dataCacheService: DataCacheService,
    val unitCacheService: UnitCacheService,
    val playerRepository: PlayerRepository
) {

    @Transactional
    fun getJourneyStatus(allyCode: String, journey: String): List<PlayerStatusJourney> =
        requireNotNull(playerRepository.findPlayerByAllyCode(allyCode)) { "No record found of allyCode $allyCode" }
            .let { player ->
                dataCacheService.getJourneyData()
                    .filter { it.baseId == journey }
                    .map { JourneyGuideProgress(it, player.units) }
                    .map { it.toStatus(player, unitCacheService) }
            }
            .sortedByDescending { it.totalProgress }
}