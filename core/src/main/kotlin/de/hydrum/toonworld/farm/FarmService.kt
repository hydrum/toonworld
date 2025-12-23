package de.hydrum.toonworld.farm

import de.hydrum.toonworld.farm.database.FarmRepository
import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import de.hydrum.toonworld.unit.UnitCacheService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FarmService(
    private val discordGuildRepository: DiscordGuildRepository,
    private val unitCacheService: UnitCacheService,
    private val farmRepository: FarmRepository,
    private val playerRepository: PlayerRepository
) {

    @Transactional
    fun getAllFarmsForGuildId(discordGuildId: Long) = discordGuildRepository.findByDiscordGuildId(discordGuildId)?.ownedFarms?.map { DiscordFarmWrapper(it, unitCacheService) }

    @Transactional
    fun getJourneyGuideFarms() = farmRepository.getAllByUnlockBaseIdIsNotNull()

    @Transactional
    fun getFarmStatus(allyCode: String, baseId: String): PlayerFarmStatus =
        requireNotNull(playerRepository.findPlayerByAllyCode(allyCode)) { "No record found of allyCode $allyCode" }
            .let { player ->
                farmRepository.getFarmByUnlockBaseId(baseId)
                    ?.let { FarmProgress(it, player.units) }
                    ?.toStatus(player, unitCacheService)
                    ?: throw IllegalStateException("No farm found for baseId $baseId")
            }
}