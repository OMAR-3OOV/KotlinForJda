package commands.informationCategory

import Command
import com.github.scribejava.apis.TwitterApi
import dev.minn.jda.ktx.messages.Embed
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters
import io.github.redouane59.twitter.dto.list.TwitterList
import io.github.redouane59.twitter.dto.list.TwitterList.TwitterListData
import io.github.redouane59.twitter.dto.tweet.TweetType
import io.github.redouane59.twitter.dto.tweet.TweetV2
import io.github.redouane59.twitter.dto.tweet.TweetV2.TweetData
import io.github.redouane59.twitter.dto.user.User
import io.github.redouane59.twitter.dto.user.UserV2.UserData
import io.github.redouane59.twitter.signature.TwitterCredentials
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import utilities.staffUtil.Roles
import java.awt.Color
import java.time.format.DateTimeFormatter

class Twitter : Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        try {
            val twitter = TwitterClient(
                TwitterCredentials.builder()
                    .accessToken("1194246243817574400-P2O0nrGJc0GoeuxdoBXoxawL0YMW2h")
                    .accessTokenSecret("MAC1bXiH25529CTdtjgRKAc0Np3RhDO08XRSbO9FHSsHc")
                    .apiKey("KS1S8VSOQvD42Nkih0UuToH0a")
                    .apiSecretKey("71bBGQq3WjSOZYx3w7n0oVu7wfZVy33kB8juTFUdfeNBzbEZ0o")
                    .build()
            )

            if (args.isEmpty()) {
                event.channel.sendMessage(":x: | You must mention a twitter username to get the user info!").queue()
                return
            }

            val USERNAME = args[0];

            val user = twitter.getUserFromUserName(USERNAME)

            if (user == null) {
                event.channel.sendMessage(":x: | This username is not exist!").queue()
                return
            }

//            println(twitter.getUserTimeline(user.id).includes.tweets.first { it.referencedTweets.first().id}.ge)

            val embed = Embed {
                author {
                    name = user.displayedName
                    url = "https://twitter.com/${user.name}"
                }
                description = "```\n${user.description}```"
                color = Color(32, 163, 246).rgb

                field {
                    name = "Joined in"
                    value = user.dateOfCreation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    inline = true
                }

                field {
                    name = "Tweets count"
                    value = user.tweetCount.toString()
                    inline = true
                }

//                field {
//                    name = "Last tweet"
//                    value = user.data.
//                    inline = true
//                }

                thumbnail = user.profileImageUrl
            }

            event.channel.sendMessageEmbeds(embed).queue()
        } catch (error : IllegalStateException) {
            event.channel.sendMessage(":x: | Request failed please resend the command again!").queue()
        }
    }

    override val help: String
        get() = "r?twitter <username>"
    override val command: String
        get() = "twitter"
    override val category: Categories
        get() = Categories.INFORMATION
    override val roles: List<Roles>
        get() = arrayListOf(Roles.ADMIN)
    override val description: String
        get() = "Check twitter profile stats"
    override val isDisplay: Boolean
        get() = true

}