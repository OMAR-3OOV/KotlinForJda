import commands.funCategory.MessengerEvent
import commands.gamesCategory.RPCEvent
import dev.minn.jda.ktx.jdabuilder.light
import listeners.Events
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.AllowedMentions
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import javax.security.auth.login.LoginException

class Main {
    companion object {
        fun Logger(): Logger = LoggerFactory.getLogger(Main::class.java)

        @Throws(LoginException::class)
        @JvmStatic fun start() {
            val gateways = arrayListOf (
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_PRESENCES, GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS
            )

            val token = "ODk3MTgzNDAxOTM5OTc2MjEz.G5fGl_.eYZrPlXSCpzFUH07VI8OlhauEWuIVKJRoWFTJg"

            val jda = light(token, enableCoroutines = true, intents = gateways) {
                enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS, CacheFlag.ROLE_TAGS, CacheFlag.MEMBER_OVERRIDES)
                disableCache(EnumSet.of(CacheFlag.EMOTE, CacheFlag.VOICE_STATE))

                addEventListeners(Events(Main), RPCEvent(), MessengerEvent())
            }.awaitReady()
            AllowedMentions.setDefaultMentions(EnumSet.of(Message.MentionType.USER))

            jda.upsertCommand("shutdown", "shutdown ${jda.selfUser.name}!w").queue()
            Logger().info("Bot has been built!")
        }
    }
}

fun main() {
    Main.start()
}
