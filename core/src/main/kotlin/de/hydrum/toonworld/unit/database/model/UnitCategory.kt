package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*

@Entity
@Table(name = "units_categories")
class UnitCategory(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var unitBaseId: String,
    @Column(nullable = false) var category: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false) var unit: Unit?

)
