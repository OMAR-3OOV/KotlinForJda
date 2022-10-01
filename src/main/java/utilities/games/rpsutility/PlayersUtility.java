package utilities.games.rpsutility;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * This players data class related to {@link RPSUtility} to manage the player game process.
 */
public class PlayersUtility {

    private final User sender;

    @Nullable
    private User opponent;
    private final Guild guild;

    public RPSTYPES senderMove;
    private RPSTYPES opponentMove;

    /**
     * @param guild related to the guild the send request from
     * @param sender the user who send the game request
     * @param opponent the user who receive the game request
     *
     * @Note: Once there is no {@link #opponent} it will automatically put the bot as opponent
     */
    public PlayersUtility(Guild guild, User sender, @Nullable User opponent) {
        this.guild = guild;
        this.sender = sender;

        if (opponent != null) {
            this.opponent = opponent;
        } else {
            this.opponent = this.guild.getSelfMember().getUser();
        }

    }

    public Guild getGuild() {
        return guild;
    }

    @Contract(pure = true)
    public User getSender() {
        return sender;
    }

    @Contract(pure = true)
    public User getOpponent() {
        return opponent;
    }

    @Contract(pure = true)
    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    /**
     * get the {@link #sender} move.
     * @return related to {@link #senderMove} checker
     */
    @Contract(pure = true)
    public RPSTYPES getSenderMove() {
        return senderMove;
    }

    /**
     * get the {@link #opponent} move.
     * @return related to {@link #opponentMove} getter
     */

    public RPSTYPES getOpponentMove() {
        return opponentMove;
    }

    /**
     * Set the {@link #sender} move.
     * @param senderMove related to {@link #senderMove} set method
     */
    @Contract(pure = true)
    public void setSenderMove(RPSTYPES senderMove) {
        this.senderMove = senderMove;
    }

    /**
     * Set the {@link #opponent} move.
     * @param opponentMove related to {@link #opponentMove} set method
     */
    @Contract(pure = true)
    public void setOpponentMove(RPSTYPES opponentMove) {
        this.opponentMove = opponentMove;
    }

    /**
     * Check if the {@link #sender} have select a move.
     * @return related to {@link #senderMove}
     */
    public boolean isSenderMove() {
        return this.senderMove != null;
    }

    /**
     * Check if the {@link #opponent} have select a move.
     * @return related to {@link #opponentMove}
     */
    public boolean isOpponentMove() {
        return this.opponentMove != null;
    }

    /**
     * @return related to both of {@link #sender} & {@link #opponent} if they have select a move!
     */
    public boolean isAllMoves() {
        return (isSenderMove() && isOpponentMove());
    }


}
