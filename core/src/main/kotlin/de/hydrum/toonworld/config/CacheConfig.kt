package de.hydrum.toonworld.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig(val appConfig: AppConfig) {

    @Bean
    fun caffeineConfig() = Caffeine
        .newBuilder()
        .expireAfterWrite(appConfig.cache.expireAfterWriteInMinutes, TimeUnit.MINUTES)

    @Bean
    fun cacheManager() = CaffeineCacheManager()
        .also { it.setCaffeine(caffeineConfig()) }
}

object CacheNames {

    const val UNITS = "UNITS"
    const val MODS = "MODS"
    const val JOURNEY_GUIDES = "JOURNEY_GUIDES"
    const val DISCORD_USER_ALLY_CODE = "DISCORD_USER_ALLY_CODE"
    const val DISCORD_USER_GUILD = "DISCORD_USER_GUILD"

}