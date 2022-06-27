package utilities.messengerUtility

import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.text.SimpleDateFormat
import java.util.Date
import java.util.stream.Collectors

data class MessengerManager(val getter: User, val channel: TextChannel) {

    // Sender should be existed, but it used null to be as default only in any case that the sender didn't set!
    var sender: User? = null
    var message: Message? = null
    val jda: JDA = channel.jda

    var guild: Guild = channel.guild
    var started: Boolean = false
    var pause: Boolean = false

    companion object {
        /**
         * @param User related to the [getter]
         * @param MessengerManager related to this class.
         *
         * This function is usable when the [getter] do any action
         */
        val messenger: HashMap<User, MessengerManager> = HashMap()

        /**
         * @param User related to the [sender]
         * @param MessengerManager related to this class.
         *
         * This function is usable when the [sender] do any action
         */
        val dm: HashMap<User, MessengerManager> = HashMap()


        /**
         * The last sender that [getter] send it and the textchannel receive it
         */
        val lastMessageGetter: HashMap<User, Message> = HashMap()

        /**
         * The last sender that [sender] send it and the [getter] receive it in DM
         */
        val lastMessageSender: HashMap<User, Message> = HashMap()

        /**
         * This hashmap will sort all [getter] messages that sent to bot, so it will be useful when [getter] delete a message
         *
         * @param Long related to message id
         * @param String related to message content
         */
        val messageCache: HashMap<Long, String> = HashMap()

        /**
         * This hashmap will sort [getter] when send a message, it can be useful in [MessageDeleteEvent][net.dv8tion.jda.api.events.message.MessageDeleteEvent]
         *
         * @param Long related to message id
         * @param User related to [getter]
         */
        val userCache: HashMap<Long, User> = HashMap()
    }

    init {}

    fun messengerStart() {
        try {
            if (this.sender == null) return this.channel.sendMessage(":x: | Something won't wrong, i can't decide who send the message!")
                .queue() else

                this.started = true
            this.getter.openPrivateChannel().queue { private ->
                // Default message
                private.sendMessage("Hi ${getter.name}! 😀").queue()
                messenger[this.getter] = this
                dm[this.sender!!] = this
            }

            val bts: ArrayList<Button> = ArrayList()
//            bts.add(jda.button(label = "End messenger", style = ButtonStyle.DANGER, user = sender) {
//                this.message!!.editMessageEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Ends*)"))
//                    .queue()
//                messengerEnds()
//            })
//
//            bts.add(jda.button(label = "Pause", style = ButtonStyle.SECONDARY, user = sender) {
//                val resume = jda.button(label = "Resume", style = ButtonStyle.SUCCESS, user = sender) { resume() }
//                this.message!!.editMessageEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Paused*)"))
//                    .setActionRow(resume).queue()
//                pause()
//            })

            setMessage(this.channel.sendMessageEmbeds(defaultEmbed()).complete())
        } catch (error: Exception) {
            this.channel.sendMessage(":x: | I  can't DM this user! `Error: ${error.message}`").queue()
            error.printStackTrace()
        }
    }

    /**
     * This method to send the messenger between the users
     */
    fun messengerEnds() {
        this.started = false
    }

    /**
     * This method to set [sender] the one who will send the messages to the channel and the bot going to send it to direct message to [getter]
     */
    @JvmName("setSenderMessenger")
    fun setSender(user: User) {
        this.sender = user
    }

    /**
     * This method to set the Manage message the going to manage the messenger between [sender] and [getter]
     *
     * @param message related to the message going to send after the command used
     */
    @JvmName("setMessageMessenger")
    fun setMessage(message: Message) {
        this.message = message
    }

    fun addMessageCache(message: Message) {
        messageCache[message.idLong] = message.contentRaw
    }

    fun addUserCache(message: Message) {
        println("${message.idLong} Added!")
        userCache[message.idLong] = this.sender!!
    }

    /**
     * This method to send message to [sender] to the [channel], it will set as last message automatically [setGetterLastMessage].
     *
     * @param message related to the message the [getter] send to bot.
     * @exception Exception if the message failed to reach to channel.
     */
    fun sendMessageToChannel(message: Message) {
        try {
            val msg = this.channel.sendMessage("**${getter.name}:** ${message.contentRaw}").complete()
            setGetterLastMessage(msg)
            addMessageCache(message)
        } catch (err: Exception) {
            message.reply(":x: | Failed to send! Error: ${err.cause.toString()}").queue()
        }
    }

    /**
     * This method to send message to [getter] DM, it will set as last message automatically [setSenderLastMessage].
     *
     * @param message related to the message the [sender] send in [channel]
     * @param actionRow related to the message filter, if there is any button filer it will display a button on the message that send to [getter]
     * @exception Exception if the message failed to reach to DM.
     */
    fun sendMessageToDm(message: Message, actionRow: ActionRow? = null) {
        try {
            this.getter.openPrivateChannel().queue { dm ->
                val msg: Message?

                if (actionRow != null) {
                    msg = dm.sendMessage(message.contentRaw).setActionRows(actionRow).complete()
                } else {
                    msg = dm.sendMessage(message.contentRaw).complete()
                }

                setSenderLastMessage(msg)
                addMessageCache(message)
                addUserCache(message)
            }
        } catch (err: Exception) {
            message.reply(":x: | Failed to send! Error: ${err.cause.toString()}").queue()
        }
    }

    /**
     * This method is to edit the last message that getter sent to bot, if the message edited it going to edit the message cache as well.
     *
     * @param lastMessageGetter related to getter last massage
     */
    fun editGetterLastMessage(message: Message) {
        val content = message.contentRaw
        messageCache[message.idLong] = content
        lastMessageGetter[this.getter]!!.editMessage("**${getter.name}:** ${content} (*Edited*)").queue()
    }

    /**
     * This method to set the last message for [getter]
     *
     * @param lastMessageGetter related to getter last massage
     */
    fun setGetterLastMessage(message: Message) {
        lastMessageGetter[this.getter] = message
    }

    /**
     * This method is to edit the last message that sender sent to bot
     *
     * @param lastMessageSender related to sender last message
     */
    fun editSenderLastMessage(message: Message) {
        val content = message.contentRaw
        lastMessageSender[this.sender]!!.editMessage(content).complete()
    }

    /**
     * This method to set the last message for [sender]
     *
     * @param lastMessageSender related to getter last massage
     */
    fun setSenderLastMessage(message: Message) {
        lastMessageSender[this.sender!!] = message
    }

    /**
     * This method to delete the last message that [getter] sent to bot
     */
    fun deleteGetterLastMessage(content: String) {
        // This will check if the message can be deleted to it won't throw any errors!
        if (lastMessageGetter[this.getter]!!.type.canDelete()) {
            lastMessageGetter[this.getter]!!.editMessage("**${getter.name}:** $content (*Deleted*)").queue()
        }
    }

    /**
     * This method to delete the last message that [sender] sent to [getter] in DM
     */
    fun deleteSenderLastMessage() {
        // This will check if the message can be deleted to it won't throw any errors!
        if (lastMessageSender[this.sender]!!.type.canDelete()) {
            lastMessageSender[this.sender]!!.delete().queue()
        }
    }

    /**
     * To pause the messages that [sender] send it to [channel], which means if if this option is on it won't let the [getter] receive any message till it get [resume]
     */
    fun pause() {
        this.pause = true
    }

    /**
     * To resume the messages that [sender] will send, which mean every message send to channel from the [sender], the [getter] will get them
     */
    fun resume() {
        this.pause = false
    }

    /**
     * The default embed for [message] to control the messenger
     */
    fun defaultEmbed(t: String = "messenger between ${sender!!.name} & ${getter.name}"): MessageEmbed {
        val desc = ArrayList<String>()

        desc.add("**> How to use?**")
        desc.add(" - To end the messenger you have to use *End Messenger* Button!")
        desc.add(" - To pause the messenger you have to use *Pause* Button! ( it will turn to resume )")
        desc.add(" - You can use some message filters, **Filters:** `<activity> / <avatar> / <timecreated>`")

        return Embed {
            title = t
            description = desc.stream().collect(Collectors.joining("\n"))
            footer {
                name = "Created: ${SimpleDateFormat("dd/MM/yyyy").format(Date())}"
                iconUrl = sender!!.avatarUrl
            }
        }
    }
}
