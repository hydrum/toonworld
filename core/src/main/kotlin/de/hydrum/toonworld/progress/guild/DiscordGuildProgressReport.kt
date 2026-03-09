package de.hydrum.toonworld.progress.guild

import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant
import java.time.temporal.ChronoUnit

fun GuildProgressData.calculateResetDays(joinedDateTime: Instant): Long {
    val calculationDateTime = if (fromDateTime > joinedDateTime) fromDateTime else joinedDateTime

    val latestReset = if (nextResetTime == toDateTime) nextResetTime else nextResetTime.minus(24, ChronoUnit.HOURS)

    if (calculationDateTime.isAfter(latestReset)) {
        return 1
    }

    val hoursBetween = calculationDateTime.until(latestReset, ChronoUnit.HOURS)
    val fullDaysBetween = hoursBetween / 24
    val resets = fullDaysBetween + 1

    return if (resets <= 0L) 1 else resets
}

fun GuildProgressData.toDiscordEmbed(type: ProgressType = ProgressType.GALACTIC_POWER): List<EmbedCreateSpec> =
    when (type) {
        ProgressType.GALACTIC_POWER -> """
            > Data
            **Guild**: `$name`
            **Guild id**: `$guildId`
            **Data from**: ${fromDateTime.toDiscordRelativeDateTime()}
            **Data to**: ${toDateTime.toDiscordRelativeDateTime()}
            
            > Stats
            ```${toStatsProgressText()}```
            > Total
            ```${toMemberProgressText()}```
            > Top 10
            ```${toMemberTopProgressText()}```
            > Bottom 10
            ```${toMemberLeastProgressText()}```
        """

        ProgressType.RAID_TICKETS -> """
            > Data
            **Guild**: `$name`
            **Guild id**: `$guildId`
            **Data from**: ${fromDateTime.toDiscordRelativeDateTime()}
            **Data to**: ${toDateTime.toDiscordRelativeDateTime()}
            
            > Raid Tickets Progress (Average)
            ```${toRaidTicketsAverageText()}```
        """
    }
        .trimIndent()
        .let {
            EmbedCreateSpec.builder()
                .color(Color.LIGHT_SEA_GREEN)
                .title("Progressreport - ${type.label}")
                .description(it)
                .build()
        }
        .let { listOfNotNull(it) }

fun GuildProgressData.toStatsProgressText() =
    "Galactic Power | ${galacticPowerGain.changeText()} | ${galacticPowerGain.toPctText()}"

fun GuildProgressData.toMemberProgressText() =
    members
        .sortedWith(
            compareByDescending { it.galacticPowerGain.sortByValue() }
        )
        .take(50)
        .let {
            if (it.isEmpty()) return@let "---"
            val maxNameLength = it.maxOf { it.name.length }
            val maxTotalLength = it.maxOf { it.galacticPowerGain.changeText().length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.changeText().padEnd(maxTotalLength)} | ${it.galacticPowerGain.toPctText()}" }
        }

fun GuildProgressData.toMemberTopProgressText() =
    members
        .sortedWith(
            compareByDescending { it.galacticPowerGain.sortByChangeValue() }
        )
        .filter { it.galacticPowerGain.fromValue != null }
        .take(10)
        .let {
            if (it.isEmpty()) return@let "---"
            val maxNameLength = it.maxOf { it.name.length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.toDiffText()}" }
        }

fun GuildProgressData.toMemberLeastProgressText() =
    members
        .sortedWith(
            compareBy { it.galacticPowerGain.sortByChangeValue() }
        )
        .filter { it.galacticPowerGain.fromValue != null }
        .take(10)
        .let {
            if (it.isEmpty()) return@let "---"
            val maxNameLength = it.maxOf { it.name.length }

            it.joinToString("\n") { "${it.name.padEnd(maxNameLength)} | ${it.galacticPowerGain.toDiffText()}" }
        }

fun GuildProgressData.toRaidTicketsAverageText() =
    members
        .map {
            val daysBetween = calculateResetDays(it.joinDateTime)
            val average = (it.raidTickets.absGain ?: 0) / daysBetween
            Pair(it.name, average)
        }
        .sortedWith(
            compareByDescending<Pair<String, Long>> { it.second }.thenBy { it.first }
        )
        .take(50)
        .let {
            if (it.isEmpty()) return@let "---"
            val maxNameLength = it.maxOf { it.first.length }
            val maxAvgLength = it.maxOf { it.second.toString().length }

            it.joinToString("\n") { "${it.first.padEnd(maxNameLength)} | ${it.second.toString().padStart(maxAvgLength)}" }
        }
