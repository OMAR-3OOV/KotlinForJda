package commands.funCategory

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class Funfact: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        // codes
    }

    override val help: String
        get() = "r?funfact"
    override val command: String
        get() = "funfact"
    override val description: String
        get() = "i will tell you a funfact"
    override val isDisplay: Boolean
        get() = true
}