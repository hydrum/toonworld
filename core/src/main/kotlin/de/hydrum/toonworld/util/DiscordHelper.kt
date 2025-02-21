package de.hydrum.toonworld.util

import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.User
import reactor.core.publisher.Mono
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

fun User.getMemberOrNull(discordGuildId: Optional<Snowflake>) =
    if (discordGuildId.isPresent)
        asMember(discordGuildId.get())
            .onErrorResume { Mono.empty() }
            .block(1.seconds.toJavaDuration())
    else null

fun User.getNicknameOrNull(discordGuildId: Optional<Snowflake>) = getMemberOrNull(discordGuildId)?.nickname?.getOrNull()