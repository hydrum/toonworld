package de.hydrum.toonworld.guild.database.model

import de.hydrum.toonworld.management.database.DiscordGuild
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "guilds")
data class Guild(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "swgoh_guild_id", nullable = false)
    var swgohGuildId: String,

    @Column(name = "update_time", nullable = false)
    var updateTime: Instant,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "banner_logo_id", nullable = false)
    var bannerLogoId: String,

    @Column(name = "banner_color_id", nullable = false)
    var bannerColorId: String,

    @Column(name = "member_count", nullable = false)
    var memberCount: Int,

    @Column(name = "galactic_power", nullable = false)
    var galacticPower: Long,

    @Column(name = "next_reset_time", nullable = false)
    var nextResetTime: Instant,

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