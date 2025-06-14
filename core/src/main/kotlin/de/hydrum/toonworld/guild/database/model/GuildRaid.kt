package de.hydrum.toonworld.guild.database.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "guilds_raids")
data class GuildRaid(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var raidId: String,
    @Column(nullable = false) var endTime: Instant,
    @Column(name = "score_total", nullable = false) var score: Long,

    @OneToMany(
        mappedBy = "raid",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var members: MutableList<GuildRaidMember> = mutableListOf(),
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id", nullable = false)
    var guild: Guild?
)
