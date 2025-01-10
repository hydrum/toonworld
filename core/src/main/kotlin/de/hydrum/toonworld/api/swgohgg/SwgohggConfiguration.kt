package de.hydrum.toonworld.api.swgohgg

import de.hydrum.toonworld.api.swgohgg.apis.UnitApi
import de.hydrum.toonworld.api.swgohgg.models.Ability
import de.hydrum.toonworld.config.AppConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import de.hydrum.toonworld.api.swgohgg.models.Unit as SwgohggUnit

@Configuration
class SwgohggConfiguration {

    @Bean
    fun swgohggApi(config: AppConfig): SwgohggApi = with(config.api.swgohgg) {
        SwgohggApi(
            UnitApi(basePath = baseUrl)
        )
    }

}

class SwgohggApi(
    val unitApi: UnitApi
) {
    fun getUnits(): List<SwgohggUnit> = unitApi.getUnits().data

    fun getAbilities(): List<Ability> = unitApi.getAbilities()
}