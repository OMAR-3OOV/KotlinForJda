package commands.informationCategory

import Command
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.signature.TwitterCredentials
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import utilities.staffUtil.Roles

class Twitter : Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        val twitter = TwitterClient(TwitterCredentials.builder()
            .accessToken("")
            .accessTokenSecret("")
            .apiKey("")
            .apiSecretKey("")
            .build()
        )
    }

    override val help: String
        get() = TODO("Not yet implemented")
    override val command: String
        get() = TODO("Not yet implemented")
    override val category: Categories
        get() = TODO("Not yet implemented")
    override val roles: List<Roles>
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")
    override val isDisplay: Boolean
        get() = TODO("Not yet implemented")

}