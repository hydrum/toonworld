package de.hydrum.toonworld.management

import de.hydrum.toonworld.config.CacheNames
import de.hydrum.toonworld.management.database.DiscordPlayerRepository
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import discord4j.core.`object`.entity.User
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class DiscordPlayerCacheService(
    private val cacheManager: CacheManager,
    private val repository: DiscordPlayerRepository,
    private val playerRepository: PlayerRepository
) {

    fun getAllyCodeChecked(user: User, slot: Long = 0L) = requireNotNull(getAllyCode(user, slot)) { "Nothing is linked for slot $slot. Please link an ally code first for this slot." }
    fun getAllyCode(user: User, slot: Long = 0L) = findAllyCode(user.id.asLong(), slot)

    fun getGuildIdChecked(user: User, slot: Long = 0L) = requireNotNull(getGuildId(user, slot)) { "Cannot retrieve the guild of slot $slot. Either no ally code is linked or no synced profile was found." }
    fun getGuildId(user: User, slot: Long = 0L) = findGuildId(user.id.asLong(), slot)

    @Cacheable(cacheNames = [CacheNames.DISCORD_USER_ALLY_CODE], key = "{ #discordUserId, #slot }", unless = "#result == null")
    fun findAllyCode(discordUserId: Long, slot: Long): String? =
        repository.findByDiscordUserIdAndSlot(discordUserId, slot)?.allyCode

    @Cacheable(cacheNames = [CacheNames.DISCORD_USER_GUILD], key = "{ #discordUserId, #slot }", unless = "#result == null")
    fun findGuildId(discordUserId: Long, slot: Long): String? =
        findAllyCode(discordUserId, slot)
            ?.let { playerRepository.findPlayerByAllyCode(it)?.swgohGuildId }


    @TransactionalEventListener
    fun invalidateCache(event: InvalidateDiscordPlayerCache) = with(event) {
        cacheManager.getCache(CacheNames.DISCORD_USER_ALLY_CODE)?.evictIfPresent("{ $discordUserId, $slot }")
        cacheManager.getCache(CacheNames.DISCORD_USER_GUILD)?.evictIfPresent("{ $discordUserId, $slot }")
    }

    class InvalidateDiscordPlayerCache(val discordUserId: Long, val slot: Long)
}