import commands.funCategory.MessengerEvent
import commands.gamesCategory.RPCEvent
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.jdabuilder.light
import listeners.Events
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManager
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.utils.AllowedMentions
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
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
                disableCache(EnumSet.of(CacheFlag.EMOJI, CacheFlag.VOICE_STATE))
                setMemberCachePolicy(MemberCachePolicy.ALL)
                setChunkingFilter(ChunkingFilter.ALL)
                addEventListeners(Events(Main), RPCEvent(), MessengerEvent())
            }.awaitReady()

            AllowedMentions.setDefaultMentions(EnumSet.of(Message.MentionType.USER))

            jda.updateCommands()
                .addCommands(
                    Commands.user("Messenger"),
                    Commands.user("Rock Paper Scissors")
                ).queue()

            Logger().info("Bot has been built!")
        }
    }
}

fun main() {
    Main.start()
}
