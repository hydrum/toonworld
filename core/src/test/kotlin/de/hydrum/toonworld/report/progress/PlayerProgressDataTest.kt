package de.hydrum.toonworld.report.progress

import de.hydrum.toonworld.player.database.model.PlayerUnit
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.player.progress.player.PlayerProgressUnit
import de.hydrum.toonworld.util.gainToBoolean
import de.hydrum.toonworld.util.gainToInt
import de.hydrum.toonworld.util.gainToRelicTier
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PlayerProgressDataTest {

    @Test
    fun test() {
        val boushh1 = PlayerUnit(id = 175, player = null, baseId = "BOUSHH", gearLevel = 1, level = 1, rarity = 7, relicTier = RelicTier.LOCKED, hasUltimate = false, zetas = 0, omicrons = 0)
        val boushh2 = PlayerUnit(id = 175, player = null, baseId = "BOUSHH", gearLevel = 12, level = 85, rarity = 7, relicTier = RelicTier.LOCKED, hasUltimate = false, zetas = 0, omicrons = 0)

        val compare = PlayerProgressUnit(
            name = boushh2.baseId,
            gearLevelGain = boushh1.gearLevel gainToInt boushh2.gearLevel,
            levelGain = boushh1.level gainToInt boushh2.level,
            rarityGain = boushh1.rarity gainToInt boushh2.rarity,
            relicTierGain = boushh1.relicTier gainToRelicTier boushh2.relicTier,
            ultimateGain = boushh1.hasUltimate gainToBoolean boushh2.hasUltimate,
            abilityGains = emptyList()
        )

        assertTrue(compare.gearLevelGain.hasChanged())
        assertTrue(compare.levelGain.hasChanged())

        assertFalse(compare.rarityGain.hasChanged())
        assertFalse(compare.relicTierGain.hasChanged())
        assertFalse(compare.ultimateGain.hasChanged())

        assertTrue(compare.hasChanged())
    }

    @Test
    fun testNewUnit() {
        val grandInquisitor2 = PlayerUnit(id = 175, player = null, baseId = "GRANDINQUISITOR", gearLevel = 13, level = 85, rarity = 7, relicTier = RelicTier.TIER_7, hasUltimate = false, zetas = 0, omicrons = 0)

        val compare = PlayerProgressUnit(
            name = grandInquisitor2.baseId,
            gearLevelGain = null gainToInt grandInquisitor2.gearLevel,
            levelGain = null gainToInt grandInquisitor2.level,
            rarityGain = null gainToInt grandInquisitor2.rarity,
            relicTierGain = null gainToRelicTier grandInquisitor2.relicTier,
            ultimateGain = null gainToBoolean grandInquisitor2.hasUltimate,
            abilityGains = emptyList()
        )

        assertTrue(compare.gearLevelGain.hasChanged())
        assertTrue(compare.levelGain.hasChanged())
        assertTrue(compare.rarityGain.hasChanged())
        assertTrue(compare.relicTierGain.hasChanged())
        assertTrue(compare.ultimateGain.hasChanged())
        assertTrue(compare.hasChanged())

        assertEquals(13, compare.gearLevelGain.absGain)
        assertEquals(85, compare.levelGain.absGain)
        assertEquals(7, compare.rarityGain.absGain)
        assertEquals(RelicTier.TIER_7, compare.relicTierGain.absGain)
        assertEquals("R7 (+7)", compare.relicTierGain.changeText())
        assertEquals(false, compare.ultimateGain.absGain)
    }

    @Test
    fun relicAbsGain() {
        var relicTierGain = null gainToRelicTier RelicTier.TIER_7
        assertEquals(RelicTier.TIER_7, relicTierGain.absGain)
        assertEquals("R7 (+7)", relicTierGain.changeText())


        relicTierGain = RelicTier.TIER_5 gainToRelicTier RelicTier.TIER_7
        assertEquals(RelicTier.TIER_2, relicTierGain.absGain)
        assertEquals("R7 (+2)", relicTierGain.changeText())
    }
}