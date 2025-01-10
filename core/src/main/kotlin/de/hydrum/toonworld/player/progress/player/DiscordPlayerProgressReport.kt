package de.hydrum.toonworld.player.progress.player

import de.hydrum.toonworld.util.Gain
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color


fun PlayerProgressData.toDiscordEmbed(): List<EmbedCreateSpec> =
    """
        > Stats        
        **Player:**         `${player.name}`
        **Guild:**          `${player.guildName} (${player.swgohGuildId})`
        **AllyCode:**       `${player.allyCode}`
        **Data from:**       ${fromDateTime.toDiscordRelativeDateTime()}
        **Data to:**         ${toDateTime.toDiscordRelativeDateTime()}
        **Upgraded Toons:** `${upgradedUnits.size}`
        
        > Stats
        ```${toStatsText()}```
        > Stars / Gear / Relic
        ```${toToonGearText()}```
        > Abilities
        ```${toToonAbilityEmbedOrNull() ?: "---"}```
    """
        .trimIndent()
        .let {
            EmbedCreateSpec.builder()
                .color(Color.LIGHT_SEA_GREEN)
                .title("Progressreport")
                .description(it)
                .build()
        }
        .let { listOfNotNull(it) }

fun PlayerProgressData.toStatsText() =
    listOf(
        ProgressHolder("Galactic Power", galacticPowerGain),
        ProgressHolder("Zetas", zetaGain),
        ProgressHolder("Omicrons", omicronGain)
    ).let {
        val maxStatLength = it.maxOf { it.name.length }
        val maxPrevValueLength = it.maxOf { it.gain.formatValue(it.gain.fromValue).length }
        val maxCurrValueLength = it.maxOf { it.gain.formatValue(it.gain.toValue).length }
        val maxAbsGainLength = it.maxOf { it.gain.formatValue(it.gain.absGain).length }
        it.joinToString("\n") {
            "${it.name.padEnd(maxStatLength)} " +
                    "| ${it.gain.formatValue(it.gain.fromValue).padEnd(maxPrevValueLength)} " +
                    "| ${it.gain.formatValue(it.gain.toValue).padEnd(maxCurrValueLength)} " +
                    "| ${it.gain.formatValue(it.gain.absGain).padEnd(maxAbsGainLength)} " +
                    "| ${it.gain.toPctText()} "
        }
    }

fun PlayerProgressData.toToonGearText(): String =
    upgradedUnits
        .filter { it.rarityGain.hasChanged() || it.gearLevelGain.hasChanged() || it.relicTierGain.hasChanged() }
        .sortedWith(
            compareByDescending<PlayerProgressUnit> { it.relicTierGain.sortByValue() }.thenByDescending { it.relicTierGain.sortByChangeValue() }
                .thenByDescending { it.gearLevelGain.sortByValue() }.thenByDescending { it.gearLevelGain.sortByChangeValue() }
                .thenByDescending { it.rarityGain.sortByValue() }.thenByDescending { it.rarityGain.sortByChangeValue() }
        )
        .take(MAX_TOON_PROGRESS)
        .let {
            val maxToonLength = it.maxOf { it.name.length }
            val maxStarsLength = it.maxOf { it.rarityGain.changeText().length }
            it.joinToString("\n") { "${it.name.padEnd(maxToonLength)} | ${it.rarityGain.changeText().padEnd(maxStarsLength)} | ${if (it.relicTierGain.hasChanged()) it.relicTierGain.changeText() else "G" + it.gearLevelGain.changeText()}" }
        }

fun PlayerProgressData.toToonAbilityEmbedOrNull(): String? =
    upgradedUnits
        .filter { it.abilityGains.any { ability -> ability.hasZetaChanged() || ability.hasOmicronChanged() } }
        .take(MAX_TOON_PROGRESS)
        .flatMap { unit ->
            unit.abilityGains
                .filter { ability -> ability.hasZetaChanged() }
                .map { ability -> "zeta: ${ability.getAbilityName()}" }
                .union(
                    unit.abilityGains
                        .filter { ability -> ability.hasOmicronChanged() }
                        .map { ability -> "omi: ${ability.getAbilityName()}" }
                ).map { changeText -> Pair(unit, changeText) }
        }.let {
            val maxToonLength = it.maxOf { it.first.name.length }
            it.joinToString("\n") { "${it.first.name.padEnd(maxToonLength)} | ${it.second}" }
        }


internal data class ProgressHolder<T>(val name: String, val gain: Gain<T>)

const val MAX_TOON_PROGRESS = 50