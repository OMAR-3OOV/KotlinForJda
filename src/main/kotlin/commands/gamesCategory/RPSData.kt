package commands.gamesCategory

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

data class RPSData(val sender: User, val opponent: User, val textChannel: TextChannel, var embed: EmbedBuilder) {

    companion object {
        @JvmStatic
        var rps: MutableMap<User, RPSData> = LinkedHashMap()
    }

    val gamesList = LinkedList<RPSData>()

    val rock: String = "✊"
    val paper: String = "\uD83D\uDD90"
    val scissors: String = "✌"

    var isSenderSelected: Boolean = false
    var isOpponentSelected: Boolean = false

    var senderSelecttion: String? = null
    var opponentSelecttion: String? = null

    var gameID: Int? = null

    init {
        if (!rps.containsKey(sender)) {
            rps[sender] = this
            gameCreate()
        } else {
            textChannel.sendMessage("${this.sender.asMention}, You're already in game with ${this.opponent.asMention}").queue()
        }

        println(rps.entries)
    }

    fun gameCreate() {

        this.embed = EmbedBuilder()

        embed.setDescription(gameMessage().toString()
            .replace("-", "\uD83D\uDFE5")
            .replace("|", "\uD83D\uDFE5")
            .replace(".", "⬛")
            .replace("P1", "◽")
            .replace("P2", "◽")
        )

        embed.setColor(Color(0, 255, 0))

        // You can get game information from the game ID
        this.gameID = generateID()
        this.gamesList.add(this)

        val bts = ArrayList<Button>();

        bts.add(Button.success("game-rock", rock))
        bts.add(Button.success("game-paper", paper))
        bts.add(Button.success("game-scissors", scissors))

        val message = this.textChannel.sendMessageEmbeds(this.embed.build()).setActionRows(ActionRow.of(bts).asEnabled()).complete()
    }

    fun gameMessage():StringBuilder {
        val gui: StringBuilder = StringBuilder()

        gui.append("`${sender.name} (${if (isSenderSelected) "Ready" else "Not Ready"}) vs ${opponent.name} (${if (isOpponentSelected) "Ready" else "Not Ready"})`").append("\n")
        gui.append("---------").append("\n")
        gui.append("|...|...|").append("\n")
        gui.append("|.P1.|.P2.|").append("\n")
        gui.append("|...|...|").append("\n")
        gui.append("---------").append("\n")

        return gui
    }

    fun generateID(): Int {
        val random: Random = Random()
        val minimum = 10000
        val maximum = 99999
        return random.nextInt((maximum - minimum) + 1) + minimum;
    }

    fun setSenderSelect(bool: Boolean) {
        this.isSenderSelected = bool
    }

    fun setOpponentSelect(bool: Boolean) {
        this.isOpponentSelected = bool
    }

    fun setSenderSelection(selection: String) {
        senderSelecttion = selection
    }

    fun setOpponentSelection(selection: String) {
        opponentSelecttion = selection
    }

    fun rematch() {

    }

    fun endGame() {
        if (rps.containsKey(sender)) {
            rps.remove(sender)
        }
    }

    fun endGame(user: User) {
        if (rps.containsKey(user)) {
            rps.remove(user)
        }
    }
}