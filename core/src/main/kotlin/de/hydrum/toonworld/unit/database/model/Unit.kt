package de.hydrum.toonworld.unit.database.model

import jakarta.persistence.*
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

@Entity
@Table(name = "units")
data class Unit(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long?,
    @Column(nullable = false) var baseId: String,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var image: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var alignment: Alignment,
    @Column(nullable = false) var role: String,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var combatType: CombatType,
    var shipBaseId: String?,
    var shipSlot: Int?,
    var isCapitalShip: Boolean,
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
    var aliases: MutableList<UnitAlias> = mutableListOf()

)
