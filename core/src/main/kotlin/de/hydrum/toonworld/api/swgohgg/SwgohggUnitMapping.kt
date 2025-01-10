package de.hydrum.toonworld.api.swgohgg

import de.hydrum.toonworld.unit.database.model.AbilityType
import de.hydrum.toonworld.unit.database.model.Alignment
import de.hydrum.toonworld.unit.database.model.CombatType
import de.hydrum.toonworld.unit.database.model.OmicronMode
import de.hydrum.toonworld.util.firstOrDefault
import de.hydrum.toonworld.api.swgohgg.models.Ability as SwgohggAbility
import de.hydrum.toonworld.api.swgohgg.models.Unit as SwgohggUnit
import de.hydrum.toonworld.unit.database.model.Unit as ToonWorldUnit
import de.hydrum.toonworld.unit.database.model.UnitAbility as ToonWorldUnitAbility
import de.hydrum.toonworld.unit.database.model.UnitCategory as ToonWorldUnitCategory


infix fun SwgohggUnit.updates(unit: ToonWorldUnit): ToonWorldUnit {
    unit.baseId = baseId
    unit.name = name
    unit.image = image
    unit.alignment = Alignment.entries.firstOrDefault(alignment, Alignment.UNKNOWN)
    unit.role = role
    unit.combatType = CombatType.entries.firstOrDefault(combatType, CombatType.UNKNOWN)
    unit.shipBaseId = shipBaseId
    unit.shipSlot = shipSlot
    unit.isCapitalShip = isCapitalShip
    unit.isGalacticLegend = isGalacticLegend
    categories.map { category ->
        unit.categories.find { it.category == category } ?: ToonWorldUnitCategory(
            id = null,
            unitBaseId = baseId,
            unit = unit,
            category = category
        )
    }.also { unit.categories.clear() }.also { unit.categories.addAll(it) }
    return unit
}

fun SwgohggUnit.toEntity() =
    ToonWorldUnit(
        id = null,
        baseId = baseId,
        name = name,
        image = image,
        alignment = Alignment.entries.firstOrDefault(alignment, Alignment.UNKNOWN),
        role = role,
        combatType = CombatType.entries.firstOrDefault(combatType, CombatType.UNKNOWN),
        shipBaseId = shipBaseId,
        shipSlot = shipSlot,
        isCapitalShip = isCapitalShip,
        isGalacticLegend = isGalacticLegend,
        categories = categories
            .map { category ->
                ToonWorldUnitCategory(
                    id = null,
                    unitBaseId = baseId,
                    unit = null,
                    category = category
                )
            }
            .toMutableList()
    ).also { unit -> unit.categories.forEach { it.unit = unit } }

fun ToonWorldUnitAbility.updatedBy(ability: SwgohggAbility): ToonWorldUnitAbility {
    runCatching {
        name = ability.name
        baseId = ability.baseId
        type = AbilityType.entries.firstOrDefault(ability.type, AbilityType.UNKNOWN)
        omicronMode = OmicronMode.entries.firstOrDefault(ability.omicronMode, OmicronMode.NONE)
        maxTier = ability.tierMax
        isOmega = ability.isOmega
        isZeta = ability.isZeta
        isOmicron = ability.isOmicron
        isUltimate = ability.isUltimate
    }.onFailure { throw RuntimeException("exception for $ability", it) }
    return this
}

fun SwgohggAbility.toEntity(): ToonWorldUnitAbility =
    ToonWorldUnitAbility(
        id = null,
        unit = null,
        name = name,
        baseId = baseId,
        type = AbilityType.entries.firstOrDefault(type, AbilityType.UNKNOWN),
        omicronMode = OmicronMode.entries.firstOrDefault(omicronMode, OmicronMode.NONE),
        maxTier = tierMax,
        isOmega = isOmega,
        isZeta = isZeta,
        isOmicron = isOmicron,
        isUltimate = isUltimate
    )