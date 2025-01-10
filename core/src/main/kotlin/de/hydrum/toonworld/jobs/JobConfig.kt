package de.hydrum.toonworld.jobs

import de.hydrum.toonworld.sync.GuildSyncJob
import de.hydrum.toonworld.sync.PlayerSyncJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.JobDetailFactoryBean

@Configuration
class JobConfig {

    @Bean(name = [PlayerSyncJob.JOB_NAME])
    fun playerSyncJobDetail(): JobDetailFactoryBean =
        JobDetailFactoryBean()
            .also { it.setJobClass(PlayerSyncJob::class.java) }
            .also { it.setDurability(true) }

    @Bean(name = [GuildSyncJob.JOB_NAME])
    fun guildSyncJobDetail(): JobDetailFactoryBean =
        JobDetailFactoryBean()
            .also { it.setJobClass(GuildSyncJob::class.java) }
            .also { it.setDurability(true) }
}