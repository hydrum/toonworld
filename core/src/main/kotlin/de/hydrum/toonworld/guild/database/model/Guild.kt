package de.hydrum.toonworld.guild.database.model

import de.hydrum.toonworld.management.database.DiscordGuild
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "guilds")
data class Guild(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(name = "swgoh_guild_id", nullable = false) var swgohGuildId: String,
    @Column(nullable = false) var updateTime: Instant,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var bannerLogoId: String,
    @Column(nullable = false) var bannerColorId: String,
    @Column(nullable = false) var memberCount: Int,
    @Column(nullable = false) var galacticPower: Long,
    @Column(nullable = false) var nextResetTime: Instant,

    @OneToMany(
        mappedBy = "guild",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var members: MutableList<GuildMember> = mutableListOf(),

    @OneToMany(
        mappedBy = "guild",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var raids: MutableList<GuildRaid> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "swgoh_guild_id", referencedColumnName = "swgoh_guild_id", insertable = false, updatable = false)
    var discordGuild: DiscordGuild? = null

)
