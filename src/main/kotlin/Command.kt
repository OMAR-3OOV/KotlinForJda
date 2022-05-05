import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.FileNotFoundException

interface Command {

    fun handle(args: List<String>, event: MessageReceivedEvent) { throw FileNotFoundException() }

    val getHelp: String

    val getCommand: String

    val getDescription: String

    val isDisplay: Boolean

}