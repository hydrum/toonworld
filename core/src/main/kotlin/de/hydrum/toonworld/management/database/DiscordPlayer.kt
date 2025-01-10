package de.hydrum.toonworld.management.database

import jakarta.persistence.*

@Entity
@Table(name = "discord_players")
class DiscordPlayer(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "ally_code", nullable = false)
    var allyCode: String,

    @Column(name = "swgoh_player_id", nullable = false)
    var swgohPlayerId: String,

    @Column(name = "discord_user_id", nullable = false)
    var discordUserId: Long,

    @Column(name = "slot", nullable = false)
    var slot: Long

)