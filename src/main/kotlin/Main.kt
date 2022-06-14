import commands.gamesCategory.RPCEvent
import listeners.Events
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.security.auth.login.LoginException

class Main {
    companion object {
        fun Logger(): Logger = LoggerFactory.getLogger(Main::class.java)

        @Throws(LoginException::class)
        @JvmStatic fun start() {
            val gateways = arrayListOf (
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_PRESENCES,
            )

            val token = "ODk3MTgzNDAxOTM5OTc2MjEz.G5fGl_.eYZrPlXSCpzFUH07VI8OlhauEWuIVKJRoWFTJg"

            val builder = JDABuilder.create(token, gateways)
            builder.enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS)
            builder.disableCache(EnumSet.of(CacheFlag.EMOTE, CacheFlag.VOICE_STATE))
            builder.addEventListeners(Events(this), RPCEvent())
            val jda = builder.build()

            jda.upsertCommand("shutdown", "shutdown ${jda.selfUser.name}!w").queue()
            Logger().info("Bot has been built!")
        }
    }
}

fun main() {
    Main.start()
}
