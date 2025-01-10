//package de.hydrum.toonworld.sync
//
//import de.hydrum.toonworld.database.repository.PlayerRepository
//import io.github.oshai.kotlinlogging.KotlinLogging
//import org.junit.jupiter.api.Assertions.assertNotNull
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.annotation.Rollback
//import org.springframework.transaction.annotation.Transactional
//
//@SpringBootTest
//@Transactional
//@Rollback
//class PlayerSyncServiceTest(
//    @Autowired private val playerSyncService: PlayerSyncService,
//    @Autowired private val playerRepository: PlayerRepository
//) {
//
//    @Test
//    fun `player will be persisted to DB`() {
//        playerSyncService.syncPlayer(154962211)
//
//        val dbPlayer = playerRepository.findPlayerByAllyCode(154962211)
//
//        log.info { dbPlayer }
//
//        assertNotNull(dbPlayer)
//    }
//
//    companion object {
//        private val log = KotlinLogging.logger { }
//    }
//}