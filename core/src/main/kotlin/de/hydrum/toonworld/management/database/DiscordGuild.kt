package de.hydrum.toonworld.management.database

import jakarta.persistence.*

@Entity
@Table(name = "discord_guilds")
class DiscordGuild(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "swgoh_guild_id", nullable = false)
    var swgohGuildId: String,

    @Column(name = "discord_guild_id", nullable = false)
    var discordGuildId: Long,

    @Column(name = "journey_progress_channel_id")
    var journeyProgressReportChannelId: Long

)