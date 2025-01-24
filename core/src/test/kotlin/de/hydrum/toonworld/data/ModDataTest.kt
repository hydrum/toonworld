package de.hydrum.toonworld.data

import com.fasterxml.jackson.databind.ObjectMapper
import de.hydrum.toonworld.TestUtil
import de.hydrum.toonworld.api.comlink.models.Player
import de.hydrum.toonworld.data.mods.ModData
import de.hydrum.toonworld.data.mods.UnitStat
import de.hydrum.toonworld.data.mods.createModWith
import de.hydrum.toonworld.data.mods.toText
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ModDataTest {

    @Test
    fun test() {
        val objectMapper = createObjectMapper()
        assertNotNull(objectMapper)

        val modDataFile = TestUtil().loadResource("/data/game-data-mods_20250110.json")
        val modData = objectMapper.readValue(modDataFile, ModData::class.java)

        val playerFile = TestUtil().loadResource("/data/hydrum_20250110.json")
        val player = objectMapper.readValue(playerFile, Player::class.java)

        player.rosterUnit.forEach { unit ->
            unit.equippedStatMod.map { mod -> mod.createModWith(modData) }
                .sortedBy { it.slot }
                .filter { it.level != 15 }
                .forEach {
                    with(it) {
                        log.info { unit.definitionId }
                        val secondaries = listOf<Triple<UnitStat?, Long?, Int?>>(
                            Triple(primaryStat, primaryValue, 0),
                            Triple(secondary1Stat, secondary1Value, secondary1Roll),
                            Triple(secondary2Stat, secondary2Value, secondary2Roll),
                            Triple(secondary3Stat, secondary3Value, secondary3Roll),
                            Triple(secondary4Stat, secondary4Value, secondary4Roll)
                        )
                            .filter { it.first != null && it.second != null && it.third != null }
                            .map { Triple(it.first!!, it.second!!, it.third!!) }
                        log.info {
                            "ModSet: ${modSet.displayName} ${slot.displayName} " +
                                    "Tier: $level $rarity$tier | " +
                                    secondaries.joinToString(" | ") { it.toText() }
                        }
                    }
                }
        }

    }

    companion object {
        private val log = KotlinLogging.logger { }
        fun createObjectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
}