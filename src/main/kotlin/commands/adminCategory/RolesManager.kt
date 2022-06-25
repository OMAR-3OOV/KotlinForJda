package commands.adminCategory

import Command
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utilities.categoryUtil.Categories
import utilities.staffUtil.Roles
import utilities.staffUtil.RolesData
import java.util.regex.Matcher
import java.util.regex.Pattern

class RolesManager: Command {
    override fun handle(args: List<String>, event: MessageReceivedEvent) {
        try {
            val handler = ArrayList<String>(args)

            if (handler.isNotEmpty()) {

                if (handler[0].contains(Regex("\\d+"))) {
                    var userMentioned = handler[0]

                    val regex: Pattern = Pattern.compile(Message.MentionType.USER.pattern.pattern())
                    val matcher: Matcher = regex.matcher(userMentioned)

                    var user: User? = null
                    if (matcher.find()) {
                        userMentioned = userMentioned.replace("<", "").replace("!", "").replace("@", "").replace("#", "").replace("&", "").replace(">", "")
                        user = event.guild.getMemberById(userMentioned)!!.user
                    } else {
                        event.channel.sendMessage("Sorry, i can't decide did you type `${handler[1]}` please use r?help role").queue()
                    }

                    val roledata = RolesData(user!!)
                    val embed = Embed {
                        title = "[${roledata.getUserRole().displayName}] ${user.name}"
                        color = roledata.getUserRole().color.rgb
                        description = "**> People with same role:** *${roledata.getConfig().getStringList(roledata.getUserRole().key).size}*"
                    }

                    event.channel.sendMessageEmbeds(embed).queue()
                } else if (handler[0] == "add") {

                } else if (handler[0] == "remove") {

                }
            }
        } catch (argsErr: NullPointerException) {
            argsErr.printStackTrace()
        }

    }

    override val help: String
        get() = "r?role <add/remove> <user> `(you can use r?role <user> to get info)!`"
    override val command: String
        get() = "role"
    override val category: Categories
        get() = Categories.MANAGEMENT
    override val roles: List<Roles>
        get() = arrayListOf(Roles.ADMIN, Roles.CEO)
    override val description: String
        get() = "To add/remove the role from the discord user (*ON DISCORD BOT CONTROL*) or get the info"
    override val isDisplay: Boolean
        get() = false
}