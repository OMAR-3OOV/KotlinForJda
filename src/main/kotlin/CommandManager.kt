import utilities.categoryUtil.Categories
import utilities.categoryUtil.CategoryManager
import com.sun.istack.Nullable
import commands.Help
import commands.Potato
import commands.Question
import commands.adminCategory.BotInfos
import commands.funCategory.Funfact
import commands.gamesCategory.RPC
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.staffUtil.Roles
import utilities.staffUtil.RolesData
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
open class CommandManager(bot: Main.Companion) {

    var commands: MutableMap<String, Command> = LinkedHashMap()

    private var categories: MutableMap<String, Categories> = HashMap()
    open var commandsByCategory = TreeMap<Categories, List<Command>>() // easy way to call the commands, and it made the code go faster than before

    init {

        val (task, time) = measureTimedValue { addCommand(
            Question(),
            Potato(),
            Help(bot),
            Funfact(),
            RPC(),
            BotInfos()
        )}

        println(time)

        registerCategories()
        registerCommandsIntoCategory()
    }

    /**
     * @param event return to the Message Received Event in the JDA Api
     * @param prefix return to the bot prefix, so user should use <prefix> first and then the command!
     *
     * Make sure that even if there is arguments existing the command will work,
     * so you should use if statement in command class to void this problem but , in other hand, it won't make any issues with command
     */
    @Throws(NullPointerException::class)
    open fun handleCommand(event: @Nullable MessageReceivedEvent, prefix: String) {
        val rolesData = RolesData(event.author, Roles.EVERYONE)
        val split: List<String> =
            event.message.contentRaw.lowercase().replaceFirst(Pattern.quote(prefix).toRegex(), "").split(" ")
        val command: String = split[0].lowercase(Locale.getDefault())

        if (commands.containsKey(command)) {
            if (!commands[command]!!.roles.contains(rolesData.getUserRole())) {
                event.channel.sendMessage("> Sorry you don't have the role to use this command!").queue()
                return
            }

            val args: List<String> = split.subList(1, split.size)

            commands[command]?.handle(args, event)
        }
    }

    /**
     * a supertype function
     * The command should exist in the Map, otherwise, it won't work!
     */

    private fun addCommand(vararg commands: Command) {
        commands.forEach { command ->
            if (this.commands.containsKey(command.command)) return
            this.commands[command.command] = command
        }
    }

    /**
     * Just to register the categories into hashmap & use them as a information and concept for the commands
     *
     * NOTE: This hashmap is just to get the categories and not the commands from the categories
     * first, because the way to call this hashmap is by using <category-name>:<category-id>.
     * it's really special way to recall the hashmap, and it's key for every category.
     * so even if there is a changes in future in categories, old data won't erase.
     */
    private fun registerCategories() {
        for (category in Categories.values()) {
            val cm = CategoryManager(category)

            if (categories.containsKey(cm.keyId())) continue // keyId is simple to <category-name>:<category-id>
            categories[cm.keyId()] = cm.category
        }
    }

    /**
     * So basically this method created to call the all commands that use specific category.
     * now we can get the commands with only calling this method,
     * no need for stream 2 times like before & it's way faster for recalling
     */
    private fun registerCommandsIntoCategory() {
        Categories.values().forEach { category ->
            commandsByCategory[category] = commands.values.stream().filter { filter -> filter.category == category }.toList()
        }
    }
}