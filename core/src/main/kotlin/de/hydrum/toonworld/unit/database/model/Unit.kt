package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "units")
data class Unit(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,

    @Column(name = "base_id", nullable = false)
    var baseId: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "image", nullable = false)
    var image: String,

    @Column(name = "alignment", nullable = false)
    @Enumerated(EnumType.STRING)
    var alignment: Alignment,

    @Column(name = "role", nullable = false)
    var role: String,

    @Column(name = "combat_type", nullable = false)
    @Enumerated(EnumType.STRING)
    var combatType: CombatType,

    @Column(name = "ship_base_id")
    var shipBaseId: String?,

    @Column(name = "ship_slot")
    var shipSlot: Int?,

    @Column(name = "is_capital_ship")
    var isCapitalShip: Boolean,

    @Column(name = "is_galactic_legend")
    var isGalacticLegend: Boolean,

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var abilities: MutableList<UnitAbility> = mutableListOf(),

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var categories: MutableList<UnitCategory> = mutableListOf(),

    @OneToMany(
        mappedBy = "unit",
        cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE],
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @Fetch(value = FetchMode.SUBSELECT)
    var aliases: MutableList<UnitAlias> = mutableListOf(),
)
