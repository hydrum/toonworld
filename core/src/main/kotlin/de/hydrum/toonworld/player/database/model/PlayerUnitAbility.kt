package de.hydrum.toonworld.player.database.model

import jakarta.persistence.*

@Entity
@Table(name = "players_units_abilities")
data class PlayerUnitAbility(

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) var id: Long?,
    @Column(nullable = false) var baseId: String,
    @Column(nullable = false) var tier: Int,
    @Column(nullable = false) var hasOmega: Boolean,
    @Column(nullable = false) var hasZeta: Boolean,
    @Column(nullable = false) var hasOmicron: Boolean,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_unit_id", nullable = false)
    var unit: PlayerUnit?,
    @Transient @Column(nullable = false) var playerUnitId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    var player: Player?,
    @Transient @Column(nullable = false) var playerId: Long? = null

)
