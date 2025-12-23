package de.hydrum.toonworld.progress.player

import de.hydrum.toonworld.farm.FarmProgress
import de.hydrum.toonworld.farm.FarmService
import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.player.database.repository.PlayerHistoryRepository
import de.hydrum.toonworld.unit.UnitCacheService
import de.hydrum.toonworld.util.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class PlayerProgressReportService(
    private val playerHistoryRepository: PlayerHistoryRepository,
    private val unitCacheService: UnitCacheService,
    private val discordGuidRepository: DiscordGuildRepository,
    private val farmService: FarmService,
) {

    @Transactional
    fun reportProgress(allyCode: String, from: Instant?, to: Instant?): PlayerProgressData {
        // validation
        validateAllyCode(allyCode)

        val fromDate = from ?: requireNotNull(playerHistoryRepository.findEarliestSyncDateTime(allyCode)) { "No sync data found" }
        val toDate = to ?: utcNow()

        require(fromDate <= toDate) { "cannot find progress from future to past. please check your input." }

        log.trace { "trying to report progress for $allyCode in timeframe: $fromDate  ---  $toDate" }

        val fromPlayer = playerHistoryRepository.findPlayerAtTimestamp(allyCode = allyCode, instant = fromDate)
            ?: playerHistoryRepository.findEarliestSyncDateTime(allyCode = allyCode)
                .let { playerHistoryRepository.findPlayerAtTimestamp(allyCode = allyCode, instant = requireNotNull(it) { "No sync data found" }) }
                .let { checkNotNull(it) { "fromPlayer cannot be null as we ensured that there is an earliest time and therefore it should be able to retrieve such." } }
        val toPlayer = requireNotNull(playerHistoryRepository.findPlayerAtTimestamp(allyCode = allyCode, instant = toDate)) { "unexpected => please report with allyCode $allyCode and $toDate" }

        log.debug { "retrieved players: ${fromPlayer.allyCode} (update: ${fromPlayer.updateTime}) | ${toPlayer.allyCode} (update: ${toPlayer.updateTime})" }

        return compareProgress(fromPlayer, toPlayer)
    }

    @Transactional
    fun compareProgress(player1: Player, player2: Player): PlayerProgressData {
        val fromPlayer = if (player1.updateTime <= player2.updateTime) player1 else player2
        val toPlayer = if (player1.updateTime > player2.updateTime) player1 else player2

        // use journey progress by what the guild is using as farms.
        val toPlayerDiscordGuild = if (toPlayer.swgohGuildId != null) discordGuidRepository.findBySwgohGuildId(toPlayer.swgohGuildId!!) else null

        // TODO: maybe this section needs to be reworked.
        // the idea is to include Journey Guides + Farms that are important to the Guild (such as TB Farms like Zeffo/Reva/Mandalore-ready, ...)
        // but it should exclude other types like "any GL unlocked" or those which are there for role tracking.
        val farms = listOfNotNull(
            toPlayerDiscordGuild
                ?.farms
                ?.filter { it.announceChannelId != null }
                ?.map { it.farm },
            farmService.getJourneyGuideFarms()
        ).flatten().distinct()

        return PlayerProgressData(
            player = toPlayer,
            fromDateTime = fromPlayer.updateTime,
            toDateTime = toPlayer.updateTime,
            galacticPowerGain = fromPlayer.galacticPower gainToLong toPlayer.galacticPower,
            zetaGain = fromPlayer.units.sumOf { it.zetas } gainToLong toPlayer.units.sumOf { it.zetas },
            omicronGain = fromPlayer.units.sumOf { it.omicrons } gainToLong toPlayer.units.sumOf { it.omicrons },
            modsSpeed15 = fromPlayer.getModSpeedValues().filter { it >= 15 }.size gainToInt toPlayer.getModSpeedValues().filter { it >= 15 }.size,
            modsSpeed20 = fromPlayer.getModSpeedValues().filter { it >= 20 }.size gainToInt toPlayer.getModSpeedValues().filter { it >= 20 }.size,
            modsSpeed25 = fromPlayer.getModSpeedValues().filter { it >= 25 }.size gainToInt toPlayer.getModSpeedValues().filter { it >= 25 }.size,
            modsSixRarity = fromPlayer.units.flatMap { it.mods }.filter { it.rarity == 6 }.size gainToInt toPlayer.units.flatMap { it.mods }.filter { it.rarity == 6 }.size,
            mods25SpeedAverage = fromPlayer.getModSpeedValues().sortedDescending().take(25).average() gainToDouble toPlayer.getModSpeedValues().sortedDescending().take(25).average(),
            mods500SpeedAverage = fromPlayer.getModSpeedValues().sortedDescending().take(500).average() gainToDouble toPlayer.getModSpeedValues().sortedDescending().take(500).average(),
            upgradedUnits = toPlayer.units
                .map { toUnit -> Pair(fromPlayer.units.firstOrNull { fromUnit -> fromUnit.id == toUnit.id }, toUnit) }
                .map { (fromUnit, toUnit) ->
                    PlayerProgressUnit(
                        baseId = toUnit.baseId,
                        name = unitCacheService.findUnit(toUnit.baseId)?.name ?: toUnit.baseId,
                        gearLevelGain = fromUnit?.gearLevel gainToInt toUnit.gearLevel,
                        levelGain = fromUnit?.level gainToInt toUnit.level,
                        rarityGain = fromUnit?.rarity gainToInt toUnit.rarity,
                        relicTierGain = fromUnit?.relicTier gainToRelicTier toUnit.relicTier,
                        ultimateGain = fromUnit?.hasUltimate gainToBoolean toUnit.hasUltimate,
                        abilityGains =
                            toUnit.abilities.map { secondAbility -> Pair(secondAbility, fromUnit?.abilities?.firstOrNull { firstAbility -> secondAbility.baseId == firstAbility.baseId }) }
                                .map { (toAbility, fromAbility) ->
                                    GainUnitAbility(
                                        fromAbility = fromAbility,
                                        toAbility = toAbility,
                                        ability = checkNotNull(unitCacheService.findUnit(toUnit.baseId)?.abilities?.first { ability -> toAbility.baseId == ability.baseId })
                                    )
                                },
                    )
                }.filter { it.hasChanged() },
            farmProgress = farms
                .map { farm ->
                    val fromJourneyProgress = FarmProgress(farm, fromPlayer.units)
                    val toJourneyProgress = FarmProgress(farm, toPlayer.units)
                    PlayerProgressFarm(
                        farmId = farm.id!!,
                        farmName = if (farm.unlockBaseId == null) farm.name else (unitCacheService.findUnit(farm.unlockBaseId!!)?.name ?: farm.unlockBaseId!!),
                        totalProgressGain = fromJourneyProgress.totalProgress gainToDouble toJourneyProgress.totalProgress,
                        unitGains = farm.units.map { farmUnit ->
                            val fromUnit = fromJourneyProgress.relatedPlayerUnits.firstOrNull { it.playerUnit?.baseId == farmUnit.baseId }
                            val toUnit = toJourneyProgress.relatedPlayerUnits.firstOrNull { it.playerUnit?.baseId == farmUnit.baseId }
                            PlayerProgressFarmUnit(
                                unitName = unitCacheService.findUnit(farmUnit.baseId)?.name ?: farmUnit.baseId,
                                rarityProgress = fromUnit?.getRarityProgress() gainToDouble toUnit?.getRarityProgress(),
                                gearProgress = fromUnit?.getGearProgress() gainToDouble toUnit?.getGearProgress(),
                                relicProgress = fromUnit?.getRelicProgress() gainToDouble toUnit?.getRelicProgress(),
                                totalProgress = fromUnit?.getWeightedProgress() gainToDouble toUnit?.getWeightedProgress()
                            )
                        }
                    )
                }
                .filter { it.totalProgressGain.fromValue != 1.0 }
                .sortedByDescending { it.totalProgressGain.toValue }
        )
    }

    private fun Player.getModSpeedValues(): List<Long> =
        units
            .flatMap { it.mods }
            .map { it.getSecondarySpeed() }
            .ifEmpty { listOf(0) }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}