package de.hydrum.toonworld.guild.database.model

import de.hydrum.toonworld.player.database.model.Player
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "guilds_members")
data class GuildMember(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(name = "swgoh_player_id", nullable = false) var swgohPlayerId: String,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) @Enumerated(value = EnumType.STRING) var memberLevel: GuildMemberLevel,
    @Column(nullable = false) var joinTime: Instant,
    @Column(nullable = false) var galacticPower: Long,
    @Column(nullable = false) var lastActivityTime: Instant,
    @Column(name = "raid_tickets_total", nullable = false) var raidTickets: Long,
    @Column(name = "guild_tokens_total", nullable = false) var guildTokens: Long,
    @Column(name = "donations_total", nullable = false) var donations: Long,

    @OneToMany(mappedBy = "member")
    var raids: MutableList<GuildRaidMember> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "swgoh_player_id", referencedColumnName = "swgoh_player_id", insertable = false, updatable = false)
    var player: Player? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id", nullable = false)
    var guild: Guild?
)
