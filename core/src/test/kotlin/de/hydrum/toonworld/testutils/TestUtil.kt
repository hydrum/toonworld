package de.hydrum.toonworld.testutils

import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

class TestUtil {
    fun loadResourceAsStream(file: String) = requireNotNull(this::class.java.getResourceAsStream(file))
    fun loadResource(file: String) = requireNotNull(this::class.java.getResource(file))
}

fun createObjectMapper(): ObjectMapper = JsonMapper.builder().findAndAddModules().build()
