package commands

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class questionCommand: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        event.channel.sendMessage("No master, im not gay").queue()
    }

    override val getHelp: String
        get() = "r?${getCommand}"
    override val getCommand: String
        get() = "areyougay"
    override val getDescription: String
        get() = "ask me if im gay"
    override val isDisplay: Boolean
        get() = true


}