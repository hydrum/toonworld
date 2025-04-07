package de.hydrum.toonworld.farm

import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.unit.UnitCacheService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FarmService(
    private val discordGuildRepository: DiscordGuildRepository,
    private val unitCacheService: UnitCacheService
) {

    @Transactional
    fun getAllFarmsForGuildId(discordGuildId: Long) = discordGuildRepository.findByDiscordGuildId(discordGuildId)?.farms?.map { DiscordFarmWrapper(it, unitCacheService) }
}