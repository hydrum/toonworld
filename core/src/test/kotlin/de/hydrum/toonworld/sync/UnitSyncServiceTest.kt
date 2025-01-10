package de.hydrum.toonworld.sync

import de.hydrum.toonworld.*
import de.hydrum.toonworld.api.swgohgg.SwgohggApi
import de.hydrum.toonworld.unit.database.UnitRepository
import de.hydrum.toonworld.unit.database.model.AbilityType
import de.hydrum.toonworld.unit.database.model.Alignment
import de.hydrum.toonworld.unit.database.model.CombatType
import de.hydrum.toonworld.unit.database.model.OmicronMode
import de.hydrum.toonworld.util.ErrorHelper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import de.hydrum.toonworld.api.swgohgg.models.Ability as SwgohggAbility
import de.hydrum.toonworld.api.swgohgg.models.Unit as SwgohggUnit
import de.hydrum.toonworld.unit.database.model.Unit as ToonWorldUnit

class UnitSyncServiceTest {

    @MockK
    lateinit var swgohggApi: SwgohggApi

    @MockK
    lateinit var unitRepository: UnitRepository

    @MockK
    lateinit var errorHelper: ErrorHelper

    val capturedEntities = slot<List<ToonWorldUnit>>()

    lateinit var service: UnitSyncService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        every { swgohggApi.getUnits() } returns listOf(SWGOHGG_UNIT_1, SWGOHGG_UNIT_2)
        every { swgohggApi.getAbilities() } returns listOf(SWGOHGG_ABILITY_1, SWGOHGG_ABILITY_2, SWGOHGG_ABILITY_3, SWGOHGG_ABILITY_4)

        every { unitRepository.saveAll<ToonWorldUnit>(capture(capturedEntities)) } returnsArgument 0

        service = UnitSyncService(
            swgohggApi = swgohggApi,
            unitRepository = unitRepository,
            errorHelper = errorHelper
        )
    }

    @Test
    fun test() {
        every { unitRepository.findAll() } returns emptyList<ToonWorldUnit>()

        service.updateUnits()

        assertEquals(2, capturedEntities.captured.size)
        assertEntity(capturedEntities.captured, SWGOHGG_UNIT_1, listOf(SWGOHGG_ABILITY_1, SWGOHGG_ABILITY_2, SWGOHGG_ABILITY_3))
        assertEntity(capturedEntities.captured, SWGOHGG_UNIT_2, listOf(SWGOHGG_ABILITY_4))
    }

    fun assertEntity(entities: List<ToonWorldUnit>, expectedUnit: SwgohggUnit, expectedAbilities: List<SwgohggAbility>) = with(expectedUnit) {
        val found = entities.find { it.baseId == baseId } ?: fail { "$baseId was not found" }
        assertEquals(null, found.id)
        assertEquals(baseId, found.baseId)
        assertEquals(name, found.name)
        assertEquals(image, found.image)
        assertEquals(getAlignment(this), found.alignment)
        assertEquals(role, found.role)
        assertEquals(getCombatType(this), found.combatType)
        assertEquals(shipBaseId, found.shipBaseId)
        assertEquals(shipSlot, found.shipSlot)
        assertEquals(isCapitalShip, found.isCapitalShip)
        assertEquals(isGalacticLegend, found.isGalacticLegend)
        assertTrue(found.categories.all {
            it.unitBaseId == found.baseId
                    && it.id == null
                    && it.unit == found
                    && it.category in categories
        })
        assertAbilities(found, expectedAbilities)
    }

    fun assertAbilities(entity: ToonWorldUnit, expectedAbilities: List<SwgohggAbility>) = expectedAbilities.forEach {
        with(it) {
            val foundAbility = entity.abilities.find { it -> it.baseId == baseId } ?: fail { "ability $baseId not found in entity ${entity.baseId}" }

            assertEquals(null, foundAbility.id)
            assertEquals(entity, foundAbility.unit)
            assertEquals(name, foundAbility.name)
            assertEquals(baseId, foundAbility.baseId)
            assertEquals(getAbilityType(this), foundAbility.type)
            assertEquals(getOmicronType(this), foundAbility.omicronMode)
            assertEquals(tierMax, foundAbility.maxTier)
            assertEquals(isOmega, foundAbility.isOmega)
            assertEquals(isZeta, foundAbility.isZeta)
            assertEquals(isOmicron, foundAbility.isOmicron)
            assertEquals(isUltimate, foundAbility.isUltimate)
        }
    }

    companion object {

        private fun getCombatType(unit: SwgohggUnit) = when (unit) {
            SWGOHGG_UNIT_1 -> CombatType.CHARACTER
            SWGOHGG_UNIT_2 -> CombatType.SHIP
            else -> fail { "ability ${unit.baseId} not defined" }
        }

        private fun getAlignment(unit: SwgohggUnit) = when (unit) {
            SWGOHGG_UNIT_1 -> Alignment.NEUTRAL
            SWGOHGG_UNIT_2 -> Alignment.DARK
            else -> fail { "ability ${unit.baseId} not defined" }
        }

        private fun getAbilityType(ability: SwgohggAbility) = when (ability) {
            SWGOHGG_ABILITY_1 -> AbilityType.LEADER
            SWGOHGG_ABILITY_2 -> AbilityType.UNIQUE
            SWGOHGG_ABILITY_3 -> AbilityType.BASIC
            SWGOHGG_ABILITY_4 -> AbilityType.SHIP_SPECIAL
            else -> fail { "ability ${ability.baseId} not defined" }
        }

        private fun getOmicronType(ability: SwgohggAbility) = when (ability) {
            SWGOHGG_ABILITY_1 -> OmicronMode.CONQUEST
            SWGOHGG_ABILITY_2 -> OmicronMode.TW
            SWGOHGG_ABILITY_3 -> OmicronMode.NONE
            SWGOHGG_ABILITY_4 -> OmicronMode.NONE
            else -> fail { "ability ${ability.baseId} not defined" }
        }
    }
}