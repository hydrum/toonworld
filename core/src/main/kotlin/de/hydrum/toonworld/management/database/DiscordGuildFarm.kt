package de.hydrum.toonworld.management.database

import de.hydrum.toonworld.farm.database.Farm
import jakarta.persistence.*

@Entity
@Table(name = "discord_guilds_farms")
class DiscordGuildFarm(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    var discordRoleId: Long?,

    var announceChannelId: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discord_guild_id", nullable = false)
    var discordGuild: DiscordGuild?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    var farm: Farm

)
