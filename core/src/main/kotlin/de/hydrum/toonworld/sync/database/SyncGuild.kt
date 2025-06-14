package de.hydrum.toonworld.sync.database

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sync_guilds")
class SyncGuild(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
    @Column(nullable = false) var swgohGuildId: String,
    @Column(nullable = false) var statsSyncEnabled: Boolean,
    @Column(nullable = false) var playerSyncEnabled: Boolean,
    var nextSync: Instant?,
    var lastSuccessSync: Instant?

)
