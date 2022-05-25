package gameUtilities;

import commands.gamesCategory.RPSTypes;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * All things we need to create new RPS Game: Users ID ( sender, component ) / Guild / Channel / Message ( Message, Embed )
 * <p>
 * if user want to play with bot the component value should be null.
 */

public class RPSUtility {

    /* System requirement to game data */
    private User sender;
    @Nullable
    private User component;
    private Guild guild;
    private TextChannel channel;
    @NotNull
    private Message message;

    /* Game system */

    private final MessageEmbed embed = (MessageEmbed) message.getEmbeds();
    private int gameId;
    private RPSTypes senderMove;
    private RPSTypes componentMove;

    private int rounds = 1; // Default
    private boolean unlimitedLoop = false; // Default

    private final List<RPSUtility> games = new ArrayList<>();
    private HashMap<User, RPSUtility> game = new HashMap<>();
    private final HashMap<Message, User> pending = new HashMap<>();

    /* Class methods */

    public RPSUtility() {
    }

    public RPSUtility(User sender, @Nullable User component, Guild guild, TextChannel channel, @NotNull Message message) {
        this.sender = sender;

        if (component != null) {
            this.component = component;
        }

        this.guild = guild;
        this.channel = channel;
        this.message = message;

        // Game tasks

        if (component != null) {

            if (!component.isBot() || !component.isSystem()) {
                channel.sendMessage(":x: | **YOU CAN'T PLAY WITH OTHER BOTS >:c**").queue();
                return;
            }

            if (game.containsKey(this.sender)) {
                channel.sendMessage(":x: | `Sorry " + this.sender.getName() + " but it seems you're already in game with " + game.get(this.sender).getComponent().getName() + " in " + game.get(this.sender).getGuild().getName() + "` **NOTE: You can use `r?rps leave` to leave the game that you have been created before with your component**").queue();
                return;
            }

            if (isComponentInGame()) {
                channel.sendMessage(":x: | `It seems that " + this.component.getName() + " is already with someone else`").queue();
                return;
            }

            generateNewGameId();
            game.put(this.sender, this);
            pending.put(this.message, this.component);
            games.add(this);

        } else {
            if (game.containsKey(sender)) {
                channel.sendMessage(":x: | `Sorry " + this.sender.getName() + " but it seems you're already in game in " + game.get(this.sender).getGuild().getName() + "` **NOTE: You can use `r?rps leave` to leave the game that you have been created before with your component**").queue();
                return;
            }

            generateNewGameId();
            game.put(this.sender, this);
            createNewGame();
            games.add(this);
        }
    }

    /**
     * create new match, NOTE: this method only used one time to start the game.
     */
    private void createNewGame() {

    }

    /**
     * end all game tasks.
     */
    public void gameEnd() {

    }

    /**
     * rematch the game after it ends.
     */
    public void rematch() {

    }

    /**
     * Updating the message to currently info for the game stats.
     */
    public void updateMessage() {

    }

    /**
     * If the message deleted by accidentally, it will automatically send to the same previous info.
     */
    public void resendGameMessage() {

    }

    /**
     * Update the pending request.
     */
    public void updatePendingRequest(boolean bool) {
        if (bool) {
            createNewGame();
        } else {
            endTasks();
        }
    }

    /**
     * End the tasks for the game.
     * <p>
     * endTasks is different from gameEnd, endTasks used to end the game tasks before the component accept the match.
     * basically this method only usable when there is component inside the system process.
     */
    private void endTasks() {
        games.remove(this);
        game.remove(this.sender);
        pending.remove(this.message);
    }

    public HashMap<User, RPSUtility> getGame() {
        return game;
    }

    public HashMap<Message, User> getPending() {
        return pending;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {

        if (rounds < 1) {
            throw new NumberFormatException("rounds couldn't be below 1");
        }

        this.rounds = rounds;
    }

    public boolean getUnlimitedLoop() {
        return this.unlimitedLoop;
    }

    public void setUnlimitedLoop(boolean bool) {
        this.unlimitedLoop = bool;
    }

    public User getSender() {
        return sender;
    }

    public @Nullable User getComponent() {
        return component;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public @NotNull Message getMessage() {
        return message;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    /**
     * Check if the sender is selected or not.
     */
    public boolean isSenderSelect() {
        return this.senderMove != null;
    }

    /**
     * Check if the component is selected or not.
     */
    public boolean isComponentSelect() {
        return this.componentMove != null;
    }

    /**
     * Set the sender choice.
     */
    public void setSenderMove(RPSTypes type) {
        this.senderMove = type;
    }

    /**
     * set the component choice.
     */
    public void setComponentMove(RPSTypes type) {
        this.componentMove = type;
    }

    /**
     * Check if the component is already in another game or not.
     */
    private boolean isComponentInGame() {
        return games.stream().allMatch(it -> it.component.equals(component)); // true means the components in game, otherwise, false
    }

    private void generateNewGameId() {
        Random random = new Random();
        int minimum = 100000;
        int maximum = 999999;
        int id = random.nextInt((minimum - maximum) + 1);

        if (games.stream().allMatch(it -> it.gameId == id)) {
            generateNewGameId();
        } else {
            this.gameId = id;
        }
    }
}