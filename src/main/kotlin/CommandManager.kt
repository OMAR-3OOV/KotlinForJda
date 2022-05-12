import utilities.categoryUtil.Categories
import utilities.categoryUtil.CategoryManager
import com.sun.istack.Nullable
import commands.Help
import commands.Potato
import commands.Question
import commands.funCategory.Funfact
import commands.gamesCategory.RPC
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.FileNotFoundException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

open class CommandManager(bot: Main.Companion) {

    var commands: MutableMap<String, Command> = HashMap()

    private var categories: MutableMap<String, Categories> = HashMap()

    init {
        addCommand(
            Question(),
            Potato(),
            Help(bot),
            Funfact(),
            RPC(),
        )

        registerCategories()
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
        val split: List<String> =
            event.message.contentRaw.lowercase().replaceFirst(Pattern.quote(prefix).toRegex(), "").split(" ")
        val command: String = split[0].lowercase(Locale.getDefault())

        if (commands.containsKey(command)) {
            val args: List<String> = split.subList(1, split.size)

            commands[command]!!.handle(args, event)
        }
    }

    /**
     * a supertype function
     * The command should exist in the Map, otherwise, it won't work!
     *
     * New
     */
    private fun addCommand(vararg commands: Command) {
        for (command in commands) {
            if (this.commands.containsKey(command.command)) continue
            this.commands[command.command] = command
        }
    }

    private fun registerCategories() {
        for (category in Categories.values()) {
            val cm = CategoryManager(category)

            if (categories.containsKey(cm.keyId())) continue // keyId is simple to <category-name>:<category-id>
            categories[cm.keyId()] = cm.category
        }
    }
}