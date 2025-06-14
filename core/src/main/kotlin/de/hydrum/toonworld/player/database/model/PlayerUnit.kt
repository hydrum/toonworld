package de.hydrum.toonworld.player.database.model

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "players_units")
data class PlayerUnit(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) @SequenceGenerator(name = "players_units_seq") var id: Long?,
    @Column(nullable = false) var baseId: String,
    @Column(nullable = false) var gearLevel: Int,
    @Column(nullable = false) var level: Int,
    @Column(nullable = false) var rarity: Int,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var relicTier: RelicTier,
    @Column(nullable = false) var hasUltimate: Boolean,
    @Column(nullable = false) var zetas: Int,
    @Column(nullable = false) var omicrons: Int,

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var abilities: MutableList<PlayerUnitAbility> = mutableListOf(),

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var mods: MutableList<PlayerUnitMod> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    var player: Player?

)
