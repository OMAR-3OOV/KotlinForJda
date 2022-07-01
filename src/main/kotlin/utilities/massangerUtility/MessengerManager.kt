package utilities.messengerUtility

import dev.minn.jda.ktx.events.onButton
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.exceptions.ContextException
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

data class MessengerManager(val getter: User, val channel: TextChannel) {

    // Sender should be existed, but it used null to be as default only in any case that the sender didn't set!
    var sender: User? = null
    var message: Message? = null
    val jda: JDA = channel.jda

    var pause: Boolean = false
    var isMessage: Boolean = false

    /**
     * It describe if the messenger is still ON or OFF, the default result is false, it will to be true when the [MessengerManager.messengerStart] used.
     */
    var started: Boolean = false

    companion object {

        /**
         * @param User related to the [getter].
         * @param MessengerManager related to this class.
         *
         * This function is usable when the [getter] do any action.
         */
        val messenger: HashMap<User, MessengerManager> = HashMap()

        /**
         * @param User related to the [sender].
         * @param MessengerManager related to this class.
         *
         * This function is usable when the [sender] do any action.
         */
        val dm: HashMap<User, MessengerManager> = HashMap()


        /**
         * The last sender that [getter] send it and the textchannel receive it.
         */
        val lastMessageGetter: HashMap<User, Message> = HashMap()

        /**
         * The last sender that [sender] send it and the [getter] receive it in DM.
         */
        val lastMessageSender: HashMap<User, Message> = HashMap()

        /**
         * This hashmap will sort all [getter] messages that sent to bot, so it will be useful when [getter] delete a message.
         *
         * @param Long related to message id.
         * @param String related to message content.
         */
        val messageCache: HashMap<Long, String> = HashMap()

        /**
         * This hashmap will sort [getter] when send a message, it can be useful in [MessageDeleteEvent][net.dv8tion.jda.api.events.message.MessageDeleteEvent].
         *
         * @param Long related to message id.
         * @param User related to [getter].
         */
        val userCache: HashMap<Long, User> = HashMap()

        /**
         * This hashmap will store the control panel message, it can be useful when the message get deleted to it can be turn back automatically.
         *
         * @param Long RELATED TO control panel message id.
         * @param MessengerManager related to the data class that contained with the control panel.
         */
        val managerMessage: HashMap<Long, MessengerManager> = HashMap()

        /**
         * This hashmap to store the thread channel from the message that have been created the thread, so this can help to get the information
         *
         * @param Message related to the [message]
         * @param ThreadChannel related to the [createThreadMessages]
         */
        val threadMessages: HashMap<Long, ThreadChannel> = HashMap()

        /**
         * This hashmap to store the message that created the thread, so if the thread deleted it will automatically create new thread
         *
         * @param ThreadChannel related to the thread that [message] created it from [createThreadMessages]
         * @param Message related to [message]
         */
        val threadManager: HashMap<ThreadChannel, MessengerManager> = HashMap()
    }

    init {

    }

    /**
     * To start the messenger between [sender] & [getter]
     *
     * @exception Exception when [messengerStart] failed to start.
     */
    fun messengerStart() {
        try {
            if (this.sender == null) return this.channel.sendMessage(":x: | Something won't wrong, i can't decide who send the message!")
                .queue()
            else {
                this.started = true
                this.getter.openPrivateChannel().queue { private ->
                    // Default message
                    private.sendMessage("Hi ${getter.name}! 😀").queue()
                    messenger[this.getter] = this
                    dm[this.sender!!] = this
                }

                setMessage(controlPanel().complete())
                managerMessage[message!!.idLong] = this
                createThreadMessages(message!!)!!.queue { thread ->
                    setThread(thread)
                    thread.sendMessage("${this.sender!!.asMention} Now you can start chatting with ${this.getter.name} here")
                        .queueAfter(2, TimeUnit.SECONDS)
                }

            }
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
        message!!.editMessageEmbeds(
            defaultEmbed(
                t = "messenger between ${sender!!.name} & ${getter.name} (*Ends*)",
                c = Color.RED
            )
        ).queue()
        getThread().delete().queue()
        threadMessages.remove(this.message!!.idLong)
        messenger.remove(this.getter)
        dm.remove(this.sender)
        lastMessageSender.clear()
        lastMessageGetter.clear()
        messageCache.clear()
        userCache.clear()
        managerMessage.clear()
        return
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
        managerMessage[message.idLong] = this
    }

    /**
     * This method to get the thread from [threadMessages]
     */
    fun getThread(): ThreadChannel {
        return threadMessages[this.message!!.idLong]!!
    }

    /**
     * This method to set new thread, most use when the old thread get deleted
     */
    fun setThread(thread: ThreadChannel) {
        threadMessages[this.message!!.idLong] = thread
        threadManager[thread] = this
    }

    /**
     * This method will add the message to [messageCache] Hashmap.
     */
    fun addMessageCache(message: Message) {
        messageCache[message.idLong] = message.contentRaw
    }

    /**
     * This method will add the user to [userCache] to be used to support [messageCache].
     */
    fun addUserCache(message: Message) {
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
            val msg = getThread()
                .sendMessage("**${getter.name}:** ${message.contentRaw}")
                .complete()
            setGetterLastMessage(msg)
            addMessageCache(message)
        } catch (err: Exception) {
            message.reply(":x: | Failed to send! Error: ${err.message}").queue()
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
            message.reply(":x: | Failed to send! Error: ${err.message}").queue()
        }
    }

    /**
     * This method is to edit the last message that getter sent to bot, if the message edited it going to edit the message cache as well.
     *
     * @param lastMessageGetter related to getter last massage
     */
    fun editGetterLastMessage(message: Message) {
        try {
            val content = message.contentRaw
            messageCache[message.idLong] = content
            lastMessageGetter[this.getter]!!.editMessage("**${getter.name}:** ${content} (*Edited*)").queue()
        } catch (err: ContextException) {
            message.channel.sendMessage(":x: | ${getter.name} has edit a message and it failed to edit! Error: ${err.message}")
                .queue()
        } catch (err: Exception) {
            message.channel.sendMessage(":x: | ${getter.name} has edit a message and it failed to edit! Error: ${err.message}")
                .queue()
        }
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
        try {
            val content = message.contentRaw
            lastMessageSender[this.sender]!!.editMessage(content).complete()
        } catch (err: Exception) {
            message.reply(":x: | Failed to edit! Error: ${err.message}").queue()
        }
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
    @Throws(ContextException::class)
    fun deleteGetterLastMessage(content: String) {
        try {
            // This will check if the message can be deleted to it won't throw any errors!
            lastMessageGetter[this.getter]!!
                .editMessage("**${getter.name}:** $content (*Deleted*)")
                .setCheck { lastMessageGetter[this.getter] != null }
                .queue()
        } catch (err: Exception) {
            message!!.reply(":x: | ${getter.name} has been delete a message and failed to announce you! Error: ${err.message}")
                .queue()
        } catch (err: ContextException) {
            message!!.channel.sendMessage(":x: | ${getter.name} has been delete a message and failed to announce you! Error: ${err.message}")
                .queue()
        }
    }

    /**
     * This method to delete the last message that [sender] sent to [getter] in DM
     */
    fun deleteSenderLastMessage() {
        try {
            // This will check if the message can be deleted to it won't throw any errors!
            if (lastMessageSender[this.sender]!!.type.canDelete()) {
                lastMessageSender[this.sender]!!.delete().queue()
            }
        } catch (err: Exception) {
            message!!.reply(":x: | Failed to delete! Error: ${err.message}").queue()
        }
    }

    /**
     * To pause the messages that [sender] send it to [channel], which means if if this option is on it won't let the [getter] receive any message till it get [resume]
     */
    private fun pause() {
        this.pause = true
    }

    /**
     * To resume the messages that [sender] will send, which mean every message send to channel from the [sender], the [getter] will get them.
     */
    private fun resume() {
        this.pause = false
    }

    /**
     * This method to check if the control panel message get deleted or not,
     * most use to manage the events because when the message delete the thread keeps,
     * so I have to delete the thread as well, after delete the thread the event for deleting it will be requested so that make few errors,
     * so I have to make sure that the message is exists
     */
    fun isMessage(boolean: Boolean = true) {
        this.isMessage = boolean
    }

    /**
     * The default embed for [message] to control the messenger
     */
    private fun defaultEmbed(
        t: String = "messenger between ${sender!!.name} & ${getter.name}",
        c: Color = Color(0x2F3136)
    ): MessageEmbed {
        val desc = ArrayList<String>()

        desc.add("**> How to use?**")
        desc.add(" - To end the messenger you have to use *End Messenger* Button!")
        desc.add(" - To pause the messenger you have to use *Pause* Button! ( it will turn to resume )")
        desc.add(" - You can use some message filters, **Filters:** `<activity> / <avatar> / <timecreated>`")

        return Embed {
            title = t
            description = desc.stream().collect(Collectors.joining("\n"))
            color = c.rgb
            footer {
                name = "Created: ${SimpleDateFormat("dd/MM/yyyy").format(Date())}"
                iconUrl = sender!!.avatarUrl
            }
        }
    }

    /**
     * This method is the control panel to the messenger that [sender] can use it.
     * @param Message related to [message].
     * @param onButton related to [ButtonInteractionEvent] using additional jda kotlin supported
     */
    fun controlPanel(): MessageAction {
        val bts: ArrayList<Button> = ArrayList()

        bts.add(Button.danger("${this.sender!!.id}-end", "End messenger"))
        bts.add(Button.secondary("${this.sender!!.id}-pause", "Pause"))
        val resumebtn = Button.success("${this.sender!!.id}-resume", "Resume")

        jda.onButton("${this.sender!!.id}-end") {
            if (it.user != sender) return@onButton
            if (started) {
                if (it.isAcknowledged) {
                    it.hook.editOriginalEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Ends*)"))
                        .queue()
                    messengerEnds()
                    return@onButton
                }

                it.interaction.deferEdit()
                    .setEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Ends*)"))
                    .queue()
                messengerEnds()
            }
        }

        jda.onButton("${this.sender!!.id}-pause") {
            if (it.user != sender) return@onButton
            if (started) {
                if (it.isAcknowledged) {
                    it.hook.editOriginalEmbeds(
                        defaultEmbed(
                            t = "messenger between ${sender!!.name} & ${getter.name} (*Paused*)",
                            c = Color(0xCC7900)
                        )
                    )
                        .setActionRow(resumebtn).queue()
                    pause()
                    return@onButton
                }

                it.interaction.deferEdit()
                    .setEmbeds(
                        defaultEmbed(
                            t = "messenger between ${sender!!.name} & ${getter.name} (*Paused*)",
                            c = Color(0xCC7900)
                        )
                    )
                    .setActionRow(resumebtn).queue()
                pause()
            }
        }

        jda.onButton("${this.sender!!.id}-resume") {
            if (it.user != sender) return@onButton
            if (started) {
                if (it.isAcknowledged) {
                    it.hook.editOriginalEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Resumed*)"))
                        .setActionRow(bts).queue()
                    resume()
                    return@onButton
                }

                it.interaction.deferEdit()
                    .setEmbeds(defaultEmbed(t = "messenger between ${sender!!.name} & ${getter.name} (*Resumed*)"))
                    .setActionRow(bts).queue()
                resume()
            }
        }

        if (this.pause) {
            return this.channel.sendMessageEmbeds(
                defaultEmbed(
                    t = "messenger between ${sender!!.name} & ${getter.name} (*Paused*)",
                    c = Color(0xCC7900)
                )
            ).setActionRow(resumebtn)
        } else {
            return this.channel.sendMessageEmbeds(defaultEmbed()).setActionRow(bts)
        }
    }

    /**
     * This method used to create new thread channel to [Control panel message][MessengerManager.message]
     */
    fun createThreadMessages(message: Message): RestAction<ThreadChannel>? {
        val newThread = this.channel.createThreadChannel("${getter.name} Messenger", message.id)
        return newThread
    }
}