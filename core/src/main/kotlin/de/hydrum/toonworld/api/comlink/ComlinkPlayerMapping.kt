package de.hydrum.toonworld.api.comlink

import de.hydrum.toonworld.api.comlink.models.PlayerProfileStat
import de.hydrum.toonworld.player.database.model.GacLeague
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.unit.UnitCacheService
import de.hydrum.toonworld.util.elseTake
import de.hydrum.toonworld.util.firstOrDefault
import de.hydrum.toonworld.util.utcNow
import java.time.Instant
import java.time.LocalTime
import de.hydrum.toonworld.api.comlink.models.Player as ComlinkPlayer
import de.hydrum.toonworld.api.comlink.models.RosterUnit as ComlinkPlayerUnit
import de.hydrum.toonworld.player.database.model.Player as ToonWorldPlayer
import de.hydrum.toonworld.player.database.model.PlayerUnit as ToonWorldPlayerUnit
import de.hydrum.toonworld.player.database.model.PlayerUnitAbility as ToonWorldPlayerUnitAbility
import de.hydrum.toonworld.unit.database.model.Unit as ToonWorldUnit
import de.hydrum.toonworld.unit.database.model.UnitAbility as ToonWorldUnitAbility


private const val GALACTIC_POWER_STAT_NAME = "STAT_GALACTIC_POWER_ACQUIRED_NAME"
private const val GALACTIC_WAR_WON_STAT_NAME = "STAT_TOTAL_GALACTIC_WON_NAME_TU07_2"
private const val GUILD_CONTRIBUTION_STAT_NAME = "STAT_TOTAL_GUILD_CONTRIBUTION_NAME_TU07_2"

private fun Iterable<PlayerProfileStat>.findOrZero(key: String): String = find { it.nameKey == key }?.value.elseTake { "0" }

infix fun ComlinkPlayer.updates(player: ToonWorldPlayer): ToonWorldPlayer {
    player.allyCode = allyCode
    player.swgohPlayerId = playerId
    player.name = name
    player.updateTime = utcNow()
    player.galacticPower = profileStat.findOrZero(GALACTIC_POWER_STAT_NAME).toLong()
    player.gacSkillRating = playerRating.playerSkillRating?.skillRating
    player.gacDivision = playerRating.playerRankStatus?.divisionId
    player.gacLeague = playerRating.playerRankStatus?.let { GacLeague.valueOf(it.leagueId) }
    player.galacticWarWon = profileStat.findOrZero(GALACTIC_WAR_WON_STAT_NAME).toInt()
    player.guildTokensEarned = profileStat.findOrZero(GUILD_CONTRIBUTION_STAT_NAME).toInt()
    player.swgohGuildId = guildId
    player.guildName = guildName
    player.lastActivityTime = Instant.ofEpochMilli(lastActivityTime.toLong())
    player.resetTime = LocalTime.parse("00:00:00.000").minusMinutes(localTimeZoneOffsetMinutes.toLong())
    player.level = level
    player.fleetArenaRank = pvpProfile.find { it.tab == 2 }?.rank
    player.squadArenaRank = pvpProfile.find { it.tab == 1 }?.rank
    rosterUnit.map { rosterUnit ->
        player.units.find { it.baseId == rosterUnit.definitionId.split(":").first() }
            ?.updates(rosterUnit)
            .elseTake { rosterUnit.toEntity(player) }
    }.also { player.units.clear() }.also { player.units.addAll(it) }
    return player
}

fun ComlinkPlayer.toEntity() =
    ToonWorldPlayer(
        id = null,
        allyCode = allyCode,
        swgohPlayerId = playerId,
        name = name,
        updateTime = utcNow(),
        galacticPower = profileStat.findOrZero(GALACTIC_POWER_STAT_NAME).toLong(),
        gacSkillRating = playerRating.playerSkillRating?.skillRating,
        gacDivision = playerRating.playerRankStatus?.divisionId,
        gacLeague = playerRating.playerRankStatus?.let { GacLeague.valueOf(it.leagueId) },
        galacticWarWon = profileStat.findOrZero(GALACTIC_WAR_WON_STAT_NAME).toInt(),
        guildTokensEarned = profileStat.findOrZero(GUILD_CONTRIBUTION_STAT_NAME).toInt(),
        swgohGuildId = guildId,
        guildName = guildName,
        lastActivityTime = Instant.ofEpochMilli(lastActivityTime.toLong()),
        resetTime = LocalTime.parse("00:00:00.000").minusMinutes(localTimeZoneOffsetMinutes.toLong()),
        level = level,
        squadArenaRank = pvpProfile.find { it.tab == 1 }?.rank,
        fleetArenaRank = pvpProfile.find { it.tab == 2 }?.rank,
        units = rosterUnit.map { unit -> unit.toEntity(null) }.toMutableList()
    ).also { it.units.forEach { unit -> unit.player = it } }


fun ToonWorldPlayerUnit.updates(unit: ComlinkPlayerUnit): ToonWorldPlayerUnit {
    baseId = unit.definitionId.split(":").first()
    gearLevel = unit.currentTier
    level = unit.currentLevel
    rarity = unit.currentRarity
    relicTier = RelicTier.entries.firstOrDefault(unit.relic?.currentTier, RelicTier.NONE)
    hasUltimate = unit.purchasedAbilityId.any { it.contains("ultimateability") }
    // abilities, zetas, omicrons have to be updated elsewhere via ComlinkPlayer.updatesAbilitiesOf
    return this
}

fun ComlinkPlayerUnit.toEntity(player: ToonWorldPlayer?) =
    ToonWorldPlayerUnit(
        id = null,
        player = player,
        baseId = definitionId.split(":").first(),
        gearLevel = currentTier,
        level = currentLevel,
        rarity = currentRarity,
        relicTier = RelicTier.entries.firstOrDefault(relic?.currentTier, RelicTier.NONE),
        hasUltimate = purchasedAbilityId.any { it.contains("ultimateability") },
        // abilities, zetas, omicrons have to be updated elsewhere via ComlinkPlayer.updatesAbilitiesOf
        zetas = 0,
        omicrons = 0,
    )

fun ToonWorldUnit.createOrUpdateAbilitiesFor(playerUnit: ToonWorldPlayerUnit, comlinkUnit: ComlinkPlayerUnit): List<ToonWorldPlayerUnitAbility> =
    abilities.map { ability ->
        playerUnit.abilities
            .find { it.baseId == ability.baseId }
            .elseTake {
                ToonWorldPlayerUnitAbility(
                    id = null,
                    unit = playerUnit,
                    player = playerUnit.player, // beware: has to be set by this point
                    baseId = ability.baseId,
                    tier = 1,
                    hasOmega = false,
                    hasZeta = false,
                    hasOmicron = false
                )
            }.also { playerAbility ->
                comlinkUnit.skill.find { it.id == ability.baseId }
                    ?.also {
                        // for some reason comlink ability tiers are [null, 0, 1, ...]
                        playerAbility.tier = it.tier + 2
                        playerAbility.hasOmega = playerAbility.tier >= ability.getOmegaTier()
                        playerAbility.hasZeta = playerAbility.tier >= ability.getZetaTier()
                        playerAbility.hasOmicron = playerAbility.tier >= ability.getOmicronTier()
                    }
            }
    }.also {
        playerUnit.zetas = it.count { it.hasZeta }
        playerUnit.omicrons = it.count { it.hasOmicron }
    }


fun ComlinkPlayer.updatesAbilitiesOf(player: ToonWorldPlayer, unitCacheService: UnitCacheService): ToonWorldPlayer {
    player.units
        .forEach { playerUnit ->
            val toonWorldUnit = unitCacheService.findUnit(playerUnit.baseId)
            // possible scenario: the toon was not added to the swgohgg api
            if (toonWorldUnit == null) return@forEach

            val comlinkPlayerUnit = requireNotNull(this.rosterUnit.find { it.definitionId.split(":").first() == playerUnit.baseId }) { "${playerUnit.baseId} cannot be found in Comlink response for player ${player.allyCode}" }
            val abilities = toonWorldUnit.createOrUpdateAbilitiesFor(playerUnit, comlinkPlayerUnit)
            playerUnit.abilities.clear()
            playerUnit.abilities.addAll(abilities)
        }
    return player
}

fun ToonWorldUnitAbility.getOmegaTier(): Int {
    var tier = maxTier
    if (isOmicron) tier--
    if (isZeta) tier--
    return if (isOmega) tier else Int.MAX_VALUE
}

fun ToonWorldUnitAbility.getZetaTier(): Int {
    var tier = maxTier
    if (isOmicron) tier--
    return if (isZeta) tier else Int.MAX_VALUE
}

fun ToonWorldUnitAbility.getOmicronTier(): Int = if (isOmicron) maxTier else Int.MAX_VALUE