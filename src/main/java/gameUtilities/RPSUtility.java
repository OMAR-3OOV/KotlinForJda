package gameUtilities;

import commands.gamesCategory.RPSTypes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * All things we need to create new RPS Game: Users ID ( sender, Opponent ) / Guild / Channel / Message ( Message, Embed )
 * <p>
 * if user want to play with bot the Opponent value should be null.
 */

public class RPSUtility {

    /* System requirement to game data */
    private final User sender;
    @Nullable
    private User Opponent;
    private final Guild guild;
    private final TextChannel channel;
    private final EmbedBuilder embed;
    private Message message;

    /* Game system */
    private int gameId;
    private RPSTypes senderMove;
    private RPSTypes opponentMove;
    private RPSTypes botMove;

    private int rounds = 1; // Default
    private boolean unlimitedLoop = false; // Default

    public static final List<RPSUtility> games = new ArrayList<>();
    public static final HashMap<Message, RPSUtility> game = new HashMap<>();
    public static final HashMap<User, RPSUtility> pending = new HashMap<>();
    private boolean pendingRequest = false;
    private User winner;
    /* Class methods */

    public RPSUtility(User sender, @Nullable User Opponent, Guild guild, TextChannel channel, @NotNull EmbedBuilder embed) {
        this.sender = sender;

        if (Opponent != null) {
            this.Opponent = Opponent;
        }

        this.guild = guild;
        this.channel = channel;
        this.embed = embed;

        // Game tasks

        if (Opponent != null) {

            if (Opponent.isBot() || Opponent.isSystem()) {
                channel.sendMessage(":x: | **YOU CAN'T PLAY WITH OTHER BOTS >:c**").queue();
                return;
            }

            if (isSenderInGame()) {
                channel.sendMessage(":x: | `Sorry " + this.sender.getName() + " but it seems you're already in game with " + this.Opponent.getName() + " in " + this.guild.getName() + "` **NOTE: You can use `r?rps leave` to leave the game that you have been created before against Opponent**").queue();
                return;
            }

            if (isOpponentInGame()) {
                channel.sendMessage(":x: | `It seems that " + this.Opponent.getName() + " is already with someone else`").queue();
                return;
            }

            if (Opponent == sender) {
                channel.sendMessage(":x: | `You can't play with yourself, please use r?rps`").queue();
                return;
            }

            generateNewGameId();
            pending.put(this.Opponent, this);
            createNewGame();
            games.add(this);

        } else {
            if (isSenderInGame()) {
                channel.sendMessage(":x: | `Sorry " + this.sender.getName() + " but it seems you're already in game in " + this.guild.getName() + "` **NOTE: You can use `r?rps leave` to leave the game**").queue();
                return;
            }

            generateNewGameId();
            createNewGame();
            games.add(this);

            System.out.println(this.sender.getName() + " Are playing rpc, There is " + games.size() + " Games");
        }
    }

    /**
     * create new match, NOTE: this method only used one time to start the game.
     */
    public void createNewGame() {

        if (Opponent != null && pendingRequest) { // Message after opponent accept
            EmbedBuilder embed = this.embed;

            assert this.getOpponent() != null;
            embed.setAuthor(this.sender.getName() + " Against " + this.getOpponent().getName());
            embed.setDescription(getEmbedMessage(this.sender, this.getOpponent()).toString()
                    .replace("-", "\uD83D\uDFE5")
                    .replace("|", "\uD83D\uDFE5")
                    .replace(".", "⬛")
                    .replace("P1", "◽")
                    .replace("P2", "◽"));

            embed.setColor(new Color(255, 150, 0));

            this.message = this.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(rpsButtons(false))).complete();
            game.put(this.message, this);
        } else if (getOpponent() != null) { // Accept - Deny
            EmbedBuilder embed = this.embed;

            embed.setAuthor(this.sender.getName() + " Against " + this.getOpponent().getName() + " ( Pending request )");
            embed.setDescription(getEmbedMessage(this.sender, this.getOpponent()).toString()
                    .replace("-", "\uD83D\uDFE5")
                    .replace("|", "\uD83D\uDFE5")
                    .replace(".", "⬛")
                    .replace("P1", "◽")
                    .replace("P2", "◽"));

            embed.setColor(new Color(255, 150, 0));

            List<Button> pendingButtons = new ArrayList<>();
            pendingButtons.add(Button.success("accept-button-" + this.getOpponent().getId(), "Accept"));
            pendingButtons.add(Button.danger("deny-button-" + this.getOpponent().getId(), "Deny"));

            this.message = channel.sendMessageEmbeds(embed.build()).setActionRows(ActionRow.of(pendingButtons)).complete();
            game.put(this.message, this);
        } else { // If there is no opponent mentioned
            EmbedBuilder embed = this.embed;
            this.Opponent = this.guild.getSelfMember().getUser();

            var random = new Random();
            var nextStep = random.nextInt(2); // 0, 1, 2 ( there is 3 choice only in rps game )

            var botchoice = switch (nextStep) {
                case 0 -> RPSTypes.ROCK;
                case 1 -> RPSTypes.PAPER;
                case 2 -> RPSTypes.SCISSORS;
                default -> throw new IllegalStateException("Unexpected value: " + nextStep);
            };
            setOpponentMove(botchoice);

            embed.setAuthor(this.sender.getName() + " Against Me");
            embed.setDescription(getEmbedMessage().toString()
                    .replace("-", "\uD83D\uDFE5")
                    .replace("|", "\uD83D\uDFE5")
                    .replace(".", "⬛")
                    .replace("P1", "◽")
                    .replace("P2", "◽"));

            embed.setColor(new Color(255, 150, 0));

            this.message = channel.sendMessageEmbeds(embed.build()).setActionRows(ActionRow.of(rpsButtons(false))).complete();
            game.put(this.message, this);
        }
    }

    /**
     * end all game tasks.
     */
    public void gameEnd() {

        if (this.getOpponent() != null) {
            assert this.Opponent != null;
            if (winningMove().equals(0)) {
                this.embed.addField(
                        "> " + this.sender.getName() + " Won!",
                        "**" + this.sender.getName() + "** is winner against **" + this.Opponent.getName() + "**!",
                        false
                );
                setWinner(this.sender);
                this.embed.setColor(new Color(0, 250, 0));
            } else if (winningMove().equals(1)) {
                this.embed.addField(
                        "> Draw!",
                        this.sender.getName() + " & " + this.Opponent.getName() + " Draws",
                        false
                );
                this.embed.setColor(new Color(200, 150, 0));
            } else if (winningMove().equals(2)) {
                this.embed.addField(
                        "> " + this.sender.getName() + " Lost!",
                        "**" + this.sender.getName() + "** is loser against **" + this.Opponent.getName() + "**!",
                        false
                );
                setWinner(this.Opponent);
                this.embed.setColor(new Color(250, 0, 0));
            } else if (winningMove().equals(3)) {
                this.embed.addField("> Game Over, no winners", "if you see this message this means the bot didn't decide on of the choice, please report this to the developer!", true);
                this.embed.setColor(new Color(1, 1, 1));
            } else {
                this.embed.addField("> Game Over, no winners", "if you see this message, this means you made something make the game broken please contact with the developers to fix it!", true);
                this.embed.setColor(new Color(1, 1, 1));
            }

        } else {
            EmbedBuilder embed = this.embed;

            embed.setAuthor("The winner is " + getWinner().getName(), "", "");
            embed.setDescription(getEmbedMessage().toString()
                    .replace("-", "\uD83D\uDFE5")
                    .replace("|", "\uD83D\uDFE5")
                    .replace(".", "⬛")
                    .replace("P1", getSenderMove().getEmoji().toString())
                    .replace("P2", getOpponentMove().getEmoji().toString()));

            if (getWinner().equals(this.sender)) {
                embed.setColor(new Color(0, 255, 0));
            } else {
                embed.setColor(new Color(255, 0, 0));
            }

        }

        if (unlimitedLoop) {
            this.message.editMessageEmbeds(this.embed.build()).setActionRows(ActionRow.of(rpsButtons(true))).queue();
        } else {
            this.message.editMessageEmbeds(this.embed.build()).setActionRows(ActionRow.of(rpsButtons(true))).queue();
            endTasks();
        }
    }

    private User getWinner() {
        return this.winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    /**
     * rematch the game after it ends.
     */
    public void rematch() {
        setOpponentMove(null);
        setSenderMove(null);

        if (this.Opponent == this.guild.getSelfMember().getUser()) {
            var random = new Random();
            var nextStep = random.nextInt(2); // 0, 1, 2 ( there is 3 choice only in rps game )

            var botchoice = switch (nextStep) {
                case 0 -> RPSTypes.ROCK;
                case 1 -> RPSTypes.PAPER;
                case 2 -> RPSTypes.SCISSORS;
                default -> throw new IllegalStateException("Unexpected value: " + nextStep);
            };
            setOpponentMove(botchoice);
        }

        EmbedBuilder embed = this.embed;

        assert this.Opponent != null;
        embed.setAuthor(this.sender.getName() + " Against " + this.Opponent.getName());
        embed.setDescription(getEmbedMessage().toString()
                .replace("-", "\uD83D\uDFE5")
                .replace("|", "\uD83D\uDFE5")
                .replace(".", "⬛")
                .replace("P1", "◽")
                .replace("P2", "◽"));

        embed.setColor(new Color(255, 150, 0));

        updateMessage(true);
    }

    public StringBuilder getEmbedMessage() {
        StringBuilder gui = new StringBuilder();

        assert this.Opponent != null;
        gui.append("> **").append(this.sender.getAsMention()).append("** ( ").append(isSenderSelect() ? "Ready" : "Waiting").append(" )").append(" vs **").append(this.Opponent.getAsMention()).append("** ( ").append(isOpponentSelect() ? "Ready" : "Waiting").append(" )").append("\n");
        gui.append(" ").append("\n");
        gui.append("---------").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("|.P1.|.P2.|").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("---------").append("\n");
        gui.append(" ").append("\n");

        return gui;
    }

    public StringBuilder getEmbedMessage(User sender, User opponent) {
        StringBuilder gui = new StringBuilder();

        gui.append("> **").append(sender.getAsMention()).append("** ( ").append(isSenderSelect() ? "Ready" : "Waiting").append(" )").append(" vs **").append(opponent.getAsMention()).append("** ( ").append(isOpponentSelect() ? "Ready" : "Waiting").append(" )").append("\n");
        gui.append(" ").append("\n");
        gui.append("---------").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("|.P1.|.P2.|").append("\n");
        gui.append("|...|...|").append("\n");
        gui.append("---------").append("\n");
        gui.append(" ").append("\n");

        return gui;
    }

    public List<Button> rpsButtons(boolean isDisable) {
        List<Button> bts = new ArrayList<>();

        Arrays.stream(RPSTypes.values()).forEach(type -> {
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
     */
    public void updateMessage(boolean isRematch) {
        EmbedBuilder embed = this.embed;

        embed.setDescription(getEmbedMessage().toString()
                .replace("-", "\uD83D\uDFE5")
                .replace("|", "\uD83D\uDFE5")
                .replace(".", "⬛")
                .replace("P1", "◽")
                .replace("P2", "◽"));

        embed.setColor(new Color(255, 150, 0));

        if (isRematch) {
            Button cancel = Button.danger(this.gameId + "-cancel", "❌");
            this.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(rpsButtons(false)), ActionRow.of(cancel.asEnabled())).queue();
        } else {
            this.message.editMessageEmbeds(embed.build()).setActionRows(ActionRow.of(rpsButtons(false))).queue();
        }
    }

    /**
     * If the message deleted by accidentally, it will automatically send to the same previous info.
     */
    public void resendGameMessage() {
        this.message = channel.sendMessageEmbeds(embed.build()).setActionRows(ActionRow.of(rpsButtons(false))).complete();
        game.put(this.message, this);
    }

    /**
     * Update the pending request.
     */
    public void updatePendingRequest(boolean bool) {
        if (bool) {
            this.pendingRequest = true;
            this.getPending().remove(this.Opponent);
            createNewGame();
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
        game.remove(this.message);
        pending.remove(this.getOpponent());
    }

    public int getGameId() {
        return gameId;
    }

    public HashMap<Message, RPSUtility> getGame() {
        return game;
    }

    public HashMap<User, RPSUtility> getPending() {
        return pending;
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
        if (senderMove == null || opponentMove == null) {
            return 3;
        } else if ( // Winning Move
                senderMove.equals(RPSTypes.ROCK) && opponentMove.equals(RPSTypes.SCISSORS) ||
                        senderMove.equals(RPSTypes.SCISSORS) && opponentMove.equals(RPSTypes.PAPER) ||
                        senderMove.equals(RPSTypes.PAPER) && opponentMove.equals(RPSTypes.ROCK)
        ) {
            return 0;
        } else if ( // Drawing Move
                senderMove.equals(RPSTypes.ROCK) && opponentMove.equals(RPSTypes.ROCK) ||
                        senderMove.equals(RPSTypes.SCISSORS) && opponentMove.equals(RPSTypes.SCISSORS) ||
                        senderMove.equals(RPSTypes.PAPER) && opponentMove.equals(RPSTypes.PAPER)
        ) {
            return 1;
        } else if ( // Losing Move
                senderMove.equals(RPSTypes.SCISSORS) && opponentMove.equals(RPSTypes.ROCK) ||
                        senderMove.equals(RPSTypes.PAPER) && opponentMove.equals(RPSTypes.SCISSORS) ||
                        senderMove.equals(RPSTypes.ROCK) && opponentMove.equals(RPSTypes.PAPER)
        ) {
            return 2;
        }
        return 3;
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

    public @Nullable User getOpponent() {
        return Opponent;
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

    public EmbedBuilder getEmbed() {
        return embed;
    }

    /**
     * Check if the sender is selected or not.
     */
    public boolean isSenderSelect() {
        return this.senderMove != null;
    }

    /**
     * Check if the Opponent is selected or not.
     */
    public boolean isOpponentSelect() {
        return this.opponentMove != null;
    }

    public boolean isCheckerSelect(RPSTypes choice) {
        return choice != null;
    }

    public boolean isAllSelected() {
        return (isSenderSelect() && isOpponentSelect());
    }

    /**
     * get the Opponent move.
     */
    public RPSTypes getSenderMove() {
        return this.senderMove;
    }

    /**
     * Set the sender move.
     */
    public void setSenderMove(RPSTypes type) {
        this.senderMove = type;
    }

    /**
     * get the Opponent move.
     */
    public RPSTypes getOpponentMove() {
        return this.opponentMove;
    }

    /**
     * set the Opponent move.
     */
    public void setOpponentMove(RPSTypes type) {
        this.opponentMove = type;
    }

    /**
     * Check if the Opponent is already in another game or not.
     */
    private boolean isOpponentInGame() {
        System.out.println(game.entrySet().stream().anyMatch(all -> {
            assert all.getValue().getOpponent() != null;
            return all.getValue().getOpponent().equals(this.Opponent);
        }));

        return game.entrySet().stream().anyMatch(all -> {
            assert all.getValue().getOpponent() != null;
            return all.getValue().getOpponent().equals(this.Opponent);
        }); // true means the Opponents in game, otherwise, false
    }

    private boolean isSenderInGame() {
        System.out.println(game.entrySet().stream().anyMatch(all -> all.getValue().getSender().equals(this.sender)));
        return game.entrySet().stream().anyMatch(all -> all.getValue().getSender().equals(this.sender));
    }

    private void generateNewGameId() {
        Random random = new Random();
        int minimum = 100000;
        int maximum = 999999;

        this.gameId = random.nextInt((maximum - minimum) + 1);
    }
}