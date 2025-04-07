package de.hydrum.toonworld.management.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiscordGuildRepository : JpaRepository<DiscordGuild, Long> {

    fun findBySwgohGuildId(swgohGuildId: String): DiscordGuild?

    fun findByDiscordGuildId(discordGuildId: Long): DiscordGuild?

}