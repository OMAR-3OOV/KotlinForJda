package commands.adminCategory

import Command
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import utilities.categoryUtility.Categories
import utilities.staffUtility.Roles

class Shutdown: Command, ListenerAdapter() {

    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        if (args.isEmpty()){
            event.jda.shutdown()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name.equals("shutdown")) {
            event.reply("Bot is shutdown").setEphemeral(true).queue()
            event.jda.shutdown()
        }
    }

    override val help: String
        get() = "r?shutdown"
    override val command: String
        get() = "shutdown"
    override val category: Categories
        get() = Categories.MANAGEMENT
    override val roles: List<Roles>
        get() = arrayListOf(Roles.ADMIN)
    override val description: String
        get() = "this command to shutdown the bot!"
    override val isDisplay: Boolean
        get() = false
}