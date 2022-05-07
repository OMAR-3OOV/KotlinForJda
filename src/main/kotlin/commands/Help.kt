package commands

import Command
import CommandManager
import Main
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

class Help(private val bot: Main.Companion) : Command {

    override fun handle(args: List<String>, event: MessageReceivedEvent) {

        val embed = EmbedBuilder()
        val commandManager = CommandManager(bot)

        val commands: MutableMap<String, Command> = commandManager.commands

        if (args.isEmpty()) {
            val description: StringBuilder = embed.descriptionBuilder

            embed.setTitle("⚙ Help assistant!")

            val counter: AtomicInteger = AtomicInteger(0)

            description.append(commands.keys.stream().map { map -> (if (counter.getAndAdd(1) % 3 == 0) "\n" else " / ") + "**${map}**" }.collect(Collectors.joining("")))
            embed.setFooter("· Usage; r?help <command>")
        } else if (args.isNotEmpty()) {
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