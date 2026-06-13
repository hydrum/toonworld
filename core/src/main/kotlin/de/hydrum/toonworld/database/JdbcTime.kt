package de.hydrum.toonworld.database

import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.util.Calendar
import java.util.TimeZone

private val UTC_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")

private fun utcCalendar(): Calendar = Calendar.getInstance(UTC_TIME_ZONE)

fun PreparedStatement.setUtcTimestamp(parameterIndex: Int, instant: Instant) {
    setTimestamp(parameterIndex, Timestamp.from(instant), utcCalendar())
}

fun ResultSet.getUtcInstant(columnLabel: String): Instant =
    getTimestamp(columnLabel, utcCalendar()).toInstant()
