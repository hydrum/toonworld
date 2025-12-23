package de.hydrum.toonworld.player.database.model

enum class RelicTier(val label: String, val relicValue: Int, val minStars: Int) {
    NONE("", 0, 0),
    LOCKED("", 0, 0),
    TIER_0("R0", 0, 1),
    TIER_1("R1", 1, 2),
    TIER_2("R2", 2, 3),
    TIER_3("R3", 3, 4),
    TIER_4("R4", 4, 5),
    TIER_5("R5", 5, 6),
    TIER_6("R6", 6, 7),
    TIER_7("R7", 7, 7),
    TIER_8("R8", 8, 7),
    TIER_9("R9", 9, 7),
    TIER_10("R10", 10, 7)
}