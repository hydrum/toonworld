package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*

@Entity
@Table(name = "units_abilities")
class UnitAbility(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    var unit: Unit?,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "base_id", nullable = false)
    var baseId: String,

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var type: AbilityType,

    @Column(name = "omicron_mode", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var omicronMode: OmicronMode,

    @Column(name = "tier_max", nullable = false)
    var maxTier: Int,

    @Column(name = "is_omega", nullable = false)
    var isOmega: Boolean,

    @Column(name = "is_zeta", nullable = false)
    var isZeta: Boolean,

    @Column(name = "is_omicron", nullable = false)
    var isOmicron: Boolean,

    @Column(name = "is_ultimate", nullable = false)
    var isUltimate: Boolean
)
