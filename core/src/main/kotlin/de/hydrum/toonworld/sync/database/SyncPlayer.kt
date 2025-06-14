package de.hydrum.toonworld.sync.database

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sync_players")
class SyncPlayer(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
    @Column(nullable = false) var allyCode: String,
    var lastSuccessSync: Instant?,
    var nextSync: Instant?,
    @Column(nullable = false) var playerSyncEnabled: Boolean,
    @Column(nullable = false) var gacSyncEnabled: Boolean

)
