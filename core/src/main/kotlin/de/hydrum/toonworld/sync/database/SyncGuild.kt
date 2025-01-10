package de.hydrum.toonworld.sync.database

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sync_guilds")
class SyncGuild(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "swgoh_guild_id", nullable = false)
    var swgohGuildId: String,

    @Column(name = "stats_sync_enabled", nullable = false)
    var statsSyncEnabled: Boolean,

    @Column(name = "player_sync_enabled", nullable = false)
    var playerSyncEnabled: Boolean,

    @Column(name = "next_sync")
    var nextSync: Instant?,

    @Column(name = "last_success_sync")
    var lastSuccessSync: Instant?,
)