package de.hydrum.toonworld.player.database.repository

import de.hydrum.toonworld.player.database.model.Player
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<Player, Long> {

    fun findPlayerByAllyCode(allyCode: String): Player?
    fun findPlayerBySwgohPlayerId(swgohPlayerId: String): Player?
    
    fun findBySwgohPlayerIdIn(ids: List<String>): List<Player>

}