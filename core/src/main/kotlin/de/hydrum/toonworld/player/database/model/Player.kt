package de.hydrum.toonworld.player.database.model

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalTime

@Entity
@Table(name = "players")
data class Player(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long?,

    @Column(name = "ally_code", nullable = false)
    var allyCode: String,

    @Column(name = "swgoh_player_id", nullable = false)
    var swgohPlayerId: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "update_time", nullable = false)
    var updateTime: Instant,

    @Column(name = "activity_time", nullable = false)
    var lastActivityTime: Instant,

    @Column(name = "reset_time", nullable = false)
    var resetTime: LocalTime,

    @Column(name = "galactic_power", nullable = false)
    var galacticPower: Long,

    @Column(name = "level", nullable = false)
    var level: Int,

    @Column(name = "gac_skill_rating")
    var gacSkillRating: Int?,

    @Column(name = "gac_division")
    var gacDivision: Int?,

    @Column(name = "gac_league")
    @Enumerated(EnumType.STRING)
    var gacLeague: GacLeague?,

    @Column(name = "galactic_war_won", nullable = false)
    var galacticWarWon: Int,

    @Column(name = "guild_tokens_earned", nullable = false)
    var guildTokensEarned: Int,

    @Column(name = "fleet_arena_rank")
    var fleetArenaRank: Int? = null,

    @Column(name = "squad_arena_rank")
    var squadArenaRank: Int? = null,

    @Column(name = "swgoh_guild_id")
    var swgohGuildId: String?,

    @Column(name = "guild_name")
    var guildName: String?,

    @OneToMany(
        mappedBy = "player",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var units: MutableList<PlayerUnit> = mutableListOf()
)