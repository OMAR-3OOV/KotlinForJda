package commands.gamesCategory

import net.dv8tion.jda.api.entities.Emoji

enum class RPSTypes (val key: String, val displayName: String, val id: Int, val emoji: Emoji) {
    ROCK("rock", "Rock", 0, Emoji.fromUnicode("✊")),
    PAPER("paper", "Paper", 1, Emoji.fromUnicode("\uD83D\uDD90")),
    SCISSORS("scissors", "Scissors", 2, Emoji.fromUnicode("✌"));

    companion object {
        fun getTypeByName(key: String): RPSTypes {
            return RPSTypes.values().find { it.key == key }!!
        }
    }
}
