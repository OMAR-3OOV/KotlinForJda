package listeners

import CommandManager
import Main
import gameUtilities.RPSUtility
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import utilities.messengerUtility.MessengerManager
import java.io.FileNotFoundException

class Events(bot: Main.Companion) : ListenerAdapter() {

    private val commandManager = CommandManager(bot)

    override fun onReady(event: ReadyEvent) {
        event.jda.presence.setPresence(OnlineStatus.ONLINE, Activity.playing("This bot is working on kotlin language!"))

        Main.Logger()
            .info("Hey master, Im totally ready & im currently in ${event.guildTotalCount} Guilds right now :3!")
    }

    override fun onGuildReady(event: GuildReadyEvent) {

    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        if (event.name == "Messenger") {
            val user = event.target
            val channel = event.textChannel
            if (event.user == user) {
                event.interaction.reply("${event.user.asMention} Are you that loneliness to messenger yourself :p? i can call my developer to talk with you if you are!")
                    .setEphemeral(true).queue()
            } else if (!(user.isBot || user.isSystem)) {

                val messenger = MessengerManager(user, channel)

                if (MessengerManager.dm.containsKey(user) && MessengerManager.messenger.containsKey(user)) {
                    channel.sendMessage(":x: | You're already with ${MessengerManager.dm[user]!!.getter.name} in messenger!")
                        .queue()
                }

                messenger.setSender(event.user)
                messenger.messengerStart()
                event.interaction.reply("${user.name} has been found!").setEphemeral(true).queue()
            } else {
                event.interaction.reply("${event.user.asMention} YOU CAN OPEN MESSENGER WITH OTHER BOTS >:c!")
                    .setEphemeral(true).queue()
            }
        } else if (event.name == "Rock Paper Scissors") {
            val user = event.user
            val target = event.target
            val channel = event.textChannel
            if (event.user == user) {
                event.interaction.reply("${event.user.asMention} Are you that loneliness to messenger yourself :p? i can call my developer to talk with you if you are!")
                    .setEphemeral(true).queue()
            } else if (!(user.isBot || user.isSystem)) {
                val embed = EmbedBuilder()
                val gui: StringBuilder = StringBuilder()
                gui.append("------------------").append("\n")
                gui.append("|................|").append("\n")
                gui.append("|....P1....P2....|").append("\n")
                gui.append("|................|").append("\n")
                gui.append("------------------").append("\n")

                val rps = RPSUtility(user, target, event.guild, event.textChannel, embed)
            } else {
                event.interaction.reply("${event.user.asMention} YOU CAN OPEN MESSENGER WITH OTHER BOTS >:c!")
                    .setEphemeral(true).queue()
            }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channelType.isGuild) {
            val prefix = "r?"

            if (event.message.contentRaw.lowercase().contains(prefix)) {

                try {
                    commandManager.handleCommand(event, prefix)
                } catch (error: FileNotFoundException) {
                    error.printStackTrace()
                }
            }
        }

    }

}