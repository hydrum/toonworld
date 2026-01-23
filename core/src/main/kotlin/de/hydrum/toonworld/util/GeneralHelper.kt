package de.hydrum.toonworld.util

import java.time.Instant

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

fun <T, S> T?.returnNullOr(lambda: (T) -> S) = if (this == null) null else lambda(this)