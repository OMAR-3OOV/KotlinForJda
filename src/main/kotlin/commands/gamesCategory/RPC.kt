package commands.gamesCategory

import Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import java.lang.NullPointerException

class RPC : Command {

    companion object {
        @JvmStatic
        var rpc: MutableMap<User, RPC_Data> = LinkedHashMap()
        var message: MutableMap<RPC_Data, Message> = LinkedHashMap()
    }

    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        // codes
        val embed = EmbedBuilder()
        val user = event.author

        try {

            val rock: String = "\uD83C\uDFFB"
            val paper: String = "\uD83D\uDD90\uD83C\uDFFB"
            val scissors: String = "\uD83C\uDFFB"

            val gui: StringBuilder = StringBuilder()

            /**
             * The game gui
             *
             * ------------------
             * |................|
             * |....p1....p2....|
             * |................|
             * -----------------=
             *
             * it will be replacement string
             */

            gui.append("------------------").append("\n")
            gui.append("|................|").append("\n")
            gui.append("|....P1....P2....|").append("\n")
            gui.append("|................|").append("\n")
            gui.append("------------------").append("\n")

            if (args.isEmpty()) {
                val bot = event.jda.selfUser;

                embed.setTitle("RPC ( ${user.name} vs ${event.jda.selfUser.name} )")

                val rpcGame = RPC_Data(user, bot, event.textChannel, embed)
            }

        } catch (userErr: NullPointerException) {

            event.channel.sendMessageEmbeds(embed.build())
        }
    }


    override val help: String
        get() = String.format("\n```md\n# To play rpc with someone use r?${command} <user> / <user-id>\n# To play rpc with bot use r?${command}```")
    override val command: String
        get() = "rpc"
    override val category: Categories
        get() = Categories.GAMES
    override val description: String
        get() = "You can play rock paper scissors with bot/someone and having fun!"
    override val isDisplay: Boolean
        get() = true
}
