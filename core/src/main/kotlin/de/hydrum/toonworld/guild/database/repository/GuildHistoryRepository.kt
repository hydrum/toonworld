package de.hydrum.toonworld.guild.database.repository

import de.hydrum.toonworld.guild.database.model.Guild
import de.hydrum.toonworld.guild.database.model.GuildMember
import de.hydrum.toonworld.guild.database.model.GuildMemberLevel
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
class GuildHistoryRepository(private val jdbcTemplate: JdbcTemplate) {

    fun findEarliestSyncDateTime(swgohGuildId: String): Instant? =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_EARLIEST_SYNC_INSTANT,
                    paramCallback = {
                        it.setString(1, swgohGuildId)
                    }
                )
            ) { rs: ResultSet, rowNum: Int -> rs.getTimestamp("row_start").toInstant() }
            .firstOrNull()

    fun findGuildAtTimestamp(swgohGuildId: String, instant: Instant): Guild? =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_GUILD_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setString(2, swgohGuildId)
                    }
                ),
                GuildHistoryMapper()
            ).firstOrNull()
            ?.also { it.members.addAll(findGuildMembersAtTimestamp(checkNotNull(it.id), instant.plusSeconds(60))) }

    private fun findGuildMembersAtTimestamp(guildId: Long, instant: Instant): List<GuildMember> =
        jdbcTemplate
            .query(
                ToonWorldHistoryPreparedStatementCreator(
                    sql = SQL_GUILD_MEMBERS_AT_TIMESTAMP,
                    paramCallback = {
                        it.setTimestamp(1, Timestamp.from(instant))
                        it.setLong(2, guildId)
                    }
                ),
                GuildMemberHistoryMapper()
            )


    companion object {
        const val SQL_GUILD_AT_TIMESTAMP = "SELECT g.* FROM guilds FOR SYSTEM_TIME AS OF TIMESTAMP ? AS g WHERE g.swgoh_guild_id = ?"
        const val SQL_GUILD_MEMBERS_AT_TIMESTAMP = "SELECT g.* FROM guilds_members FOR SYSTEM_TIME AS OF TIMESTAMP ? AS g WHERE g.guild_id = ?"
        const val SQL_EARLIEST_SYNC_INSTANT = "SELECT g.row_start FROM guilds FOR SYSTEM_TIME ALL AS g WHERE g.swgoh_guild_id = ? ORDER BY g.row_start ASC LIMIT 1"
    }

    internal class ToonWorldHistoryPreparedStatementCreator(
        private val sql: String,
        private val paramCallback: (PreparedStatement) -> Unit
    ) : PreparedStatementCreator {

        override fun createPreparedStatement(con: Connection): PreparedStatement =
            con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                .also { paramCallback.invoke(it) }
    }

    private class GuildHistoryMapper : RowMapper<Guild> {
        override fun mapRow(rs: ResultSet, rowNum: Int): Guild? =
            Guild(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                updateTime = rs.getTimestamp("update_time").toInstant(),
                galacticPower = rs.getLong("galactic_power"),
                swgohGuildId = rs.getString("swgoh_guild_id"),
                bannerLogoId = rs.getString("banner_logo_id"),
                bannerColorId = rs.getString("banner_color_id"),
                memberCount = rs.getInt("member_count"),
                nextResetTime = rs.getTimestamp("update_time").toInstant()
            )
    }

    private class GuildMemberHistoryMapper : RowMapper<GuildMember> {
        override fun mapRow(rs: ResultSet, rowNum: Int): GuildMember? =
            GuildMember(
                id = rs.getLong("id"),
                guild = null,
                swgohPlayerId = rs.getString("swgoh_player_id"),
                name = rs.getString("name"),
                memberLevel = GuildMemberLevel.valueOf(rs.getString("member_level")),
                joinTime = rs.getTimestamp("join_time").toInstant(),
                galacticPower = rs.getLong("galactic_power"),
                lastActivityTime = rs.getTimestamp("last_activity_time").toInstant(),
                raidTickets = rs.getLong("raid_tickets_total"),
                guildTokens = rs.getLong("guild_tokens_total"),
                donations = rs.getLong("donations_total")
            )
    }
}