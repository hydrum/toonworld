package de.hydrum.toonworld.farm

import de.hydrum.toonworld.farm.database.FarmRepository
import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import de.hydrum.toonworld.unit.UnitCacheService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

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
                requireNotNull(farmRepository.getFarmByUnlockBaseId(baseId)) { "No farm found for baseId $baseId" }
                    .let { FarmProgress(it, player.units) }
                    .toStatus(player, unitCacheService)
            }

    @Transactional
    fun getGuildFarmStatus(discordGuildId: Long, farmId: Long): GuildFarmStatus {
        val discordGuild = requireNotNull(discordGuildRepository.findByDiscordGuildId(discordGuildId)) { "Guild not found" }
        val farm = requireNotNull(farmRepository.findById(farmId).getOrNull()) { "Farm not found" }
        val guild = requireNotNull(discordGuild.guild) { "SWGOH Guild not linked" }

        val farmName = if (farm.unlockBaseId == null) farm.name else unitCacheService.findUnit(farm.unlockBaseId!!)?.name ?: farm.name

        return GuildFarmStatus(
            guildName = guild.name,
            farmName = farmName,
            members = guild.members.mapNotNull { member ->
                member.player?.let { player ->
                    GuildMemberFarmStatus(
                        name = player.name,
                        progress = FarmProgress(farm, player.units).totalProgress
                    )
                }
            }.sortedByDescending { it.progress }
        )
    }
}