package de.hydrum.toonworld.farm.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FarmRepository : JpaRepository<Farm, Long> {
    fun getAllByUnlockBaseIdIsNotNull(): List<Farm>

    fun getFarmByUnlockBaseId(baseId: String): Farm?
}
