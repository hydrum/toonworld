package de.hydrum.toonworld.guild.database.repository

import de.hydrum.toonworld.guild.database.model.Guild
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GuildRepository : JpaRepository<Guild, Long> {

    fun findBySwgohGuildId(swgohGuildId: String): Guild?

}