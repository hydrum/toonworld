package de.hydrum.toonworld.util

import java.time.Instant
import kotlin.math.roundToInt

// scraper stuff
fun String?.replaceCommaSeparator(): String? = this?.replace(",", "")
fun String?.replaceDashSeparator(): String? = this?.replace("-", "")

infix fun <T> T?.elseTake(lambda: () -> T): T = this ?: lambda.invoke()

inline fun <reified T : Enum<T>> Iterable<T>.firstOrDefault(value: Int?, default: T) =
    this.firstOrNull { it.ordinal == value }.elseTake { default }

fun utcNow(): Instant = Instant.now()

fun Instant.toDiscordDateTime() = "<t:${epochSecond}:f>"
fun Instant.toDiscordDate() = "<t:${epochSecond}:d>"
fun Instant.toDiscordTime() = "<t:${epochSecond}:t>"
fun Instant.toDiscordTimeSeconds() = "<t:${epochSecond}:T>"
fun Instant.toDiscordRelativeDateTime() = "<t:${epochSecond}:R>"


fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}

fun <T, S> T?.returnNullOr(lambda: (T) -> S) = if (this == null) null else lambda(this)