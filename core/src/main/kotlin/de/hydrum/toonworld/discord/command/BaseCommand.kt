package de.hydrum.toonworld.discord.command

import de.hydrum.toonworld.util.errorIsAllyCodeCannotBeFound
import de.hydrum.toonworld.util.errorIsGuildCannotBeFound
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.spec.InteractionReplyEditSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import kotlin.jvm.optionals.getOrDefault

abstract class BaseCommand(
    val name: String,
    val description: String,
    val isSuperAdminCmd: Boolean = false,
    val options: List<ApplicationCommandOptionData> = emptyList()
) {
    abstract fun handle(event: ChatInputInteractionEvent)

    open fun autocomplete(event: ChatInputAutoCompleteEvent) {}

    fun handleError(event: ChatInputInteractionEvent, e: Throwable, option: String? = null): Unit = with(event) {
        (if (e is IllegalArgumentException) e.message
        else if (errorIsGuildCannotBeFound(e)) "Failed to find guild"
        else if (option != null && errorIsAllyCodeCannotBeFound(e, option)) "Failed to find allyCode `$option`"
        else ":warning: Error while handling :warning:")
            .let { error ->
                editReply(
                    InteractionReplyEditSpec
                        .builder()
                        .build()
                        .withContentOrNull(error)
                ).subscribe()

                // logging will be done elsewhere
                throw e
            }
    }

    //    fun ChatInputAutoCompleteEvent.getValueAsStringOrBlank() = focusedOption.value.map { it.asString() }.getOrDefault("")
    fun ChatInputAutoCompleteEvent.getValueAsStringOrBlank() = focusedOption.value.map { it.raw.toString() }.getOrDefault("")
}