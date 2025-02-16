package de.hydrum.toonworld.player.progress.guild

import de.hydrum.toonworld.util.abvFormatting
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Duration

fun GuildProgressData.toDiscordEmbed(): List<EmbedCreateSpec> =
    """
        > Stats
        **Guild**:           `$name`
        **Guild id**:        `$guildId`
        **Data from**:       ${fromDateTime.toDiscordRelativeDateTime()}
        **Data to**:         ${toDateTime.toDiscordRelativeDateTime()}
        **Galactic Power**: `${galacticPowerGain.toValue.abvFormatting()}`
        
        > Total
        ```${toMemberProgressText()}```
        > Top 10
        ```${toMemberTopProgressText()}```
        > Bottom 10
        ```${toMemberLeastProgressText()}```
        > Least Raid Tickets
        ```${toMemberLeastRaidTicketsText()}```
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

fun GuildProgressData.toMemberProgressText() =
    members
        .sortedWith(
            compareByDescending<GuildMemberProgress> { it.galacticPowerGain.sortByValue() }
        )
        .take(50)
        .let {
            val maxNameLength = it.maxOf { it.name.length }
            val maxTotalLength = it.maxOf { it.galacticPowerGain.changeText().length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.changeText().padEnd(maxTotalLength)} | ${it.galacticPowerGain.toPctText()}" }
        }

fun GuildProgressData.toMemberTopProgressText() =
    members
        .sortedWith(
            compareByDescending<GuildMemberProgress> { it.galacticPowerGain.sortByChangeValue() }
        )
        .filter { it.galacticPowerGain.fromValue != null }
        .take(10)
        .let {
            val maxNameLength = it.maxOf { it.name.length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.toDiffText()}" }
        }

fun GuildProgressData.toMemberLeastProgressText() =
    members
        .sortedWith(
            compareBy<GuildMemberProgress> { it.galacticPowerGain.sortByChangeValue() }
        )
        .filter { it.galacticPowerGain.fromValue != null }
        .take(10)
        .let {
            val maxNameLength = it.maxOf { it.name.length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.toDiffText()}" }
        }

fun GuildProgressData.toMemberLeastRaidTicketsText() =
    members
        .map {
            val calculationDateTime = if (fromDateTime > it.joinDateTime) fromDateTime else it.joinDateTime
            val daysBetween = Duration.between(calculationDateTime, this.toDateTime).toDaysPart().let { if (it == 0L) 1 else it }
            Pair(it.name, (it.raidTickets.absGain ?: 0) / daysBetween)
        }
        .sortedWith(
            compareBy { it.second }
        )
        .take(10)
        .let {
            val maxNameLength = it.maxOf { it.first.length }
            it.joinToString("\n") { "${it.first.padEnd(maxNameLength)} | ${it.second}" }
        }
