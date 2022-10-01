package utilities.games.rpsutility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This message data class related to {@link RPSUtility} to manage the messages game process.
 */
public class MessageUtility {
    private Message message;
    private final MessageChannel channel;
    private MessageCreateBuilder messageCreateBuilder;
    private MessageCreateData messageCreateData;
    private MessageEditBuilder messageEditBuilder;
    private MessageEditData messageEditData;

    private final List<Button> buttons;

    public MessageUtility(MessageChannel channel) {
        this.channel = channel;
        this.buttons = new ArrayList<>();
        this.messageCreateBuilder = new MessageCreateBuilder();
        this.messageEditBuilder = new MessageEditBuilder();
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

    public MessageEditBuilder getMessageEditBuilder() {
        return messageEditBuilder;
    }

    public MessageCreateData getMessageCreateData() {
        return messageCreateData;
    }

    public MessageEditData getMessageEditData() {
        return messageEditData;
    }

    public MessageCreateData getMessageData() {
        return this.messageCreateBuilder.build();
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setMessageCreateData(MessageCreateData messageCreateData) {
        this.messageCreateData = messageCreateData;
    }

    public void setMessageEditData(MessageEditData messageEditData) {
        this.messageEditData = messageEditData;
    }

    public void addButton(Button btn) {
        this.buttons.add(btn);
    }

    public void addButton(List<Button> btn) {
        this.buttons.addAll(btn);
    }

    public void addButton(Button... btns) {
        this.buttons.addAll(List.of(btns));
    }

    public MessageCreateData buildMessage(Consumer<MessageCreateBuilder> consumer) {
        if (!buttons.isEmpty()) {
            this.messageCreateBuilder.setActionRow(buttons);
            this.buttons.clear();
        }

        consumer.accept(this.messageCreateBuilder);
        setMessageCreateData(messageCreateBuilder.build());

        return messageCreateData;
    }

    public MessageEditData editMessage(Consumer<MessageEditBuilder> consumer) {
        if (!buttons.isEmpty()) {
            this.messageEditBuilder.setActionRow(buttons);
            this.buttons.clear();
        }

        consumer.accept(this.messageEditBuilder);
        setMessageEditData(this.messageEditBuilder.build());

        return messageEditData;
    }
}
