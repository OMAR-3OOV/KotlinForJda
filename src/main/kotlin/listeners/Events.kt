package listeners

import CommandManager
import Main
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.io.FileNotFoundException

class Events(val bot: Main): ListenerAdapter() {

    override fun onReady(event: ReadyEvent) {
        event.jda.presence.setPresence(OnlineStatus.ONLINE, Activity.playing("Im bitch for my master"))
        bot.Logger().info("Hey master, Im totally ready & im currently in ${event.guildTotalCount} Guilds right now :3!")
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (event.channelType.isGuild) {
            event.retrieveMessage().queue({ q -> q.addReaction(event.reactionEmote.emoji).queue() })
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channelType.isGuild) {
            val prefix = "r?";

            if (event.message.contentRaw.contains(prefix)) {
                val commandManager = CommandManager(bot)

                try {
                    commandManager.handleCommand(event, prefix)
                } catch (error: FileNotFoundException) {
                    error.printStackTrace()
                }
            }
        }

    }

}