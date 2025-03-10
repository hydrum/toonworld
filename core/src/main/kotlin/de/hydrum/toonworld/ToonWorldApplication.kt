package de.hydrum.toonworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
class ToonWorldApplication

fun main(args: Array<String>) {
    runApplication<ToonWorldApplication>(*args)
}
