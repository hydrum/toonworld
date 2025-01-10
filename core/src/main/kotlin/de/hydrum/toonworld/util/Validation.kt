package de.hydrum.toonworld.util

import de.hydrum.toonworld.api.comlink.infrastructure.ClientError
import de.hydrum.toonworld.api.comlink.infrastructure.ClientException

fun validateAllyCode(it: String) = require(it.matches(Regex("^[1-9]{9}$"))) { "allyCode must be 9 digits long and only contain numbers 1-9" }
fun validateSlot(slot: Long) = require(slot >= 0) { "slot may not be negative" }


fun errorIsAllyCodeCannotBeFound(it: Throwable, allyCode: String) =
    it is ClientException
            && it.response is ClientError<*>
            && it.response.body is String
            && it.response.body.contains("\"code\":32,\"message\":\"Failed to find ally code ${allyCode}\"}")

fun errorIsGuildCannotBeFound(it: Throwable) =
    it is ClientException
            && it.response is ClientError<*>
            && it.response.body is String
            && it.response.body.contains("\"code\":32,\"message\":\"GUILD_ERROR_GUILD_NOT_FOUND\"}")