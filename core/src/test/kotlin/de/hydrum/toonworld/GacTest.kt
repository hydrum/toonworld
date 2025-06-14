package de.hydrum.toonworld

import de.hydrum.toonworld.testutils.TestUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class GacTest {

    @Test
    @Disabled
    fun test() {
        val doc = Jsoup.connect("https://swgoh.gg/p/154962211/gac-history/O1701813600000/3/").get()
        log.info { doc.html() }
    }

    @Test
    fun parse() {
        val gacFile = TestUtil().loadResource("/data/gac/hydrum_gac_s47_w1_r3_20250202.html").file
        val doc = Jsoup.parse(File(gacFile))

        // header
        val gacHeader = doc.select("div:contains(Season 47)").parents().last()
        val playerLine = gacHeader?.select("span")?.parents()?.last()
        val playerName = playerLine?.select("span")?.text()
        val opponentAllyCode = playerLine?.select("a")?.attr("href")?.replace("/p/", "")?.replace("/", "") // TODO replace /p/.../
        val opponentName = playerLine?.select("a")?.text()
        val scoreLine = gacHeader?.select("div > div")[3]?.text()
        log.info { "$playerName vs $opponentName ($opponentAllyCode) $scoreLine" }

        log.info { "ATTACK:" }
        printBattles(doc.select("#battles-attack"))

        log.info { "DEFENSE:" }
        printBattles(doc.select("#battles-defense"))
    }

    private fun printBattles(element: Elements) {
        val battles = element.select(".gac-counters-battle-summary")
        log.info { "${battles.size} battles" }
        battles.forEach { battle ->
            battle.select(".gac-counters-battle-summary__stat")
                .map {
                    Pair(
                        it.select(".gac-counters-battle-summary__stat-label").first()?.text(),
                        it.select(".gac-counters-battle-summary__stat-value").first()
                    )
                }
                .map {
                    when (it.first) {
                        "League" -> Pair(it.first, it.second?.select("img")?.last()?.attr("src")) // TODO resolve image to league + division
                        "Zone" -> Pair(it.first, it.second?.select("svg")?.first()?.children()?.indexOfFirst { it.hasClass("gac-zone-layout--is-active") } ?: -1)
                        else -> Pair(it.first, it.second?.text())
                    }
                }
                .joinToString(" | ") { "${it.first}: ${it.second}" }
                .let { log.info { it } }
            val attacker = battle.select(".gac-counters-battle-summary__side--attacker").first()?.select(".gac-battle-unit-portrait")?.map { it.parseUnit() }
            log.info { attacker?.joinToString(", ") { it.toString() } }
            val defender = battle.select(".gac-counters-battle-summary__side--defense").first()?.select(".gac-battle-unit-portrait")?.map { it.parseUnit() }
            log.info { defender?.joinToString(", ") { it.toString() } }
        }
    }

    companion object {
        val log = KotlinLogging.logger {}

        fun org.jsoup.nodes.Element.parseUnit(): GacParseUnit = when {
            select(".character-portrait").isNotEmpty() -> GacParseUnit(
                baseId = checkNotNull(select(".character-portrait").first()?.attr("data-unit-def-tooltip-app")) { "cannot find baseId of character" },
                alreadyDead = select(".gac-battle-unit-portrait__dead").isNotEmpty(),
                prot = select(".unit-bars__fill--prot").first()?.attr("style")?.replace("width: ", "")?.replace("%;", "") ?: "0",
                health = select(".unit-bars__fill--hp").first()?.attr("style")?.replace("width: ", "")?.replace("%;", "") ?: "0",
                zeta = select(".character-portrait__zeta").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                omi = select(".character-portrait__omicron").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                level = select(".character-portrait__level").first()?.select("text")?.first()?.text()?.toInt() ?: 85,
                gear = select(".character-portrait__gframe").first()?.toGearLevel() ?: 13,
                relic = select(".character-portrait__relic").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                rarity = (select(".rarity-range").first()?.select(".rarity-range__star--inactive")?.size ?: 0).let { 7 - it }
            )

            else -> GacParseUnit(
                baseId = checkNotNull(select(".ship-portrait").first()?.attr("data-unit-def-tooltip-app")) { "cannot find baseId of ship" },
                alreadyDead = select(".gac-battle-unit-portrait__dead").isNotEmpty(),
                prot = select(".unit-bars__fill--prot").first()?.attr("style")?.replace("width: ", "")?.replace("%;", "") ?: "0",
                health = select(".unit-bars__fill--hp").first()?.attr("style")?.replace("width: ", "")?.replace("%;", "") ?: "0",
                zeta = select(".ship-portrait__zeta").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                omi = select(".ship-portrait__omicron").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                level = select(".ship-portrait__level").first()?.select("text")?.first()?.text()?.toInt() ?: 85,
                gear = select(".ship-portrait__gframe").first()?.toGearLevel() ?: 0,
                relic = select(".ship-portrait__relic").first()?.select("text")?.first()?.text()?.toInt() ?: 0,
                rarity = (select(".rarity-range").first()?.select(".rarity-range__star--inactive")?.size ?: 0).let { 7 - it }
            )
        }

        fun org.jsoup.nodes.Element.toGearLevel(): Int = when {
            hasClass("character-portrait__gframe--tier-1") -> 1
            hasClass("character-portrait__gframe--tier-2") -> 2
            hasClass("character-portrait__gframe--tier-3") -> 3
            hasClass("character-portrait__gframe--tier-4") -> 4
            hasClass("character-portrait__gframe--tier-5") -> 5
            hasClass("character-portrait__gframe--tier-6") -> 6
            hasClass("character-portrait__gframe--tier-7") -> 7
            hasClass("character-portrait__gframe--tier-8") -> 8
            hasClass("character-portrait__gframe--tier-9") -> 9
            hasClass("character-portrait__gframe--tier-10") -> 10
            hasClass("character-portrait__gframe--tier-11") -> 11
            hasClass("character-portrait__gframe--tier-12") -> 12
            else -> 13
        }

        class GacParseUnit(
            val baseId: String,
            val alreadyDead: Boolean,
            val prot: String,
            val health: String,
            val zeta: Int,
            val omi: Int,
            val gear: Int,
            val level: Int,
            val relic: Int,
            val rarity: Int,
        ) {
            override fun toString(): String {
                return "$baseId prot:$prot health:$health z:$zeta o:$omi l:$level g:$gear r:$relic rarity:$rarity alreadyDead:$alreadyDead"
            }
        }
    }
}