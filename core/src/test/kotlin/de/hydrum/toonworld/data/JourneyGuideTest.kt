package de.hydrum.toonworld.data

import com.fasterxml.jackson.databind.ObjectMapper
import de.hydrum.toonworld.TestUtil
import de.hydrum.toonworld.api.comlink.models.Player
import de.hydrum.toonworld.api.comlink.toEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class JourneyGuideTest {

    @Test
    fun test() {
        val objectMapper = createObjectMapper()
        assertNotNull(objectMapper)

        val journeyGuideFile = TestUtil().loadResource("/data/journey-guide.json")
        val journeyGuides = objectMapper.readValue(journeyGuideFile, JourneyGuides::class.java)

        val playerFile = TestUtil().loadResource("/data/hydrum_20250110.json")
        val player = objectMapper.readValue(playerFile, Player::class.java)

        val playerUnits = player.rosterUnit.map { it.toEntity(null) }

        journeyGuides.units.map { JourneyGuideProgress(it, playerUnits) }.filter { it.totalProgress != 1.0 }.forEach { journeyProgress ->
            log.info {
                "${journeyProgress.journeyGuide.baseId}: ${journeyProgress.totalProgress.toPctText()}% | ${
                    journeyProgress.relatedPlayerUnits.filter { it.getWeightedProgress() != 1.0 }.joinToString(", ") {
                        val rarityText = "${it.playerUnit?.rarity ?: 0}/${it.journeyUnit.rarity} (${it.getRarityProgress().toPctText()}%)"
                        val gearText = "G${it.playerUnit?.gearLevel ?: 0}/${it.journeyUnit.gearLevel} (${it.getGearProgress().toPctText()}%)"
                        val relicText = "${it.playerUnit?.relicTier?.label ?: "---"}/${it.journeyUnit.relicTier.label} (${it.getRelicProgress().toPctText()}%)"
                        "${it.journeyUnit.baseId} ${it.getWeightedProgress().toPctText()}%"
                    }
                }"
            }

        }

    }

    companion object {
        private val log = KotlinLogging.logger { }
        fun createObjectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()
    }
}