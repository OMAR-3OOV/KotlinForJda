package commands

import Command
import CommandManager
import Main
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.util.stream.Collectors

class Help(private val bot: Main.Companion) : Command {

    override fun handle(args: List<String>, event: MessageReceivedEvent) {

        val embed = EmbedBuilder()
        val commandManager = CommandManager(bot)

        val commands: MutableMap<String, Command> = commandManager.commands

        if (args.isEmpty()) {
            val description: StringBuilder = embed.descriptionBuilder

            embed.setTitle("⚙ Help assistant!")

            /**
             *  it's really simple format for codes which is per each 3 values in the map.
             *  it will be a "\n" so every line going to have 3 values only
             *
             *  it sorted like this: [k1, k2, k3] , [k4, k5, k6], [k7,k8] it will keep sorting as that till the whole map sort
             *
             *  NOTE: map.chunked() is only available to kotlin!
             *  in Java it might be a little different you have to make a simple math.
             *  Using AtomicInteger is the easiest way I find out!
             *
             *  @code
             *  AtomicInteger counter = new AtomicInteger(0);
             *
             *  map.keySet().stream().map(map -> ((counter.getAndAdd(1) % 3 == 0)? "\n":"/") + map).collect(collectors.joining(""));
             */
            description.append(commands.keys.chunked(3).stream().map { it.joinToString("/") { map -> "**${map}**" } }.collect(Collectors.joining("\n")))
            embed.setFooter("· Usage; r?help <command>")
        } else {
            if (commands.containsKey(args[0])) {
                val info: Command? = commands[args[0]]
                val description: StringBuilder = embed.descriptionBuilder

                embed.setTitle("${info!!.command} :")
                description.append("**Explaining for the command:**").append("\n")
                description.append("> **Description:** ${info.description}").append("\n")
                description.append("> **How to use:** ${info.help}").append("\n")
                description.append("> **Status:**").append(" ")

                if (info.isDisplay) {
                    description.append("Everyone can you this command")
                    embed.setColor(Color(0, 181, 13))
                } else {
                    description.append("Staff only able to use this command")
                    embed.setColor(Color(255, 0, 0))
                }

                embed.setFooter("· Usage; r?help <command>")
            } else {
                embed.setTitle("${args[0]}: ")
                embed.setDescription("> This command is not exist make sure to use right now `r?help`!")
                embed.setColor(Color(255, 0, 0))
            }

        }
        event.channel.sendMessageEmbeds(embed.build()).queue()
    }


    override val help: String
        get() = "$command <command>"
    override val command: String
        get() = "help"
    override val description: String
        get() = "i will assistant you with everything you need"
    override val isDisplay: Boolean
        get() = true
}