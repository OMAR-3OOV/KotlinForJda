import com.sun.istack.Nullable
import commands.Potato
import commands.Question
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.FileNotFoundException
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.collections.HashMap

open class CommandManager(private val bot: Main) {

    private val commands: HashMap<String, Command> = HashMap()

    init {
    }

    open fun addAllCommands() {
        addCommand(Question(), Potato())

        bot.Logger().info("commands added! Commands : " + commands.keys.stream().collect(Collectors.joining(" / ")))
    }

    /**
     * @param event return to the Message Received Event in the JDA Api
     * @param prefix return to the bot prefix, so user should use <prefix> first and then the command!
     *
     * Make sure that even if there is arguments existing the command will work,
     * so you should use if statement in command class to void this problem but , in other hand, it won't make any issues with command
     */
    @Throws(FileNotFoundException::class)
    open fun handleCommand(event: @Nullable MessageReceivedEvent, prefix: String) {
        val split: List<String> = event.message.contentRaw.replaceFirst(Pattern.quote(prefix).toRegex(), "").split(" ")
        val command: String = split[0].lowercase(Locale.getDefault())

        if (commands.containsKey(command)) {
            val args: List<String> = split.subList(1, split.size)

            commands[command]?.handle(args, event)
        }
    }

    /**
     * The command should exist in the Map, otherwise, it won't work!
     */
    private fun addCommand(vararg command: Command?) {
        for (cmd in command) {
            if (cmd != null) {
                if (!commands.containsKey(cmd.getCommand)) {

                    // Simple to commands.put(key, value) but with other way
                    commands[cmd.getCommand] = cmd
                }
            }
        }
    }
}