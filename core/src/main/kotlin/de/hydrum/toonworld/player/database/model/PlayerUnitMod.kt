package de.hydrum.toonworld.player.database.model

import de.hydrum.toonworld.data.mods.ModSlot
import de.hydrum.toonworld.data.mods.ModTier
import de.hydrum.toonworld.data.mods.UnitStat
import jakarta.persistence.*


@Entity
@Table(name = "players_units_mods")
data class PlayerUnitMod(

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

    @Column(name = "slot", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var slot: ModSlot,

    @Column(name = "level", nullable = false)
    var level: Int,

    @Column(name = "rarity", nullable = false)
    var rarity: Int,

    @Column(name = "tier", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var tier: ModTier,

    @Column(name = "mod_set", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var modSet: UnitStat,

    @Column(name = "primary_stat", nullable = false)
    @Enumerated(value = EnumType.STRING)
    var primaryStat: UnitStat,
    @Column(name = "primary_value", nullable = false)
    var primaryValue: Long,

    // secondaries
    @Column(name = "secondary_1_stat")
    @Enumerated(value = EnumType.STRING)
    var secondary1Stat: UnitStat?,
    @Column(name = "secondary_1_value")
    var secondary1Value: Long?,
    @Column(name = "secondary_1_roll")
    var secondary1Roll: Int?,

    @Column(name = "secondary_2_stat")
    @Enumerated(value = EnumType.STRING)
    var secondary2Stat: UnitStat?,
    @Column(name = "secondary_2_value")
    var secondary2Value: Long?,
    @Column(name = "secondary_2_roll")
    var secondary2Roll: Int?,

    @Column(name = "secondary_3_stat")
    @Enumerated(value = EnumType.STRING)
    var secondary3Stat: UnitStat?,
    @Column(name = "secondary_3_value")
    var secondary3Value: Long?,
    @Column(name = "secondary_3_roll")
    var secondary3Roll: Int?,

    @Column(name = "secondary_4_stat")
    @Enumerated(value = EnumType.STRING)
    var secondary4Stat: UnitStat?,
    @Column(name = "secondary_4_value")
    var secondary4Value: Long?,
    @Column(name = "secondary_4_roll")
    var secondary4Roll: Int?,
) {

    @Transient
    private fun getSecondaryTriples(): List<Triple<UnitStat, Long, Int>> = listOfNotNull(
        if (secondary1Stat != null && secondary1Stat != null && secondary1Stat != null) Triple(secondary1Stat!!, secondary1Value!!, secondary1Roll!!) else null,
        if (secondary2Stat != null && secondary2Stat != null && secondary2Stat != null) Triple(secondary2Stat!!, secondary2Value!!, secondary2Roll!!) else null,
        if (secondary3Stat != null && secondary3Stat != null && secondary3Stat != null) Triple(secondary3Stat!!, secondary3Value!!, secondary3Roll!!) else null,
        if (secondary4Stat != null && secondary4Stat != null && secondary4Stat != null) Triple(secondary4Stat!!, secondary4Value!!, secondary4Roll!!) else null
    )

    @Transient
    fun getSecondarySpeed(): Long =
        getSecondaryTriples()
            .firstOrNull { it.first == UnitStat.SPEED }?.let { (it.second / 10_000) }
            ?: 0
}
