package de.hydrum.toonworld.management

import de.hydrum.toonworld.farm.database.Farm
import de.hydrum.toonworld.management.database.DiscordGuildRepository
import de.hydrum.toonworld.player.database.model.Player
import de.hydrum.toonworld.sync.GuildSyncService
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class DiscordRoleAssignmentService(
    val discordGuildRepository: DiscordGuildRepository,
    val discordClient: GatewayDiscordClient
) {

    @Transactional
    fun checkForAssignments(swgohGuildId: String) {
        val discordGuild = discordGuildRepository.findBySwgohGuildId(swgohGuildId = swgohGuildId)
        val discordPlayerOfGuild = discordGuild?.guild
            ?.members
            ?.mapNotNull { it.player?.discordPlayer }

        if (discordGuild == null || discordGuild.farms.isEmpty() || discordPlayerOfGuild.isNullOrEmpty()) {
            log.debug { "nothing to be done for guildId=$swgohGuildId" }
            return
        }

        val discordPlayerIds = discordPlayerOfGuild.map { it.discordUserId }
        val discordPlayerToMember = discordClient.getGuildMembers(Snowflake.of(discordGuild.discordGuildId))
            .filter { it.id.asLong() in discordPlayerIds }
            .map { member -> discordPlayerOfGuild.first { it.discordUserId == member.id.asLong() } to member }
            .collectList()
            .block(5.seconds.toJavaDuration())

        discordGuild.farms
            .filter { it.discordRoleId != null }
            .map { farm -> farm to discordPlayerOfGuild.filter { it.player?.hasCompletedFarm(farm.farm) == true } }
            .forEach { (farmRole, discordPlayerList) ->
                val farmDiscordRoleId = Snowflake.of(farmRole.discordRoleId!!)
                val member = discordPlayerToMember?.filter { it.first in discordPlayerList }
                member
                    ?.filter { (_, member) -> farmDiscordRoleId !in member.roleIds }
                    ?.forEach { (discordPlayer, member) ->
                        log.info { "@${member.displayName} as ${discordPlayer.player?.name} has been assigned the role $farmDiscordRoleId for ${farmRole.farm.name}." }
                        member.addRole(farmDiscordRoleId).subscribe()
                    }
            }
        log.debug { "done assigning roles for guildId=$swgohGuildId" }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun triggerAfterGuildSync(event: GuildSyncService.AfterGuildSyncEvent) = checkForAssignments(swgohGuildId = event.swgohGuildId)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun triggerEvent(event: CheckDiscordRoleAssignment) = checkForAssignments(swgohGuildId = event.swgohGuildId)

    class CheckDiscordRoleAssignment(val swgohGuildId: String)

    companion object {
        private val log = KotlinLogging.logger { }
    }
}

fun Player.hasCompletedFarm(farm: Farm): Boolean =
    farm.units.map { farmUnit ->
        units.firstOrNull { it.baseId == farmUnit.baseId }
            .let {
                it != null
                        && it.rarity >= farmUnit.minRarity
                        && it.gearLevel >= farmUnit.minGearLevel
                        && it.relicTier.ordinal >= farmUnit.minRelicTier.ordinal
            }
            .let { Pair(farmUnit.required, it) }
    }.let {
        val requiredNotMatched = it.any { (required, fulfilled) -> required && !fulfilled }
        if (requiredNotMatched) return false
        return farm.teamSize <= it.count { (_, fulfilled) -> fulfilled }
    }