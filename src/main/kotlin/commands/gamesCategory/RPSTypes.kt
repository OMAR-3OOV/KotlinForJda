package commands.gamesCategory

import net.dv8tion.jda.api.entities.Emoji

enum class RPSTypes (val key: String, val displayName: String, val id: Int, val emoji: Emoji) {

    ROCK("rock", "Rock", 0, Emoji.fromUnicode("✌")),
    PAPER("paper", "Paper", 1, Emoji.fromUnicode("✊")),
    SCISSORS("scissors", "Scissors", 2, Emoji.fromUnicode("\uD83D\uDD90"));
}