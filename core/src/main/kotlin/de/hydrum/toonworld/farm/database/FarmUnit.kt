package de.hydrum.toonworld.farm.database

import de.hydrum.toonworld.player.database.model.RelicTier
import jakarta.persistence.*

@Entity
@Table(name = "farms_units")
class FarmUnit(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    var farm: Farm?,

    @Column(name = "base_id", nullable = false)
    var baseId: String,

    @Column(name = "is_required", nullable = false)
    var required: Boolean,

    @Column(name = "min_rarity")
    var minRarity: Int,

    @Column(name = "min_gear_level")
    var minGearLevel: Int,

    @Column(name = "min_relic_tier")
    @Enumerated(EnumType.STRING)
    var minRelicTier: RelicTier,

    @Column(name = "min_stat_speed")
    var minSpeed: Long?,

    @Column(name = "min_stat_health")
    var minHealth: Long?,

    @Column(name = "min_stat_protection")
    var minProtection: Long?,

    @Column(name = "min_stat_offense")
    var minOffense: Long?
)