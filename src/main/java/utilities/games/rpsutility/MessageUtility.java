package utilities.games.rpsutility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This message data class related to {@link RPSUtility} to manage the messages game process.
 */
public class MessageUtility {
    private Message message;
    private final MessageChannel channel;
    private final MessageCreateBuilder messageCreateBuilder;
    private MessageCreateData messageCreateData;

    public MessageUtility(MessageChannel channel) {
        this.channel = channel;
        this.messageCreateBuilder = new MessageCreateBuilder();
    }

    public Message getMessage() {
        return message;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public MessageCreateBuilder getMessageCreateBuilder() {
        return messageCreateBuilder;
    }
    public MessageCreateData getMessageCreateData() {
        return messageCreateData;
    }

    public MessageCreateData getMessageData() {
        return this.messageCreateBuilder.build();
    }

    public void setMessage(Message message) {
        this.message = message;
    }


    public MessageUtility create(Consumer<MessageCreateBuilder> consumer) {
        consumer.accept(this.messageCreateBuilder);
        return this;
    }

    public MessageUtility addButton(Button btn) {
        this.messageCreateBuilder.setActionRow(btn);
        messageCreateData = messageCreateBuilder.build();
        return this;
    }

    public MessageUtility addButtons(Button... btn) {
        this.messageCreateBuilder.setActionRow(btn);
        messageCreateData = messageCreateBuilder.build();
        return this;
    }

    public MessageUtility addButtons(List<Button> btn) {
        this.messageCreateBuilder.setActionRow(btn);
        messageCreateData = messageCreateBuilder.build();
        return this;
    }

    public MessageCreateData buildCreateData() {
        return messageCreateData = messageCreateBuilder.build();
    }

    public MessageEditData buildEditData() {
        messageCreateData = messageCreateBuilder.build();
        return MessageEditData.fromCreateData(messageCreateData);
    }
}
