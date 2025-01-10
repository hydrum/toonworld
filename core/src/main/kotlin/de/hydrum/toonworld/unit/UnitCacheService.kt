package de.hydrum.toonworld.unit

import de.hydrum.toonworld.config.CacheNames
import de.hydrum.toonworld.unit.database.UnitRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import de.hydrum.toonworld.unit.database.model.Unit as ToonWorldUnit

@Service
@Transactional
class UnitCacheService(
    private val cacheManager: CacheManager,
    private val repository: UnitRepository
) {

    @Cacheable(cacheNames = [CacheNames.UNITS], key = "#baseId", unless = "#result == null")
    fun findUnit(baseId: String): ToonWorldUnit? = repository.findByBaseId(baseId)

    fun invalidateAndUpdate() =
        cacheManager.getCache(CacheNames.UNITS)
            ?.also { it.invalidate() }
            ?.also { cache ->
                repository.findAll().forEach {
                    cache.put(it.baseId, it)
                }
            }
            ?.also { log.info { "Units cache invalidated & updated" } }

    companion object {
        private val log = KotlinLogging.logger { }
    }

}