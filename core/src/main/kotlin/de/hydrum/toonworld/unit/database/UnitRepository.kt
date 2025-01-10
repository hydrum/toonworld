package de.hydrum.toonworld.unit.database

import de.hydrum.toonworld.unit.database.model.Unit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UnitRepository : JpaRepository<Unit, Long> {
    fun findByBaseId(baseId: String): Unit?
}