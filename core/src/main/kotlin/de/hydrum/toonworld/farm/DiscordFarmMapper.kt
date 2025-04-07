package de.hydrum.toonworld.farm

import de.hydrum.toonworld.farm.database.Farm
import de.hydrum.toonworld.farm.database.FarmUnit
import de.hydrum.toonworld.unit.UnitCacheService
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color

data class DiscordFarmWrapper(val farm: Farm, val unitCacheService: UnitCacheService) {
    fun toDiscordText(): String {
        val maxLengthRequired = farm.units.maxOf { it.requiredString().length }
        val maxLengthName = farm.units.maxOf { unitCacheService.findUnit(it.baseId)?.name?.length ?: 0 }
        val maxLengthRarity = farm.units.maxOf { it.minRarity.toString().length }
        val maxLengthGearLevel = farm.units.maxOf { it.minGearLevel.toString().length }
        return """> ${farm.name} (needed toons: ${farm.teamSize})
        ```${farm.units.joinToString("\n") { "${it.requiredString().padEnd(maxLengthRequired)}${(unitCacheService.findUnit(it.baseId)?.name ?: it.baseId).padEnd(maxLengthName)} | ${"${it.minRarity}*".padEnd(maxLengthRarity)} | G${it.minGearLevel.toString().padEnd(maxLengthGearLevel)} | ${it.minRelicTier.label}" }}```
    """.trimIndent()
    }

    fun FarmUnit.requiredString() = if (required) "REQ " else ""
}

fun List<DiscordFarmWrapper>.toDiscordEmbed(): List<EmbedCreateSpec> =
    let { it.joinToString("\n") { it.toDiscordText() } }
        .let {
            EmbedCreateSpec.builder()
                .color(Color.RUBY)
                .title("Farms")
                .description(it)
                .build()
        }.let { listOfNotNull(it) }