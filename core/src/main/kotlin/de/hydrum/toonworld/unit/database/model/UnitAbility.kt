package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*

@Entity
@Table(name = "units_abilities")
class UnitAbility(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var baseId: String,
    @Column(nullable = false) @Enumerated(value = EnumType.STRING) var type: AbilityType,
    @Column(nullable = false) @Enumerated(value = EnumType.STRING) var omicronMode: OmicronMode,
    @Column(name = "tier_max", nullable = false) var maxTier: Int,
    @Column(nullable = false) var isOmega: Boolean,
    @Column(nullable = false) var isZeta: Boolean,
    @Column(nullable = false) var isOmicron: Boolean,
    @Column(nullable = false) var isUltimate: Boolean,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    var unit: Unit?

)
