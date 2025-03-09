package de.hydrum.toonworld.management.database

import de.hydrum.toonworld.farm.database.Farm
import jakarta.persistence.*

@Entity
@Table(name = "discord_guilds_farm_roles")
class DiscordGuildFarmRole(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discord_guild_id", nullable = false)
    var discordGuild: DiscordGuild?,

    @Column(name = "discord_role_id", nullable = false)
    var discordRoleId: Long,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    var farm: Farm
)