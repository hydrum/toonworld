package de.hydrum.toonworld.api.comlink

import de.hydrum.toonworld.guild.database.model.GuildMemberLevel
import de.hydrum.toonworld.util.elseTake
import de.hydrum.toonworld.util.firstOrDefault
import de.hydrum.toonworld.util.utcNow
import java.time.Instant
import de.hydrum.toonworld.api.comlink.models.Guild as ComlinkGuild
import de.hydrum.toonworld.api.comlink.models.GuildMember as ComlinkGuildMember
import de.hydrum.toonworld.api.comlink.models.GuildRaidResult as ComlinkGuildRaid
import de.hydrum.toonworld.api.comlink.models.GuildRaidResultMember as ComlinkGuildRaidMember
import de.hydrum.toonworld.guild.database.model.Guild as ToonWorldGuild
import de.hydrum.toonworld.guild.database.model.GuildMember as ToonWorldGuildMember
import de.hydrum.toonworld.guild.database.model.GuildRaid as ToonWorldGuildRaid
import de.hydrum.toonworld.guild.database.model.GuildRaidMember as ToonWorldGuildRaidMember


fun ComlinkGuild.toEntity() =
    ToonWorldGuild(
        id = null,
        swgohGuildId = profile.id,
        name = profile.name,
        updateTime = utcNow(),
        bannerLogoId = profile.bannerLogoId,
        bannerColorId = profile.bannerColorId,
        memberCount = profile.memberCount,
        galacticPower = profile.guildGalacticPower.toLong(),
        nextResetTime = Instant.ofEpochSecond(nextChallengesRefresh.toLong()),
        members = member.map { it.toEntity() }.toMutableList(),
        raids = recentRaidResult.map { it.toEntity() }.toMutableList()
    )
        // set back references
        .also { guild -> guild.members.forEach { it.guild = guild } }
        .also { guild ->
            guild.raids.forEach { raid ->
                raid.guild = guild
                raid.members.forEach { guildRaidMember ->
                    guildRaidMember.raid = raid
                    guildRaidMember.guild = guild
                    guildRaidMember.member = guild.members.find { member -> member.swgohPlayerId == guildRaidMember.swgohPlayerId }
                }
            }
        }

fun ComlinkGuildMember.toEntity() =
    ToonWorldGuildMember(
        id = null,
        guild = null,
        swgohPlayerId = playerId,
        name = playerName,
        memberLevel = GuildMemberLevel.entries.firstOrDefault(memberLevel, GuildMemberLevel.UNKNOWN),
        joinTime = Instant.ofEpochSecond(guildJoinTime.toLong()),
        galacticPower = galacticPower.toLong(),
        lastActivityTime = Instant.ofEpochMilli(lastActivityTime.toLong()),
        guildTokens = memberContribution.find { it.type == 1 }?.lifetimeValue?.toLong() ?: 0,
        raidTickets = memberContribution.find { it.type == 2 }?.lifetimeValue?.toLong() ?: 0,
        donations = memberContribution.find { it.type == 3 }?.lifetimeValue?.toLong() ?: 0
    )

fun ComlinkGuildRaid.toEntity() =
    ToonWorldGuildRaid(
        id = null,
        guild = null,
        raidId = raidId,
        endTime = Instant.ofEpochSecond(endTime.toLong()),
        score = guildRewardScore.toLong(),
        members = raidMember.map { it.toEntity() }.toMutableList()
    )

fun ComlinkGuildRaidMember.toEntity() =
    ToonWorldGuildRaidMember(
        id = null,
        guild = null,
        raid = null,
        member = null,
        swgohPlayerId = playerId,
        score = memberProgress.toLong()
    )

infix fun ToonWorldGuild.updatedBy(comlinkGuild: ComlinkGuild): ToonWorldGuild {
    swgohGuildId = comlinkGuild.profile.id
    name = comlinkGuild.profile.name
    updateTime = utcNow()
    bannerLogoId = comlinkGuild.profile.bannerLogoId
    bannerColorId = comlinkGuild.profile.bannerColorId
    memberCount = comlinkGuild.profile.memberCount
    galacticPower = comlinkGuild.profile.guildGalacticPower.toLong()
    nextResetTime = Instant.ofEpochSecond(comlinkGuild.nextChallengesRefresh.toLong())

    comlinkGuild.member.map { member ->
        members.find { it.swgohPlayerId == member.playerId }
            ?.updatedBy(member)
            .elseTake { member.toEntity() }
    }.also { this.members.clear() }.also { this.members.addAll(it) }

    comlinkGuild.recentRaidResult.map { comlinkRaid ->
        raids.find { it.raidId == comlinkRaid.raidId && it.endTime == Instant.ofEpochSecond(comlinkRaid.endTime.toLong()) }
            ?.updatedBy(comlinkRaid)
            ?: raids.add(comlinkRaid.toEntity())
    }

    // set back references
    members.forEach { it.guild = this }
    raids.forEach { raid ->
        raid.guild = this
        raid.members.forEach { guildRaidMember ->
            guildRaidMember.raid = raid
            guildRaidMember.guild = this
            guildRaidMember.member = members.find { member -> member.swgohPlayerId == guildRaidMember.swgohPlayerId }
        }
    }
    return this
}

infix fun ToonWorldGuildMember.updatedBy(comlinkGuildMember: ComlinkGuildMember): ToonWorldGuildMember {
    name = comlinkGuildMember.playerName
    memberLevel = GuildMemberLevel.entries.firstOrDefault(comlinkGuildMember.memberLevel, GuildMemberLevel.UNKNOWN)
    joinTime = Instant.ofEpochSecond(comlinkGuildMember.guildJoinTime.toLong())
    galacticPower = comlinkGuildMember.galacticPower.toLong()
    lastActivityTime = Instant.ofEpochMilli(comlinkGuildMember.lastActivityTime.toLong())
    guildTokens = comlinkGuildMember.memberContribution.find { it.type == 1 }?.lifetimeValue?.toLong() ?: 0
    raidTickets = comlinkGuildMember.memberContribution.find { it.type == 2 }?.lifetimeValue?.toLong() ?: 0
    donations = comlinkGuildMember.memberContribution.find { it.type == 3 }?.lifetimeValue?.toLong() ?: 0
    return this
}

infix fun ToonWorldGuildRaid.updatedBy(comlinkGuildRaid: ComlinkGuildRaid): ToonWorldGuildRaid {
    raidId = comlinkGuildRaid.raidId
    score = comlinkGuildRaid.guildRewardScore.toLong()
    endTime = Instant.ofEpochSecond(comlinkGuildRaid.endTime.toLong())
    comlinkGuildRaid.raidMember.map { raidMember ->
        members.find { it.swgohPlayerId == raidMember.playerId }
            ?.updatedBy(raidMember)
            .elseTake { raidMember.toEntity() }
    }.also { this.members.clear() }.also { this.members.addAll(it) }
    return this
}

infix fun ToonWorldGuildRaidMember.updatedBy(comlinkGuildRaidMember: ComlinkGuildRaidMember): ToonWorldGuildRaidMember {
    score = comlinkGuildRaidMember.memberProgress.toLong()
    return this
}
