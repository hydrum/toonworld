package de.hydrum.toonworld.management.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiscordPlayerRepository : JpaRepository<DiscordPlayer, Long> {

    fun findByAllyCode(allyCode: String): DiscordPlayer?

    fun findByDiscordUserIdAndSlot(discordUserId: Long, slot: Long): DiscordPlayer?
    fun findByDiscordUserIdAndAllyCode(discordUserId: Long, allyCode: String): DiscordPlayer?

    fun existsByAllyCode(allyCode: String): Boolean

}