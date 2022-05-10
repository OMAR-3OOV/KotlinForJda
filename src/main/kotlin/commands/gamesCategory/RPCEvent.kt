package commands.gamesCategory

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import java.awt.Color
import java.util.*

class RPCEvent: ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (!RPC.rpc.containsKey(event.user)) return

        val rpcGame = RPC.rpc[event.user]
        val message = RPC.message[rpcGame]

        val random = Random()
        val nextStep: Int = random.nextInt(3)

        val rpc = arrayListOf("✊", "\uD83D\uDD90", "✌")

        val geto = when (nextStep+1) {
            1 -> rpc[0]
            2 -> rpc[1]
            3 -> rpc[2]
            else -> rpc[0]
        }

        val userselection = event.interaction.button.label
        val botselection = geto

        rpcGame!!.setOpponentSelection(botselection)
        rpcGame.setOpponentSelect(true)

        println(geto)

        rpcGame.setSenderSelection(userselection)
        rpcGame.setSenderSelect(true)

        val description = rpcGame.embed.descriptionBuilder

        description.clear()
        description.append(rpcGame.gameMessage().toString()
            .replace("-", "\uD83D\uDFE5")
            .replace("|", "\uD83D\uDFE5")
            .replace(".", "⬛")
            .replace("P1", userselection)
            .replace("P2", botselection))

        if ((userselection == rpcGame.rock && botselection == rpcGame.scissors) ||
            (userselection == rpcGame.paper && botselection == rpcGame.rock) ||
            (userselection == rpcGame.scissors && botselection == rpcGame.paper)) {

            rpcGame.embed.addField("> ${rpcGame.sender.name} Won!", "**${rpcGame.sender.name}** is winner & **${rpcGame.opponent.name}** is loser!", false)
            rpcGame.embed.setColor(Color(0, 250, 0))
        } else if ((userselection == rpcGame.rock && botselection == rpcGame.rock) ||
            (userselection == rpcGame.paper && botselection == rpcGame.paper) ||
            (userselection == rpcGame.scissors && botselection == rpcGame.scissors)) {

            rpcGame.embed.addField("> Draw between ${rpcGame.sender.name} & ${rpcGame.opponent.name}!", "**${rpcGame.sender.name}** draw with & **${rpcGame.opponent.name}** in rpc round", false)
            rpcGame.embed.setColor(Color(200, 150, 0))
        } else {
            rpcGame.embed.addField("> ${rpcGame.sender.name} Lost!", "**${rpcGame.sender.name}** is loser & **${rpcGame.opponent.name}** is winner!", false)
            rpcGame.embed.setColor(Color(250, 0, 0))
        }

        //message!!.editMessageEmbeds(rpcGame.embed.build()).setActionRows(ActionRow.of(event.button.asDisabled())).queue { q -> q.interaction }

        event.deferEdit().queue { queue ->
            queue.editOriginalEmbeds(rpcGame.embed.build()).setActionRows(ActionRow.of(event.button.asDisabled())).queue { q -> q.interaction }

            rpcGame.endGame()
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {

    }

}