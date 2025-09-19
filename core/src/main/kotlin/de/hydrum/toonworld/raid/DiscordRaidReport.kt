package de.hydrum.toonworld.raid

import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.util.abvFormatting
import de.hydrum.toonworld.util.toDiscordRelativeDateTime
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class RaidDetails(val guildName: String, val guildId: String, val raidName: String, val endTs: Instant, val score: Long)
data class RaidMemberPerformance(val player: Player?, val guildName: String, val guildId: String, val raidName: String, val endTs: Instant, val score: Long)

fun List<RaidDetails>.toDiscordEmbed(csv: Boolean): List<EmbedCreateSpec> =
    with(sortedWith(compareByDescending { it.endTs })) {
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneOffset.UTC)
        (if (csv)
            buildString {
                appendLine("```raidName,endTs,score")
                append(joinToString("\n") { "${it.raidName},${dateFormatter.format(it.endTs)},${it.score}" })
                append("```")
            }
        else """
            **Guild**: `${first().guildName}`
            **Guild id**: `${first().guildId}`
            
            ${joinToString("\n") { "#${this.indexOf(it).toString().padStart(2, '0')} | ${it.raidName} | ${it.endTs.toDiscordRelativeDateTime()} | ${it.score.abvFormatting()}" }}
        """)
            .trimIndent()
            .let {
                EmbedCreateSpec.builder()
                    .color(Color.RUBY)
                    .title("Raid Overview")
                    .description(it)
                    .build()
            }
            .let { listOfNotNull(it) }
    }


fun List<RaidMemberPerformance>.toSinglePlayerDiscordEmbed(csv: Boolean): List<EmbedCreateSpec> =
    with(sortedWith(compareByDescending { it.endTs })) {
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneOffset.UTC)
        (if (csv)
            buildString {
                appendLine("```playerName,allyCode,raidName,endTs,score")
                append(joinToString("\n") { "${it.player?.name},${it.player?.allyCode},${it.raidName},${dateFormatter.format(it.endTs)},${it.score}" })
                append("```")
            }
        else """
            **Guild**: `${first().guildName}`
            **Guild id**: `${first().guildId}`
            **Player**: `${first().player?.name}`
            **AllyCode**: `${first().player?.allyCode}`
            
            ${joinToString("\n") { "${it.raidName} | ${it.endTs.toDiscordRelativeDateTime()} | ${it.score.abvFormatting()}" }}
        """)
            .trimIndent()
            .let {
                EmbedCreateSpec.builder()
                    .color(Color.RUBY)
                    .title("Raid Member Details")
                    .description(it)
                    .build()
            }
            .let { listOfNotNull(it) }
    }


fun List<RaidMemberPerformance>.toRaidDiscordEmbed(csv: Boolean): List<EmbedCreateSpec> =
    with(sortedWith(compareByDescending { it.score })) {
        fun playerName(player: Player?) = player?.name ?: "???"
        val maxNameLength = maxOf { playerName(it.player).length }
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy").withZone(ZoneOffset.UTC)
        (if (csv)
            buildString {
                appendLine("```raidName,playerName,allyCode,endTs,score")
                append(joinToString("\n") { "${it.raidName},${playerName(it.player)},${it.player?.allyCode},${dateFormatter.format(it.endTs)},${it.score}" })
                append("```")
            }
        else """
            **Guild**: `${first().guildName}`
            **Guild id**: `${first().guildId}`
            **Raid**: `${first().raidName}`
            **End**: ${first().endTs.toDiscordRelativeDateTime()}
            
            ```${joinToString("\n") { "${playerName(it.player).padEnd(maxNameLength)} | ${it.score.abvFormatting()}" }}```
        """)
            .trimIndent()
            .let {
                EmbedCreateSpec.builder()
                    .color(Color.RUBY)
                    .title("Raid Details")
                    .description(it)
                    .build()
            }
            .let { listOfNotNull(it) }
    }