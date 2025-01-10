package de.hydrum.toonworld.sync.database

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sync_players")
class SyncPlayer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "ally_code", nullable = false)
    var allyCode: String,

    @Column(name = "last_success_sync")
    var lastSuccessSync: Instant?,

    @Column(name = "next_sync")
    var nextSync: Instant?,

    @Column(name = "player_sync_enabled", nullable = false)
    var playerSyncEnabled: Boolean,

    @Column(name = "gac_sync_enabled", nullable = false)
    var gacSyncEnabled: Boolean,
)