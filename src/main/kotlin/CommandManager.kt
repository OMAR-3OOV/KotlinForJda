import com.sun.istack.Nullable
import commands.potatoCommand
import commands.questionCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.FileNotFoundException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class CommandManager(val bot: Main) {

    val cmds: HashMap<String, Command> = HashMap()

    init {
        addCommand(questionCommand())
        addCommand(potatoCommand())

        bot.Logger().info("commands added!")
    }

    @Throws(FileNotFoundException::class)
    fun handleCommand(event: @Nullable MessageReceivedEvent, prefix: String) {
        val split: List<String> = event.message.contentRaw.replaceFirst(Pattern.quote(prefix).toRegex(), "").split(" ")
        val command: String = split[0].lowercase(Locale.getDefault())

        if (cmds.containsKey(command)) {
            val args: List<String> = split.subList(1, split.size)

            cmds[command]?.handle(args, event)
        }
    }

    private fun addCommand(command: Command) {
        if (!cmds.containsKey(command.getCommand)) {
            // Add the command
            cmds[command.getCommand] = command
        }
    }

}