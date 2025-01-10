package de.hydrum.toonworld

import com.fasterxml.jackson.databind.ObjectMapper

class TestUtil {
    fun loadResourceAsStream(file: String) = requireNotNull(this::class.java.getResourceAsStream(file))
    fun loadResource(file: String) = requireNotNull(this::class.java.getResource(file))
}

fun createObjectMapper(): ObjectMapper = ObjectMapper().also { it.findAndRegisterModules() }