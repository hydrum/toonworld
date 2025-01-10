package de.hydrum.toonworld.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(value = "app")
class AppConfig(
    var sync: SyncConfig,
    var api: ApiCategoryConfig,
    var discord: DiscordConfig,
    var cache: CacheAppConfig,
    var data: DataAppConfig,
)

data class SyncConfig(
    val player: PlayerSyncConfig,
    val guild: GuildSyncConfig,
    val unit: UnitSyncConfig
)

data class PlayerSyncConfig(
    val delayInSeconds: Long,
    val errorDelayInSeconds: Long
)

data class GuildSyncConfig(
    val delayInSeconds: Long,
    val errorDelayInSeconds: Long
)

data class UnitSyncConfig(
    val fixedDelayInMilliseconds: Long,
    val initialDelayInMilliseconds: Long
)

data class ApiCategoryConfig(
    val swgohgg: ApiConfig,
    val comlink: ApiConfig
)

data class ApiConfig(
    val baseUrl: String
)

data class DiscordConfig(
    val botToken: String,
    val superAdmin: DiscordSuperAdminConfig
)

data class DiscordSuperAdminConfig(
    val guild: String,
    val testChannel: String,
    val errorChannel: String
)

data class CacheAppConfig(
    val expireAfterWriteInMinutes: Long
)

data class DataAppConfig(
    val modFile: String
)