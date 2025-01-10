package de.hydrum.toonworld.sync.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface SyncPlayerRepository : JpaRepository<SyncPlayer, Long> {

    @Query("SELECT nextSync FROM SyncPlayer WHERE playerSyncEnabled = true ORDER BY nextSync ASC LIMIT 1")
    fun findNextSyncTime(): Instant?

    @Query("SELECT SyncPlayer FROM SyncPlayer WHERE playerSyncEnabled = true ORDER BY nextSync ASC LIMIT 1")
    fun findNextToSync(): SyncPlayer?

    fun findFirstByPlayerSyncEnabledOrderByNextSync(playerSyncEnabled: Boolean): SyncPlayer?

    fun findByAllyCode(allyCode: String): SyncPlayer?
}

@Repository
interface SyncGuildRepository : JpaRepository<SyncGuild, Long> {

    @Query("SELECT nextSync FROM SyncGuild WHERE statsSyncEnabled = true ORDER BY nextSync ASC LIMIT 1")
    fun findNextSyncTime(): Instant?

    @Query("SELECT SyncPlayer FROM SyncGuild WHERE statsSyncEnabled = true ORDER BY nextSync ASC LIMIT 1")
    fun findNextToSync(): SyncGuild?

    fun findFirstByStatsSyncEnabledOrderByNextSync(statsSyncEnabled: Boolean): SyncGuild?

    fun findBySwgohGuildId(swgohGuildId: String): SyncGuild?
}