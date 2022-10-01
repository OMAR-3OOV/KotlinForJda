package utilities.games.rpsutility;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rock Paper Scissors for rps game
 */
public enum RPSTYPES {

    ROCK("rock", "Rock", 0, Emoji.fromUnicode("✊")),
    PAPER("paper", "Paper", 1, Emoji.fromUnicode("\uD83D\uDD90")),
    SCISSORS("scissors", "Scissors", 2, Emoji.fromUnicode("✌"));

    private final String key;
    private final String name;
    private final int id;
    private final Emoji emoji;

    RPSTYPES(String key, String name, int id, UnicodeEmoji emoji) {
        this.key = key;
        this.name = name;
        this.id = id;
        this.emoji = emoji;
    }

    @Contract(pure = true)
    public String getKey() {
        return key;
    }

    @Contract(pure = true)
    public String getName() {
        return name;
    }

    @Contract(pure = true)
    public int getId() {
        return id;
    }

    @Contract(pure = true)
    public Emoji getEmoji() {
        return emoji;
    }

    @Nullable
    public static RPSTYPES getTypeByName(@NotNull String selection) {
        return RPSTYPES.valueOf(selection.toUpperCase());
    }
}
