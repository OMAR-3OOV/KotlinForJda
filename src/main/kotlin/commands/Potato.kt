package commands

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
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

    override val getHelp: String
        get() = getCommand
    override val getCommand: String
        get() = "potato"
    override val getDescription: String
        get() = "I will gives you unlimited potatos :3"
    override val isDisplay: Boolean
        get() = true


}