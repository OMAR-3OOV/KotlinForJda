package commands

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtility.Categories
import utilities.staffUtility.Roles
import java.lang.NullPointerException

class Potato: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        try {
            if (args.isEmpty()) {
                event.channel.sendMessage("Here a potato for you sweetie :3 :potato:").queue()
            }
        } catch (error: NullPointerException) {
            error.printStackTrace()
        }
    }

    override val help: String
        get() = command
    override val command: String
        get() = "potato"
    override val category: Categories
        get() = Categories.FUN
    override val roles: List<Roles>
        get() = Roles.values().toList()
    override val description: String
        get() = "I will gives you unlimited potatos :3"
    override val isDisplay: Boolean
        get() = true


}