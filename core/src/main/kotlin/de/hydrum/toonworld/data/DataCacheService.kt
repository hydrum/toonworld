package de.hydrum.toonworld.data

import com.fasterxml.jackson.databind.ObjectMapper
import de.hydrum.toonworld.config.AppConfig
import de.hydrum.toonworld.config.CacheNames
import de.hydrum.toonworld.data.mods.ModData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class DataCacheService(
    private val objectMapper: ObjectMapper,
    private val appConfig: AppConfig
) {

    @Cacheable(cacheNames = [CacheNames.MODS])
    fun getModData(): ModData = checkNotNull(
        this::class.java.getResource("/data/${appConfig.data.modFile}"),
        { "file not found of ${"/data/${appConfig.data.modFile}"}" }
    ).let {
        objectMapper.readValue(it, ModData::class.java)
    }.also { log.debug { "Mod Sets: ${it.statModSet.size} | Mod Stats: ${it.statMod.size}" } }

    @Cacheable(cacheNames = [CacheNames.JOURNEY_GUIDES])
    fun getJourneyData(): List<JourneyGuide> = checkNotNull(
        this::class.java.getResource("/data/${appConfig.data.journeyGuideFile}"),
        { "file not found of ${"/data/${appConfig.data.journeyGuideFile}"}" }
    ).let {
        objectMapper.readValue(it, JourneyGuides::class.java).units
    }.also { log.debug { "Journey Guides: ${it.size}" } }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}