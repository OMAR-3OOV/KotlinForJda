package commands.funCategory

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtility.Categories
import utilities.staffUtility.Roles

class Funfact: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        // codes
    }

    override val help: String
        get() = "r?funfact"

    override val command: String
        get() = "funfact"

    override val category: Categories
        get() = Categories.FUN

    override val roles: List<Roles>
        get() = Roles.values().toList()

    override val description: String
        get() = "i will tell you a funfact"

    override val isDisplay: Boolean
        get() = true
}