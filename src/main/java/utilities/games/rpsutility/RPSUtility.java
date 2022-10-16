package utilities.games.rpsutility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * All things we need to create new RPS Game: Users ID ( sender, Opponent ) / Guild / Channel / Message ( Message, Embed )
 * <p>
 * if user want to play with bot the Opponent value should be null.
 */

public class RPSUtility extends PlayersUtility {

    /* System requirement to game data */
    private final EmbedBuilder embed;
    private final MessageUtility messageUtility;

    /* Game system */
    private int gameId;
    private int rounds = 1; // Default
    private boolean unlimitedLoop = false; // Default
    public static final List<RPSUtility> games = new ArrayList<>();
    public static final HashMap<Message, RPSUtility> game = new HashMap<>();
    public static final HashMap<User, RPSUtility> pending = new HashMap<>();
    public ScheduledFuture<?> timer;
    private boolean pendingRequest = false;
    private User winner;
    /* Class methods */

    /**
     * @param sender   related to who send the request.
     * @param opponent related to who mentioned in the request ( if there is no mention it will set to bot).
     * @param guild    related to the guild that the request come from
     * @param channel  related to the channel that the request come from
     * @param embed    related to {@link #embed}
     */
    public RPSUtility(User sender, @Nullable User opponent, Guild guild, TextChannel channel, @NotNull EmbedBuilder embed) {
        super(guild, sender, opponent);

        this.messageUtility = new MessageUtility(channel);
        this.embed = embed;

        // Game tasks
        if (super.getOpponent() != null) {
            if (super.getOpponent().equals(getGuild().getSelfMember().getUser())) {
                if (isSenderInGame()) {
                    channel.sendMessage(":x: | `Sorry " + getSender().getName() + " but it seems you're already in game in " + getGuild().getName() + "` **NOTE: You can use `r?rps leave` to leave the game**").queue();
                    return;
                }

                generateNewGameId();
                createNewGame();
                games.add(this);
                return;
            } else if (super.getOpponent().isBot() || super.getOpponent().isSystem()) {
                channel.sendMessage(":x: | **YOU CAN'T PLAY WITH OTHER BOTS, ONLY WITH ANYA >:c**").queue();
                return;
            }

            if (super.getOpponent().equals(sender)) {
                channel.sendMessage(":x: | `Are you that lonely to play with yourself?`").queue();
                return;
            }

            if (isSenderInGame()) {
                channel.sendMessage(":x: | `Sorry " + getSender().getName() + " but it seems you're already in game with " + getOpponent().getName() + " in " + getGuild().getName() + "` **NOTE: You can use `r?rps leave` to leave the game that you have been created before against Opponent**").queue();
                return;
            }

            if (isOpponentInGame()) {
                channel.sendMessage(":x: | `It seems that " + getOpponent().getName() + " is already with someone else`").queue();
                return;
            }

            generateNewGameId();
            pending.put(getOpponent(), this);
            createNewGame();
            games.add(this);
        }
    }

    /**
     * This method to create new game tasks.
     */
    public void createNewGame() {

        EmbedBuilder embed = this.embed;

        assert getOpponent() != null;
        embed.setDescription(getEmbedMessage(getSender(), getOpponent()).toString()
                .replace("-", "\uD83D\uDFE5")
                .replace("|", "\uD83D\uDFE5")
                .replace(".", "⬛")
                .replace("P1", "◽")
                .replace("P2", "◽"));

        embed.setColor(new Color(255, 150, 0));

        if (getOpponent() != null && pendingRequest) { // Message after opponent accept

            embed.setAuthor(getSender().getName() + " Against " + getOpponent().getName());
            this.messageUtility.addButtons(rpsButtons(false));

        } else if (getOpponent().equals(getGuild().getSelfMember().getUser())) { // If the opponent are system

            var random = new Random();
            var nextStep = random.nextInt(3); // 0, 1, 2 ( there is 3 choice only in rps game )

            var choice = switch (nextStep) {
                case 0 -> RPSTYPES.ROCK;
                case 1 -> RPSTYPES.PAPER;
                case 2 -> RPSTYPES.SCISSORS;
                default -> throw new IllegalStateException("Unexpected value: " + nextStep);
            };
            setOpponentMove(choice);

            embed.setAuthor(getSender().getName() + " Against Me");
            this.messageUtility.addButtons(rpsButtons(false));

        } else if (getOpponent() != null) { // Accept - Deny

            embed.setAuthor(getSender().getName() + " Against " + this.getOpponent().getName() + " ( Pending request )");

            List<Button> pendingButtons = new ArrayList<>();
            pendingButtons.add(Button.success("accept-button-" + this.getOpponent().getId(), "Accept"));
            pendingButtons.add(Button.danger("deny-button-" + this.getOpponent().getId(), "Deny"));

            timer = Executors.newSingleThreadScheduledExecutor().schedule(() -> {

                MessageEditData messageEditData = messageUtility.create(builder -> {
                    embed.setAuthor(getSender().getName() + " Against " + getOpponent().getName() + " ( Non response )");
                    embed.setColor(new Color(200, 0, 0));

                    MessageEmbed messageEmbed = embed.build();
                    builder.setEmbeds(messageEmbed);
                }).addButtons(pendingButtons.stream().map(Button::asDisabled).toList()).buildEditData();

                messageUtility.getMessage().editMessage(messageEditData).queue();
                updatePendingRequest(false);
            }, 5, TimeUnit.SECONDS);

            this.messageUtility.addButtons(pendingButtons);
        }

        MessageCreateData messageCreateData = this.messageUtility.create(msg -> {
            MessageEmbed messageEmbed = embed.build();
            msg.setEmbeds(messageEmbed);
        }).buildCreateData();

        Message message = this.messageUtility.getChannel().sendMessage(messageCreateData).complete();
        this.messageUtility.setMessage(message);

        game.put(message, this);
    }

    /**
     * end all game tasks.
     */
    public void gameEnd() {

        if (this.getOpponent() != null) {
            if (winningMove().equals(0)) {
                String top_content = "> Won!";
                String button_content = "**%s** Won against **%p**!".replace("%s", getSender().getName()).replace("%p", getOpponent().getName());

                this.embed.addField(top_content, button_content, false);
                setWinner(getSender());
                this.embed.setColor(new Color(0, 250, 0));
            } else if (winningMove().equals(1)) {
                String top_content = "> Draw!";
                String button_content = "**%s** & **%p** Draws".replace("%s", getSender().getName()).replace("%p", getOpponent().getName());

                this.embed.addField(top_content, button_content, false);
                this.embed.setColor(new Color(30, 100, 200));
            } else if (winningMove().equals(2)) {
                String top_content = "> Lost!";
                String button_content = "**%s** is lost against **%p**!".replace("%s", getSender().getName()).replace("%p", getOpponent().getName());
                this.embed.addField(top_content, button_content, false);

                setWinner(getOpponent());
                this.embed.setColor(new Color(250, 0, 0));
            } else if (winningMove().equals(3)) {
                this.embed.addField("> Game Over, no winners", "if you see this message this means the bot didn't decide on of the choice, please report this to the developer!", true);
                this.embed.setColor(new Color(1, 1, 1));
            } else {
                this.embed.addField("> Game Over, no winners", "if you see this message, this means you made something make the game broken please contact with the developers to fix it!", true);
                this.embed.setColor(new Color(1, 1, 1));
            }

        }

        MessageUtility messageEditBuilder = this.messageUtility.create(msg -> {
            MessageEmbed messageEmbed = this.embed.build();
            msg.setEmbeds(messageEmbed);
        });

        if (unlimitedLoop) {
            this.messageUtility.getMessage().editMessage(messageEditBuilder.buildEditData()).queue();
        } else {
            this.messageUtility.addButtons(rpsButtons(true));
            this.messageUtility.getMessage().editMessage(messageEditBuilder.buildEditData()).queue();
            endTasks();
        }
    }

    private User getWinner() {
        return this.winner;
    }

    /**
     * @param winner to set the {@link #winner}
     */
    public void setWinner(@NotNull User winner) {
        this.winner = winner;
    }

    /**
     * rematch the game after it ends.
     */
    public void rematch() {
        setOpponentMove(null);
        setSenderMove(null);

        if (getOpponent() == getGuild().getSelfMember().getUser()) {
            var random = new Random();
            var nextStep = random.nextInt(3); // 0, 1, 2 ( there is 3 choice only in rps game )

            var botchoice = switch (nextStep) {
                case 0 -> RPSTYPES.ROCK;
                case 1 -> RPSTYPES.PAPER;
                case 2 -> RPSTYPES.SCISSORS;
                default -> throw new IllegalStateException("Unexpected value: " + nextStep);
            };
            setOpponentMove(botchoice);
        }
        updateMessage(true);
    }

    /**
     * @return related to {@link #embed Embed Message}
     */
    public StringBuilder getEmbedMessage() {
        StringBuilder gui = new StringBuilder();

        assert getOpponent() != null;
        gui.append("> **").append(getSender().getAsMention()).append("** ( ").append(isSenderMove() ? "Ready" : "Waiting").append(" )").append(" vs **").append(getOpponent().getAsMention()).append("** ( ").append(isOpponentMove() ? "Ready" : "Waiting").append(" )").append("\n");
        gui.append(" ").append("\n");
        gui.append("---------").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("|.P1.|.P2.|").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("---------").append("\n");
        gui.append(" ").append("\n");

        return gui;
    }

    /**
     * @param sender   related to {@link #sender}
     * @param opponent related to {@link #opponent}
     * @return related to {@link #embed Embed Message}
     */
    public StringBuilder getEmbedMessage(User sender, User opponent) {
        StringBuilder gui = new StringBuilder();

        gui.append("> **").append(sender.getAsMention()).append("** ( ").append(isSenderMove() ? "Ready" : "Waiting").append(" )").append(" vs **").append(opponent.getAsMention()).append("** ( ").append(isOpponentMove() ? "Ready" : "Waiting").append(" )").append("\n");
        gui.append(" ").append("\n");
        gui.append("---------").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("|.P1.|.P2.|").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("---------").append("\n");
        gui.append(" ").append("\n");

        return gui;
    }

    /**
     * @param isDisable to disable the buttons
     * @return related to {@link Button}
     */
    public List<Button> rpsButtons(boolean isDisable) {
        List<Button> bts = new ArrayList<>();

        Arrays.stream(RPSTYPES.values()).forEach(type -> {
            if (isDisable) {
                bts.add(Button.success("emoji-" + getGameId() + "-" + type.getKey(), type.getEmoji()).asDisabled());
            } else {
                bts.add(Button.success("emoji-" + getGameId() + "-" + type.getKey(), type.getEmoji()).asEnabled());
            }
        });

        return bts;
    }

    /**
     * Updating the message to currently info for the game stats.
     *
     * @param isRematch to check if {@link #rematch()}
     */
    public void updateMessage(boolean isRematch) {
        EmbedBuilder embed = this.embed;

        embed.setAuthor(getSender().getName() + " Against " + getOpponent().getName());
        embed.setDescription(getEmbedMessage().toString()
                .replace("-", "\uD83D\uDFE5")
                .replace("|", "\uD83D\uDFE5")
                .replace(".", "⬛")
                .replace("P1", "◽")
                .replace("P2", "◽"));

        embed.setColor(new Color(255, 150, 0));

        MessageUtility messageEditData = this.messageUtility.create(msg -> {
            MessageEmbed messageEmbed = this.embed.build();
            msg.setEmbeds(messageEmbed);
        });

        if (isRematch) {
            Button cancel = Button.danger(this.gameId + "-cancel", "❌");
            this.messageUtility.getMessage().editMessage(messageEditData.buildEditData()).setComponents(ActionRow.of(rpsButtons(false)), ActionRow.of(cancel.asEnabled())).queue();
        } else {
            this.messageUtility.getMessage().editMessage(messageEditData.buildEditData()).queue();
        }
    }

    /**
     * If the message deleted by accidentally, it will automatically send to the same previous info.
     */
    public void resendGameMessage() {
        MessageCreateData messageCreateData = this.messageUtility.create(msg -> {
            MessageEmbed messageEmbed = embed.build();
            msg.setEmbeds(messageEmbed);
        }).buildCreateData();

        Message message = this.messageUtility.getChannel().sendMessage(messageCreateData).complete();
        this.messageUtility.setMessage(message);
        game.put(message, this);
    }

    /**
     * Update the pending request.
     *
     * @param bool to update the pending request
     */
    public void updatePendingRequest(boolean bool) {
        if (bool) {
            this.pendingRequest = true;
            this.getPending().remove(getOpponent());
        } else {
            endTasks();
        }
    }

    /**
     * End the tasks for the game.
     * <p>
     * endTasks is different from gameEnd, endTasks used to end the game tasks before the Opponent accept the match.
     * basically this method only usable when there is Opponent inside the system process.
     */
    public void endTasks() {
        games.remove(this);
        game.remove(this.messageUtility.getMessage());
        pending.remove(this.getOpponent());
    }

    /**
     * This is the message system that control the game panel.
     *
     * @return game panel message.
     */
    public MessageUtility getMessageUtility() {
        return messageUtility;
    }

    /**
     * @return related to {@link #gameId Game ID}
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * @return related to {@link RPSUtility}
     */
    public HashMap<Message, RPSUtility> getGame() {
        return game;
    }

    /**
     * @return related to {@link #pending}
     */
    public HashMap<User, RPSUtility> getPending() {
        return pending;
    }

    public ScheduledFuture<?> getTimer() {
        return timer;
    }

    /**
     * This winning system works with numbers, each number have meaning.
     *
     * @return numbers [0, 1, 2, 3]
     * @Number 0 return to win.
     * @Number 1 return to draw.
     * @Number 2 return to lose.
     * @Number 3 return to null.
     * @Note it created as this way to make the system way easier & also to get different choice!
     */
    public Integer winningMove() {
        if (getSenderMove() == null || getOpponentMove() == null) {
            return 3;
        } else if ( // Winning Move
                getSenderMove().equals(RPSTYPES.ROCK) && getOpponentMove().equals(RPSTYPES.SCISSORS) ||
                        getSenderMove().equals(RPSTYPES.SCISSORS) && getOpponentMove().equals(RPSTYPES.PAPER) ||
                        getSenderMove().equals(RPSTYPES.PAPER) && getOpponentMove().equals(RPSTYPES.ROCK)
        ) {
            return 0;
        } else if ( // Drawing Move
                getSenderMove().equals(RPSTYPES.ROCK) && getOpponentMove().equals(RPSTYPES.ROCK) ||
                        getSenderMove().equals(RPSTYPES.SCISSORS) && getOpponentMove().equals(RPSTYPES.SCISSORS) ||
                        getSenderMove().equals(RPSTYPES.PAPER) && getOpponentMove().equals(RPSTYPES.PAPER)
        ) {
            return 1;
        } else if ( // Losing Move
                getSenderMove().equals(RPSTYPES.SCISSORS) && getOpponentMove().equals(RPSTYPES.ROCK) ||
                        getSenderMove().equals(RPSTYPES.PAPER) && getOpponentMove().equals(RPSTYPES.SCISSORS) ||
                        getSenderMove().equals(RPSTYPES.ROCK) && getOpponentMove().equals(RPSTYPES.PAPER)
        ) {
            return 2;
        }
        return 3;
    }

    /**
     * @return related to {@link #rounds}
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * @param rounds to set {@link #rounds}
     */
    public void setRounds(int rounds) {

        if (rounds < 1) {
            throw new NumberFormatException("rounds couldn't be below 1");
        }

        this.rounds = rounds;
    }

    /**
     * @return related to {@link #unlimitedLoop}
     */
    public boolean isUnlimitedLoop() {
        return this.unlimitedLoop;
    }

    /**
     * @param bool to set {@link #unlimitedLoop}
     */
    public void setUnlimitedLoop(boolean bool) {
        this.unlimitedLoop = bool;
    }

    /**
     * @return related to {@link #embed}
     */
    public EmbedBuilder getEmbed() {
        return embed;
    }

    /**
     * Checking if the {@link #opponent} is already in game.
     *
     * @return boolean
     */
    private boolean isOpponentInGame() {
        return game.entrySet().stream().anyMatch(all -> {
            assert all.getValue().getOpponent() != null;
            return all.getValue().getOpponent().equals(super.getOpponent());
        }); // true means the Opponents in game, otherwise, false
    }

    /**
     * Checking if the {@link #sender} is already in game.
     *
     * @return boolean
     */
    private boolean isSenderInGame() {
        return game.entrySet().stream().anyMatch(all -> all.getValue().getSender().equals(super.getSender()));
    }

    /**
     * The {@link #gameId} generator, every game is running should have a different ID, it will be really low chance to get more than 1 game with same {@link #gameId}
     */
    private void generateNewGameId() {
        Random random = new Random();
        int minimum = 100000;
        int maximum = 999999;

        this.gameId = random.nextInt((maximum - minimum) + 1);
    }
}