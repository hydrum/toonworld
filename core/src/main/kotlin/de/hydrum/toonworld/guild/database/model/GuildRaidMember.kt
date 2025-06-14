package de.hydrum.toonworld.guild.database.model

import jakarta.persistence.*

@Entity
@Table(name = "guilds_raids_members")
data class GuildRaidMember(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var swgohPlayerId: String,
    @Column(nullable = false) var score: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_id", nullable = false)
    var guild: Guild?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guild_raid_id", nullable = false)
    var raid: GuildRaid?,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "guild_member_id", nullable = true)
    var member: GuildMember? // may be null if the player has left the guild

)
