package commands

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import utilities.staffUtil.Roles

class Question: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        event.channel.sendMessage("NO IM NOT GAY").queue()
    }

    override val help: String
        get() = "r?${command}"
    override val command: String
        get() = "areyougay"
    override val category: Categories
        get() = Categories.FUN
    override val roles: List<Roles>
        get() = Roles.values().toList()
    override val description: String
        get() = "ask me if im gay"
    override val isDisplay: Boolean
        get() = true


}