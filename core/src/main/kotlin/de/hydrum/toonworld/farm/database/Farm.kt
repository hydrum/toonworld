package de.hydrum.toonworld.farm.database

import de.hydrum.toonworld.management.database.DiscordGuild
import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "farms")
class Farm(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "team_size", nullable = false)
    var teamSize: Int,

    @OneToMany(
        mappedBy = "farm",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var units: MutableList<FarmUnit> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_discord_guild_id")
    var ownerDiscordGuild: DiscordGuild?
)