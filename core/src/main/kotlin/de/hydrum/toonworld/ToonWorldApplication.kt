package de.hydrum.toonworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.Locale

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
class ToonWorldApplication

fun main(args: Array<String>) {
    Locale.setDefault(Locale.ENGLISH)
    runApplication<ToonWorldApplication>(*args)
}
