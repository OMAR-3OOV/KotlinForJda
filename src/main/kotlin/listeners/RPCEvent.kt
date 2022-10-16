package listeners

import dev.minn.jda.ktx.messages.Embed
import utilities.games.rpsutility.RPSUtility
import net.dv8tion.jda.api.events.ShutdownEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.ActionRow
import utilities.games.rpsutility.RPSTYPES
import java.awt.Color

class RPCEvent : ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val user = event.user

        if (RPSUtility.pending.containsKey(user)) {

            val rps = RPSUtility.pending[user]

            if (event.button.id.equals("accept-button-${rps!!.opponent!!.id}")) {

                val embed = rps.embed.also {
                    it.setAuthor("${rps.sender.name} Against ${rps.opponent!!.name}")
                    it.setColor(Color(200, 177, 99))
                }

                event.editMessageEmbeds(embed.build()).setComponents(ActionRow.of(rps.rpsButtons(false))).queue()

                rps.getTimer().cancel(false)
                rps.updatePendingRequest(true)
            } else if (event.button.id.equals("deny-button-${rps.opponent!!.id}")) {

                val embed = rps.embed.also {
                    it.setAuthor("${rps.sender.name} Against ${rps.opponent?.name} ( Denied )")
                    it.setColor(Color(200, 0, 0))
                }

                event.editMessageEmbeds(embed.build()).queue()
                rps.getTimer().cancel(false)
                rps.updatePendingRequest(false)
            }
        }

        if (RPSUtility.game.containsKey(event.message)) {
            val rps = RPSUtility.game.get(event.message)

            if (event.button.id.equals("${rps!!.gameId}-cancel") && (event.user.equals(rps.sender) || event.user.equals(rps.opponent))) {
                rps.endTasks()

                val embed = rps.embed
                embed.clearFields()
                embed.addField("Game Over", "${event.user.asMention} has been cancel the match!", false);

                event.deferEdit().setComponents(ActionRow.of(event.message.buttons.map { it.asDisabled() })).setEmbeds(embed.build()).queue()
                return
            }

            val sender = rps.sender
            val opponent = rps.opponent

            if (event.button.id == "emoji-${rps.gameId}-rock" || event.button.id == "emoji-${rps.gameId}-paper" || event.button.id == "emoji-${rps.gameId}-scissors")
                if (event.user == sender || event.user == opponent) {
                    val selection = event.interaction.button.id!!.replace("emoji-${rps.gameId}-", "")

                    if (user == sender) {
                        rps.senderMove = RPSTYPES.getTypeByName(selection)
                    }

                    if (user == opponent) {
                        rps.opponentMove = RPSTYPES.getTypeByName(selection)
                    }

                    if (rps.isAllMove()) {
                        val embed = rps.embed.also {
                            it.setDescription(
                                rps.embedMessage.toString()
                                    .replace("-", "\uD83D\uDFE5")
                                    .replace("|", "\uD83D\uDFE5")
                                    .replace(".", "⬛")
                                    .replace("P1", rps.senderMove!!.emoji.name)
                                    .replace("P2", rps.opponentMove!!.emoji.name)
                            )
                        }

                        event.deferEdit().setEmbeds(embed.build()).setComponents(ActionRow.of(rps.rpsButtons(true)))
                            .queue()

                        rps.gameEnd()

                        if (rps.isUnlimitedLoop) {
                            rps.rematch()
                        }
                    } else {
                        val embed = rps.embed.also {
                            it.setDescription(
                                rps.embedMessage.toString()
                                    .replace("-", "\uD83D\uDFE5")
                                    .replace("|", "\uD83D\uDFE5")
                                    .replace(".", "⬛")
                                    .replace("P1", "◽")
                                    .replace("P2", "◽")
                            )
                        }

                        event.deferEdit().setEmbeds(embed.build()).queue()
                    }
                }

        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {

    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        if (RPSUtility.game.entries.any { it.key.id.equals(event.messageId) }) {
            val rps = RPSUtility.game.entries.find { it.key.id.equals(event.messageId) }?.value

            rps!!.resendGameMessage()
        }
    }

    override fun onShutdown(event: ShutdownEvent) {
        println("Bot shutdown, ${event.timeShutdown}")
        if (RPSUtility.games.isNotEmpty()) {
            RPSUtility.games.forEach { game ->
                run {
                    game.isUnlimitedLoop = false
                    val embed = Embed {
                        description = game.getEmbedMessage().toString()
                        field {
                            name = "Game over!"
                            value = "The bot has been shutdown, sorry for ending the game here, you can continue after the bot resume!"
                            inline = false
                        }
                    }

                    game.messageUtility.message.editMessageEmbeds(embed).queue()
                    game.endTasks()
                }
            }
        }
    }
}