package de.hydrum.toonworld.player.database.model

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode


@Entity
@Table(name = "players_units")
data class PlayerUnit(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "players_units_seq")
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    var player: Player?,

    @Column(name = "base_id", nullable = false)
    var baseId: String,

    @Column(name = "gear_level", nullable = false)
    var gearLevel: Int,

    @Column(name = "level", nullable = false)
    var level: Int,

    @Column(name = "rarity", nullable = false)
    var rarity: Int,

    @Column(name = "relic_tier", nullable = false)
    @Enumerated(EnumType.STRING)
    var relicTier: RelicTier,

    @Column(name = "has_ultimate", nullable = false)
    var hasUltimate: Boolean,

    @Column(name = "zetas", nullable = false)
    var zetas: Int,

    @Column(name = "omicrons", nullable = false)
    var omicrons: Int,

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var abilities: MutableList<PlayerUnitAbility> = mutableListOf()
)
