package de.hydrum.toonworld.player.database.repository

import de.hydrum.toonworld.data.mods.ModSlot
import de.hydrum.toonworld.data.mods.ModTier
import de.hydrum.toonworld.data.mods.UnitStat
import de.hydrum.toonworld.player.database.model.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant

@Repository
class PlayerHistoryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findEarliestSyncDateTime(allyCode: String): Instant? =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_EARLIEST_SYNC_INSTANT,
                    paramCallback = {
                        it.setString(1, allyCode)
                    }
                )
            ) { rs: ResultSet, rowNum: Int -> rs.getTimestamp("row_start").toInstant() }
            .firstOrNull()

    fun findPlayerAtTimestamp(allyCode: String, instant: Instant): Player? =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_PLAYER_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setString(2, allyCode)
                    }
                ),
                PlayerHistoryMapper()
            ).firstOrNull()
            ?.also { mapHistoryData(it, instant) }

    fun findPlayersOfGuildAtTimestamp(guildId: String, instant: Instant): List<Player> =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_PLAYER_OF_GUILD_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setTimestamp(2, Timestamp.from(instant))
                        it.setTimestamp(3, Timestamp.from(instant))
                        it.setString(4, guildId)
                    }
                ),
                PlayerHistoryMapper()
            )
            .also { it.forEach { mapHistoryData(it, instant) } }

    fun mapHistoryData(player: Player, instant: Instant): Player = player
        .also { it.units.addAll(findPlayerUnitsAtTimestamp(checkNotNull(it.id), instant.plusSeconds(10))) }
        .also { player ->
            findPlayerUnitAbilitiesAtTimestamp(checkNotNull(player.id), instant.plusSeconds(10))
                .groupBy { it.playerUnitId }
                .forEach { playerUnitId, abilities ->
                    player.units.find { playerUnit -> playerUnit.id == playerUnitId }
                        ?.also { playerUnit -> playerUnit.abilities.clear() }
                        ?.also { playerUnit -> playerUnit.abilities.addAll(abilities) }
                }
        }
        .also { player ->
            findPlayerUnitModsAtTimestamp(checkNotNull(player.id), instant.plusSeconds(10))
                .groupBy { it.playerUnitId }
                .forEach { playerUnitId, mods ->
                    player.units.find { playerUnit -> playerUnit.id == playerUnitId }
                        ?.also { playerUnit -> playerUnit.mods.clear() }
                        ?.also { playerUnit -> playerUnit.mods.addAll(mods) }
                }
        }

    private fun findPlayerUnitsAtTimestamp(playerId: Long, instant: Instant): List<PlayerUnit> =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_PLAYER_UNITS_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setLong(2, playerId)
                    }
                ),
                PlayerUnitMapper()
            )

    private fun findPlayerUnitAbilitiesAtTimestamp(playerId: Long, instant: Instant): List<PlayerUnitAbility> =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_PLAYER_UNIT_ABILITIES_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setLong(2, playerId)
                    }
                ),
                PlayerUnitAbilityMapper()
            )

    private fun findPlayerUnitModsAtTimestamp(playerId: Long, instant: Instant): List<PlayerUnitMod> =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_PLAYER_UNIT_MODS_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setLong(2, playerId)
                    }
                ),
                PlayerUnitModMapper()
            )


    companion object {
        const val SQL_PLAYER_AT_TIMESTAMP = "SELECT p.* FROM players FOR SYSTEM_TIME AS OF TIMESTAMP ? AS p WHERE p.ally_code = ?"
        const val SQL_PLAYER_UNITS_AT_TIMESTAMP = "SELECT p.* FROM players_units FOR SYSTEM_TIME AS OF TIMESTAMP ? AS p WHERE p.player_id = ?"
        const val SQL_PLAYER_UNIT_ABILITIES_AT_TIMESTAMP = "SELECT p.* FROM players_units_abilities FOR SYSTEM_TIME AS OF TIMESTAMP ? AS p WHERE p.player_id = ?"
        const val SQL_PLAYER_UNIT_MODS_AT_TIMESTAMP = "SELECT p.* FROM players_units_mods FOR SYSTEM_TIME AS OF TIMESTAMP ? AS p WHERE p.player_id = ?"
        const val SQL_EARLIEST_SYNC_INSTANT = "SELECT p.row_start FROM players FOR SYSTEM_TIME ALL AS p WHERE p.ally_code = ? ORDER BY p.row_start ASC LIMIT 1"
        val SQL_PLAYER_OF_GUILD_AT_TIMESTAMP =
            """ SELECT p.* FROM guilds FOR SYSTEM_TIME AS OF TIMESTAMP ? AS g 
                JOIN guilds_members FOR SYSTEM_TIME AS OF TIMESTAMP ? AS gm ON gm.guild_id = g.id 
                JOIN players FOR SYSTEM_TIME AS OF TIMESTAMP ? AS p ON p.swgoh_player_id = gm.swgoh_player_id 
                WHERE g.swgoh_guild_id = ?
            """.trimIndent()
    }

    internal class ToonWorldHistoryPreparedStatementCreator(
        private val sql: String,
        private val paramCallback: (PreparedStatement) -> Unit
    ) : PreparedStatementCreator {

        override fun createPreparedStatement(con: Connection): PreparedStatement =
            con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                .also { paramCallback.invoke(it) }
    }

    private class PlayerHistoryMapper : RowMapper<Player> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Player =
            Player(
                id = rs.getLong("id"),
                allyCode = rs.getString("ally_code"),
                swgohPlayerId = rs.getString("swgoh_player_id") ?: "UNKNOWN",
                name = rs.getString("name"),
                updateTime = rs.getTimestamp("update_time").toInstant(),
                lastActivityTime = rs.getTimestamp("activity_time").toInstant(),
                resetTime = rs.getTime("reset_time").toLocalTime(),
                galacticPower = rs.getLong("galactic_power"),
                level = rs.getInt("level"),
                gacSkillRating = rs.getInt("gac_skill_rating"),
                gacDivision = rs.getInt("gac_division"),
                gacLeague = rs.getString("gac_league")?.let { GacLeague.valueOf(it) },
                galacticWarWon = rs.getInt("galactic_war_won"),
                guildTokensEarned = rs.getInt("guild_tokens_earned"),
                fleetArenaRank = rs.getInt("fleet_arena_rank"),
                squadArenaRank = rs.getInt("squad_arena_rank"),
                swgohGuildId = rs.getString("swgoh_guild_id"),
                guildName = rs.getString("guild_name")
            )
    }

    private class PlayerUnitMapper : RowMapper<PlayerUnit> {
        override fun mapRow(rs: ResultSet, rowNum: Int): PlayerUnit =
            PlayerUnit(
                id = rs.getLong("id"),
                player = null,
                baseId = rs.getString("base_id"),
                gearLevel = rs.getInt("gear_level"),
                level = rs.getInt("level"),
                rarity = rs.getInt("rarity"),
                relicTier = rs.getString("relic_tier").let { RelicTier.valueOf(it) },
                hasUltimate = rs.getBoolean("has_ultimate"),
                zetas = rs.getInt("zetas"),
                omicrons = rs.getInt("omicrons"),
            )
    }

    private class PlayerUnitAbilityMapper : RowMapper<PlayerUnitAbility> {
        override fun mapRow(rs: ResultSet, rowNum: Int): PlayerUnitAbility =
            PlayerUnitAbility(
                id = rs.getLong("id"),
                unit = null,
                playerUnitId = rs.getLong("player_unit_id"),
                player = null,
                playerId = rs.getLong("player_id"),
                baseId = rs.getString("base_id"),
                tier = rs.getInt("tier"),
                hasOmega = rs.getBoolean("has_omega"),
                hasZeta = rs.getBoolean("has_zeta"),
                hasOmicron = rs.getBoolean("has_omicron")
            )
    }

    private class PlayerUnitModMapper : RowMapper<PlayerUnitMod> {
        override fun mapRow(rs: ResultSet, rowNum: Int): PlayerUnitMod =
            PlayerUnitMod(
                id = rs.getLong("id"),
                unit = null,
                playerUnitId = rs.getLong("player_unit_id"),
                player = null,
                playerId = rs.getLong("player_id"),
                slot = ModSlot.entries.first { it.name == rs.getString("slot") },
                level = rs.getInt("level"),
                rarity = rs.getInt("rarity"),
                tier = ModTier.entries.first { it.name == rs.getString("tier") },
                modSet = UnitStat.entries.first { it.name == rs.getString("mod_set") },

                primaryStat = UnitStat.entries.first { it.name == rs.getString("primary_stat") },
                primaryValue = rs.getLong("primary_value"),

                secondary1Stat = UnitStat.entries.firstOrNull { it.name == rs.getString("secondary_1_stat") },
                secondary1Value = rs.getLong("secondary_1_value").let { if (rs.wasNull()) null else it },
                secondary1Roll = rs.getInt("secondary_1_roll").let { if (rs.wasNull()) null else it },

                secondary2Stat = UnitStat.entries.firstOrNull { it.name == rs.getString("secondary_2_stat") },
                secondary2Value = rs.getLong("secondary_2_value").let { if (rs.wasNull()) null else it },
                secondary2Roll = rs.getInt("secondary_2_roll").let { if (rs.wasNull()) null else it },

                secondary3Stat = UnitStat.entries.firstOrNull { it.name == rs.getString("secondary_3_stat") },
                secondary3Value = rs.getLong("secondary_3_value").let { if (rs.wasNull()) null else it },
                secondary3Roll = rs.getInt("secondary_3_roll").let { if (rs.wasNull()) null else it },

                secondary4Stat = UnitStat.entries.firstOrNull { it.name == rs.getString("secondary_4_stat") },
                secondary4Value = rs.getLong("secondary_4_value").let { if (rs.wasNull()) null else it },
                secondary4Roll = rs.getInt("secondary_4_roll").let { if (rs.wasNull()) null else it },
            )
    }
}