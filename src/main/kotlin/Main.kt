import commands.potatoCommand
import commands.questionCommand
import listeners.Events
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class Main() {

    fun Logger(): Logger = LoggerFactory.getLogger(Class.forName("Main"))

    fun start() {
        var gateways = arrayListOf<GatewayIntent>(
            GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.GUILD_PRESENCES,
        )

        val token = "ODk3MTgzNDAxOTM5OTc2MjEz.G5fGl_.eYZrPlXSCpzFUH07VI8OlhauEWuIVKJRoWFTJg"

        val builder = JDABuilder.create(token, gateways)
        builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS)
        builder.disableCache(EnumSet.of(CacheFlag.EMOTE))
        builder.addEventListeners(Events(this))
        builder.build()

        Logger().info("Bot has been built!")
    }
}

fun main() {
    val bot = Main()

    // Calling the start function from the main class
    bot.start()
}
