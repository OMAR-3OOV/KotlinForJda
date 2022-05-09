package commands.gamesCategory

import Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories

class RPC: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        // codes
    }

    override val help: String
        get() = "``` \n" +
                "- To play rpc with someone use r?${command} <user> / <user id>\n" +
                "- To play rpc with bot use r?${command}"
    override val command: String
        get() = "rpc"
    override val category: Categories
        get() = Categories.GAMES
    override val description: String
        get() = "You can play rock paper scissors with bot/someone and having fun!"
    override val isDisplay: Boolean
        get() = true
}