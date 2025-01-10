package de.hydrum.toonworld.sync

import de.hydrum.toonworld.api.swgohgg.SwgohggApi
import de.hydrum.toonworld.api.swgohgg.toEntity
import de.hydrum.toonworld.api.swgohgg.updatedBy
import de.hydrum.toonworld.api.swgohgg.updates
import de.hydrum.toonworld.unit.database.UnitRepository
import de.hydrum.toonworld.util.ErrorHelper
import de.hydrum.toonworld.util.elseTake
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import de.hydrum.toonworld.api.swgohgg.models.Unit as SwgohggUnit
import de.hydrum.toonworld.unit.database.model.Unit as ToonWorldUnit

@Service
class UnitSyncService(
    private val swgohggApi: SwgohggApi,
    private val unitRepository: UnitRepository,
    private val errorHelper: ErrorHelper
) {

    @Transactional
    fun updateUnits() {
        val units: List<SwgohggUnit> = swgohggApi.getUnits()
        val abilities = swgohggApi.getAbilities()
        val unitAbilities = abilities.groupBy { it.characterBaseId ?: it.shipBaseId }

        log.debug { "found units: ${units.size} | found abilities: ${abilities.size}" }

        if (unitAbilities.contains(null)) with(unitAbilities[null]) {
            errorHelper.sendErrorMessage("Cannot match ${this?.size} abilities to an ability. abilities: ${this?.joinToString(", ") { it.baseId }}")
        }

        val entities: List<ToonWorldUnit> = unitRepository.findAll()

        units.map { unit ->
            entities.find { entity -> entity.baseId == unit.baseId }
                ?.let { unit updates it }
                .elseTake { unit.toEntity() }
                .also { entity ->
                    unitAbilities[entity.baseId]
                        ?.map { ability ->
                            entity.abilities.find { it.baseId == ability.baseId }?.updatedBy(ability).elseTake { ability.toEntity() }
                                .also { it.unit = entity }
                        }?.also { entity.abilities.clear() }?.also { entity.abilities.addAll(it) }
                }.also {
                    if (it.abilities.isEmpty() == true) errorHelper.sendErrorMessage("no abilities found for ${it.baseId}")
                }

        }.also { unitRepository.saveAll(it) }
    }

    companion object {

        private val log = KotlinLogging.logger { }

    }
}