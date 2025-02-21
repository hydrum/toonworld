package de.hydrum.toonworld.management

import de.hydrum.toonworld.api.comlink.ComlinkApi
import de.hydrum.toonworld.guild.database.repository.GuildRepository
import de.hydrum.toonworld.management.database.DiscordPlayer
import de.hydrum.toonworld.management.database.DiscordPlayerRepository
import de.hydrum.toonworld.player.database.repository.PlayerRepository
import de.hydrum.toonworld.util.validateAllyCode
import de.hydrum.toonworld.util.validateSlot
import discord4j.core.`object`.entity.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlayerLinkService(
    private val discordPlayerRepository: DiscordPlayerRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val comlinkApi: ComlinkApi,
    private val guildRepository: GuildRepository,
    private val playerCacheService: DiscordPlayerCacheService, private val playerRepository: PlayerRepository
) {

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
    fun listUserLinkedAccounts(user: User): List<DiscordSingleUserInfo> =
        discordPlayerRepository.findByDiscordUserId(user.id.asLong())
            .map { discordUser ->
                comlinkApi.findPlayerById(discordUser.swgohPlayerId).let {
                    DiscordSingleUserInfo(
                        allyCode = it.allyCode,
                        slot = discordUser.slot,
                        playerName = it.name,
                        guildId = it.guildId,
                        guildName = it.guildName
                    )
                }
            }.sortedBy { it.slot }

    @Transactional
    fun listGuildLinkAccounts(swgohGuildId: String): List<DiscordGuildUserInfo> =
        guildRepository.findBySwgohGuildId(swgohGuildId = swgohGuildId)
            ?.members.orEmpty()
            .map { it.swgohPlayerId }
            .let {
                val discordPlayers = discordPlayerRepository.findBySwgohPlayerIdIn(it)
                val players = playerRepository.findBySwgohPlayerIdIn(it)
                return@let players.map { player -> Pair(player, discordPlayers.firstOrNull { it.swgohPlayerId == player.swgohPlayerId }) }
            }
            .map { (player, discordUser) ->
                DiscordGuildUserInfo(
                    playerName = player.name,
                    allyCode = player.allyCode,
                    guildId = player.swgohGuildId ?: "",
                    guildName = player.guildName ?: "",
                    userId = discordUser?.discordUserId,
                    slot = discordUser?.slot,
                )
            }
            .sortedBy { it.playerName.lowercase() }

    private fun sendInvalidateCacheEventIfSuccess(discordUserId: Long, slot: Long) = applicationEventPublisher.publishEvent(
        DiscordPlayerCacheService.InvalidateDiscordPlayerCache(
            discordUserId = discordUserId,
            slot = slot
        )
    )

    data class DiscordSingleUserInfo(val allyCode: String, val slot: Long, val playerName: String, val guildId: String, val guildName: String)
    data class DiscordGuildUserInfo(val playerName: String, val allyCode: String, val guildId: String, val guildName: String, val userId: Long?, val slot: Long?)
}