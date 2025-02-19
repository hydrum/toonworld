package de.hydrum.toonworld.management

import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.management.database.DiscordPlayer
import de.hydrum.toonworld.management.database.DiscordPlayerRepository
import de.hydrum.toonworld.util.validateAllyCode
import de.hydrum.toonworld.util.validateSlot
import discord4j.core.`object`.entity.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerService(
    private val discordPlayerRepository: DiscordPlayerRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val discordPlayerCacheService: DiscordPlayerCacheService,
    private val comlinkApi: ComlinkApi
) {

    fun getAllyCodeChecked(user: User, slot: Long = 0L) = requireNotNull(getAllyCode(user, slot)) { "Nothing is linked for slot $slot. Please link an ally code first for this slot." }
    fun getAllyCode(user: User, slot: Long = 0L) = discordPlayerCacheService.findAllyCode(user.id.asLong(), slot)

    fun getGuildIdChecked(user: User, slot: Long = 0L) = requireNotNull(getGuildId(user, slot)) { "Cannot retrieve the guild of slot $slot. Either no ally code is linked or no synced profile was found." }
    fun getGuildId(user: User, slot: Long = 0L) = discordPlayerCacheService.findGuildId(user.id.asLong(), slot)

    @Transactional
    fun linkPlayer(user: User, allyCode: String, slot: Long) {
        validateAllyCode(allyCode)
        validateSlot(slot)

        require(!discordPlayerRepository.existsByAllyCode(allyCode)) { "AllyCode $allyCode is already registered" }

        val existingInSlot = discordPlayerRepository.findByDiscordUserIdAndSlot(user.id.asLong(), slot)
        require(existingInSlot == null) { "You have already linked ${existingInSlot?.allyCode} for slot $slot. Please unlink first" }

        // will throw an exception if ally code cannot be found. should be handled by calling service
        val comlinkPlayer = comlinkApi.findPlayerByAllyCode(allyCode = allyCode)

        discordPlayerRepository.save(
            DiscordPlayer(
                id = null,
                allyCode = allyCode,
                swgohPlayerId = comlinkPlayer.playerId,
                discordUserId = user.id.asLong(),
                slot = slot
            )
        )
        sendInvalidateCacheEventIfSuccess(user.id.asLong(), slot)
    }

    @Transactional
    fun unlinkPlayer(allyCode: String) {
        validateAllyCode(allyCode)

        val existing = discordPlayerRepository.findByAllyCode(allyCode)
        requireNotNull(existing) { "Unable to find the allyCode" }

        discordPlayerRepository.delete(existing)
        sendInvalidateCacheEventIfSuccess(existing.discordUserId, existing.slot)
    }

    @Transactional
    fun unlinkPlayer(user: User, allyCode: String): Pair<String, Long> {
        validateAllyCode(allyCode)

        val existing = discordPlayerRepository.findByDiscordUserIdAndAllyCode(user.id.asLong(), allyCode)
        requireNotNull(existing) { "Unable to find the allyCode" }

        discordPlayerRepository.delete(existing)
        sendInvalidateCacheEventIfSuccess(user.id.asLong(), existing.slot)

        return Pair(existing.allyCode, existing.slot)
    }

    @Transactional
    fun unlinkPlayer(user: User, slot: Long): Pair<String, Long> {
        validateSlot(slot)

        val existing = discordPlayerRepository.findByDiscordUserIdAndSlot(user.id.asLong(), slot)
        requireNotNull(existing) { "Nothing is linked for slot $slot" }

        discordPlayerRepository.delete(existing)
        sendInvalidateCacheEventIfSuccess(user.id.asLong(), existing.slot)

        return Pair(existing.allyCode, existing.slot)
    }

    @Transactional
    fun listUserLinkedAccounts(user: User): List<DiscordUserInfo> =
        discordPlayerRepository.findByDiscordUserId(user.id.asLong())
            .map { discordUser ->
                comlinkApi.findPlayerById(discordUser.swgohPlayerId).let {
                    DiscordUserInfo(
                        allyCode = it.allyCode,
                        slot = discordUser.slot,
                        playerName = it.name,
                        guildId = it.guildId,
                        guildName = it.guildName
                    )
                }
            }.sortedBy { it.slot }

    private fun sendInvalidateCacheEventIfSuccess(discordUserId: Long, slot: Long) = applicationEventPublisher.publishEvent(
        DiscordPlayerCacheService.InvalidateDiscordPlayerCache(
            discordUserId = discordUserId,
            slot = slot
        )
    )

    data class DiscordUserInfo(val allyCode: String, val slot: Long, val playerName: String, val guildId: String, val guildName: String)
}