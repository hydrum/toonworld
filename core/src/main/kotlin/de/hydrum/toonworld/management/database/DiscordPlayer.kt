package de.hydrum.toonworld.management.database

import de.hydrum.toonworld.player.database.model.Player
import jakarta.persistence.*

@Entity
@Table(name = "discord_players")
class DiscordPlayer(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var allyCode: String,
    @Column(name = "swgoh_player_id", nullable = false) var swgohPlayerId: String,
    @Column(nullable = false) var discordUserId: Long,
    @Column(nullable = false) var slot: Long,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "swgoh_player_id", referencedColumnName = "swgoh_player_id", insertable = false, updatable = false)
    var player: Player? = null

)
