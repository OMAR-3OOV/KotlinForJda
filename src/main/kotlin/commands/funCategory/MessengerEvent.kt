package commands.funCategory

import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import utilities.messengerUtility.MessengerManager

class MessengerEvent: ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.author.isSystem) return
        if (event.message.contentRaw.contains("r?")) return event.message.addReaction("❌").queue()

        if (event.isFromType(ChannelType.PRIVATE)) {
            if (!MessengerManager.messenger.containsKey(event.author)) return

            val messenger: MessengerManager = MessengerManager.messenger[event.author]!!
            val message = event.message

            messenger.sendMessageToChannel(message)
        } else if (event.isFromGuild) {
            if (!MessengerManager.dm.containsKey(event.author) || MessengerManager.dm[event.author]!!.channel != event.textChannel) return

            val messenger: MessengerManager = MessengerManager.dm[event.author]!!
            val message = event.message

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

        } else if (event.isFromGuild) {
            val user = MessengerManager.userCache[event.messageIdLong]
            if (!MessengerManager.dm.containsKey(user)) return
            if (MessengerManager.messageCache.containsKey(event.messageIdLong)) {
                val content = MessengerManager.messageCache[event.messageIdLong].toString()
                val messenger = MessengerManager.dm[user]!!

                messenger.deleteSenderLastMessage()
            }
        }
    }

    override fun onMessageUpdate(event: MessageUpdateEvent) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (!MessengerManager.messenger.containsKey(event.author)) return

            val messenger = MessengerManager.messenger[event.author]!!
            val message = event.message

            messenger.editGetterLastMessage(message)
        } else if (event.isFromGuild) {
            if (!MessengerManager.dm.containsKey(event.author) || MessengerManager.dm[event.author]!!.channel != event.textChannel) return

            val messenger = MessengerManager.dm[event.author]!!
            val message = event.message

            messenger.editSenderLastMessage(message)
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {

    }

}