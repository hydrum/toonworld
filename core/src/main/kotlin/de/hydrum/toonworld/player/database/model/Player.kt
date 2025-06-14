package de.hydrum.toonworld.player.database.model

import de.hydrum.toonworld.management.database.DiscordPlayer
import jakarta.persistence.*
import java.time.Instant
import java.time.LocalTime

@Entity
@Table(name = "players")
data class Player(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long?,
    @Column(nullable = false) var allyCode: String,
    @Column(name = "swgoh_player_id", nullable = false) var swgohPlayerId: String,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var updateTime: Instant,
    @Column(name = "activity_time", nullable = false) var lastActivityTime: Instant,
    @Column(nullable = false) var resetTime: LocalTime,
    @Column(nullable = false) var galacticPower: Long,
    @Column(nullable = false) var level: Int,
    var gacSkillRating: Int?,
    var gacDivision: Int?,
    @Enumerated(EnumType.STRING) var gacLeague: GacLeague?,
    @Column(nullable = false) var galacticWarWon: Int,
    @Column(nullable = false) var guildTokensEarned: Int,
    var fleetArenaRank: Int? = null,
    var squadArenaRank: Int? = null,
    var swgohGuildId: String?,
    var guildName: String?,

    @OneToMany(
        mappedBy = "player",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var units: MutableList<PlayerUnit> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "swgoh_player_id", referencedColumnName = "swgoh_player_id", insertable = false, updatable = false)
    var discordPlayer: DiscordPlayer? = null

)
