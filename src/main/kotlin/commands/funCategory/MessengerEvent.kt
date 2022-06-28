package commands.funCategory

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.entities.ThreadChannel
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.events.thread.GenericThreadEvent
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import utilities.messengerUtility.MessengerManager
import java.util.concurrent.TimeUnit

class MessengerEvent: ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.author.isSystem) return

        if (event.isFromType(ChannelType.PRIVATE)) {
            if (!MessengerManager.messenger.containsKey(event.author)) return
            if (!MessengerManager.messenger[event.author]!!.started) return
            if (event.message.contentRaw.contains("r?")) return event.message.addReaction("❌").queue()

            val messenger: MessengerManager = MessengerManager.messenger[event.author]!!
            val message = event.message

            messenger.sendMessageToChannel(message)
        } else if (event.isFromGuild && event.isFromThread) {
            if (!MessengerManager.dm.containsKey(event.author)) return
            val managerMessage = MessengerManager.dm[event.author]!!.message
            if (MessengerManager.threadMessages[managerMessage!!.idLong]!! != event.threadChannel) return println("WRONG!")
            if (!MessengerManager.dm[event.author]!!.started) return
            if (event.message.contentRaw.contains("r?")) return event.message.addReaction("❌").queue()

            val messenger: MessengerManager = MessengerManager.dm[event.author]!!
            val message = event.message

            if (messenger.pause) return

            messenger.sendMessageToDm(message)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            val user = event.privateChannel.user
            if (!MessengerManager.messenger.containsKey(user)) return
            if (MessengerManager.messageCache.containsKey(event.messageIdLong)) {
                val content: String = MessengerManager.messageCache[event.messageIdLong].toString()
                val messenger = MessengerManager.messenger[user]!!

                messenger.deleteGetterLastMessage(content)
            }

        } else if (event.isFromGuild && event.isFromThread) {
            // If the deleted message is contains with managerMessage key so this will activate it
            val user = MessengerManager.userCache[event.messageIdLong]

            if (!MessengerManager.dm.containsKey(user)) return
            if (MessengerManager.messageCache.containsKey(event.messageIdLong)) {
                val messenger = MessengerManager.dm[user]!!

                if (messenger.pause) return

                messenger.deleteSenderLastMessage()
            }
        } else if (event.isFromGuild) {
            if (MessengerManager.managerMessage.containsKey(event.messageIdLong)) {
                val mm = MessengerManager.managerMessage[event.messageIdLong]
                mm?.isMessage(true)
                mm?.getThread()!!.delete().queue()

                val msg = mm.controlPanel().complete()

                mm.createthreadMessages(msg)!!.queue {thread ->
                    thread.sendMessage("${mm.sender!!.asMention} Thread has been resumed you can start chatting with ${mm.getter.name} again")
                        .queue()
                    mm.setThread(thread)
                    thread.manager.setArchived(true).setLocked(true).queue()
                    mm.isMessage(false)
                }

                mm.setMessage(msg)
            }
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (!MessengerManager.messenger.containsKey(event.author)) return
            if (!MessengerManager.messenger[event.author]!!.started) return

            val messenger = MessengerManager.messenger[event.author]!!
            val message = event.message

            messenger.editGetterLastMessage(message)
        } else if (event.isFromGuild && event.isFromThread) {
            if (!MessengerManager.dm.containsKey(event.author)) return
            val managerMessage = MessengerManager.dm[event.author]!!.message
            if (MessengerManager.threadMessages[managerMessage!!.idLong]!! != event.threadChannel) return println("WRONG!")
            if (!MessengerManager.dm[event.author]!!.started) return

            val messenger = MessengerManager.dm[event.author]!!
            val message = event.message

            if (messenger.pause) return

            messenger.editSenderLastMessage(message)
        }
    }

    override fun onChannelDelete(event: ChannelDeleteEvent) {
        if (event.isFromType(ChannelType.GUILD_PUBLIC_THREAD)) {
            if (MessengerManager.threadManager.containsKey(event.channel)) {
                val mm = MessengerManager.threadManager[event.channel]!!
                if (mm.isMessage) return
                if (mm.started) {
                    val thread = mm.message!!.createThreadChannel("${mm.getter.name} Messenger").complete()

                    thread.sendMessage("${mm.sender!!.asMention} Thread has been resumed you can start chatting with ${mm.getter.name} again")
                        .queue()
                    thread.manager.setArchived(true).setLocked(true).queue()
                    mm.setThread(thread)
                }
            }
        }
    }
}