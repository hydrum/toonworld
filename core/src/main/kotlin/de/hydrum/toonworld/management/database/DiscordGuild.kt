package de.hydrum.toonworld.management.database

import de.hydrum.toonworld.farm.database.Farm
import de.hydrum.toonworld.guild.database.model.Guild
import jakarta.persistence.*

@Entity
@Table(name = "discord_guilds")
class DiscordGuild(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(name = "swgoh_guild_id", nullable = false) var swgohGuildId: String,
    @Column(nullable = false) var discordGuildId: Long,
    @Column(name = "journey_progress_channel_id") var journeyProgressReportChannelId: Long?,
    var officerInfoChannelId: Long?,

    @OneToMany(
        mappedBy = "ownerDiscordGuild",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var farms: MutableList<Farm> = mutableListOf(),

    @OneToMany(
        mappedBy = "discordGuild",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    var farmRoles: MutableList<DiscordGuildFarmRole> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "swgoh_guild_id", referencedColumnName = "swgoh_guild_id", insertable = false, updatable = false)
    var guild: Guild? = null

)
