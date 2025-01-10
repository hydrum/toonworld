package de.hydrum.toonworld.player.database.model

import jakarta.persistence.*


@Entity
@Table(name = "players_units_abilities")
data class PlayerUnitAbility(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_unit_id", nullable = false)
    var unit: PlayerUnit?,

    @Transient
    @Column(name = "player_unit_id", nullable = false)
    var playerUnitId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    var player: Player?,

    @Transient
    @Column(name = "player_id", nullable = false)
    var playerId: Long? = null,

    @Column(name = "base_id", nullable = false)
    var baseId: String,

    @Column(name = "tier", nullable = false)
    var tier: Int,

    @Column(name = "has_omega", nullable = false)
    var hasOmega: Boolean,

    @Column(name = "has_zeta", nullable = false)
    var hasZeta: Boolean,

    @Column(name = "has_omicron", nullable = false)
    var hasOmicron: Boolean,
)
