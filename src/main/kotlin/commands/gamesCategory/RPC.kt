package commands.gamesCategory

import Command
import dev.minn.jda.ktx.messages.Embed
import gameUtilities.RPSUtility
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import utilities.staffUtil.Roles
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors

class RPC : Command {

    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        // codes
        val embed = EmbedBuilder()
        val user = event.author

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

        // commands : r?rpc <opponent> <rounds>

        if (args.isNotEmpty()) {
            var opponent = args.get(0)

            val regex = Pattern.compile(Message.MentionType.USER.pattern.toString())
            val matcher = regex.matcher(opponent)

            opponent = if (matcher.find()) {
                opponent.replace("<", "").replace("!", "").replace("@", "").replace("#", "").replace("&", "")
                    .replace(">", "");
            } else {
                event.jda.getUsersByName(opponent, true).stream().map { m -> m.id }.collect(Collectors.joining())
            }

            val target = event.jda.getUserById(opponent)
            val rps = RPSUtility(user, target, event.guild, event.textChannel, embed)

            if (args.size > 1) {
                if (args[1].equals("-r")) {
                    rps.unlimitedLoop = true
                }
            }
        } else {
            val rps = RPSUtility(user, null, event.guild, event.textChannel, embed)
        }
    }


    override val help: String
        get() = String.format("\n```md\n# To play rpc with someone use r?${command} <user> / <user-id>\n# To play rpc with bot use r?${command}```")
    override val command: String
        get() = "rps"
    override val category: Categories
        get() = Categories.GAMES
    override val roles: List<Roles>
        get() = Roles.values().toList()
    override val description: String
        get() = "You can play rock paper scissors with bot/someone and having fun!"
    override val isDisplay: Boolean
        get() = true
}
