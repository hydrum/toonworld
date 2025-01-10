package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*

@Entity
@Table(name = "units_categories")
class UnitCategory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_id", nullable = false)
    var unit: Unit?,

    @Column(name = "unit_base_id", nullable = false)
    var unitBaseId: String,

    @Column(name = "category", nullable = false)
    var category: String

)