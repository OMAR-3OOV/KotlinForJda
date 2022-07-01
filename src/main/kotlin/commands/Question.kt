package commands

import Command
import commands.adminCategory.RolesManager
import commands.adminCategory.Shutdown
import commands.funCategory.PrivateMessenger
import commands.gamesCategory.RPC
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtility.Categories
import utilities.staffUtility.Roles
import java.util.function.Function
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class Question: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
    }

    override fun onSlashCommand(event: SlashCommandInteractionEvent
    ) {
        TODO("Not yet implemented")
    }

    override val help: String
        get() = "r?${command}"
    override val command: String
        get() = "areyougay"
    override val category: Categories
        get() = Categories.FUN
    override val roles: List<Roles>
        get() = Roles.values().toList()
    override val description: String
        get() = "ask me if im gay"
    override val isDisplay: Boolean
        get() = true
}
