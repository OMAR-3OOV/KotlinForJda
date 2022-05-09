import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import java.io.FileNotFoundException

interface Command {

    @Throws(FileNotFoundException::class)
    fun handle(args: List<String>, event: MessageReceivedEvent)

    val help: String

    val command: String

    val category: Categories

    val description: String

    val isDisplay: Boolean

}