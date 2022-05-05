//import com.sun.istack.Nullable
//import net.dv8tion.jda.api.entities.User
//import system.commands.Administration.*
//import java.util.*
//import java.util.regex.Pattern
//import kotlin.collections.HashMap
//
//class ConvertTest {
//    private var count: AtomicInteger? = null
//    private val categories: MutableMap<Int, Category> = HashMap<Int, Category>()
//    private fun addCommand(command: Command) {
//        if (!Companion.commands.containsKey(command.getInVoke())) {
//            Companion.commands[command.getInVoke()] = command
//        }
//    }
//
//    private fun addCategory(category: Category) {
//        if (!categories.containsKey(category)) {
//            categories[category.getId()] = category
//        }
//    }
//
//    val commands: Collection<Command>
//        get() = Companion.commands.values
//
//    fun getCommand(command: String): Command? {
//        return Companion.commands[command]
//    }
//
//    fun getCategories(): Collection<Category> {
//        return categories.values
//    }
//
//    /**
//     *
//     * @param event returned to be GuildMessageReceivedEvent
//     * @param prefix prefix command access
//     * @throws FileNotFoundException
//     */
//    @Throws(FileNotFoundException::class)
//    fun handleCommand(event: @Nullable MessageReceivedEvent?, prefix: String?) {
//        val split: Array<String> =
//            event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix).toRegex(), "").split("\\s+")
//                .toTypedArray()
//        val invoke = split[0].lowercase(Locale.getDefault())
//        val profile = ProfileBuilder(event.getAuthor(), event.getGuild())
//        if (Companion.commands.containsKey(invoke)) {
//            val args: List<String> = Arrays.asList<String>(*split).subList(1, split.size)
//            if (!Companion.commands[Companion.commands[invoke].getInVoke()].Lockdown() || event.getAuthor().getId()
//                    .equals("304609934967046144", ignoreCase = true)
//            ) {
//                if (profile.getBanned()) {
//                    event.getChannel()
//                        .sendMessage("⛔ | **You're currently blacklist**, `you can apply for unbanned application in our server`")
//                        .queue()
//                    return
//                }
//
////                if (Objects.requireNonNull(event.getMember()).getRoles().stream().anyMatch(f -> !f.hasPermission(commands.get(invoke).getPermission()))) {
////                    event.getChannel().sendMessage("⛔ | **You don't have permissions do use this command**").queue();
////                    return;
////                }
//                if (commandCooldown.containsKey(event.getAuthor())) {
//                    cooldownCount[event.getAuthor()].addAndGet(1)
//                    event.getChannel().sendMessage(
//                        MessageUtils(
//                            ":error: There's " + commandCooldown[event.getAuthor()] + " seconds to use commands again, `Note: if you spam more than " + cooldownCount[event.getAuthor()].get() + " times you will get banned` ||" + event.getAuthor()
//                                .getAsMention() + "||"
//                        ).EmojisHolder()
//                    ).queue()
//                }
//                if (cooldownCount.containsKey(event.getAuthor()) && cooldownCount[event.getAuthor()].get() >= 5) {
//                    val bannedUtils = BannedUtils(event.getAuthor(), event.getJDA().getSelfUser())
//                    if (profile.getAutoBanned() >= 1) {
//                        bannedUtils.setBannedWithTime(
//                            bannedUtils.getUser(),
//                            1,
//                            BannedElapsedTimes.HOURS,
//                            "Spamming : " + bannedUtils.getProfile().getProfileProperties().getProperty("auto-banned")
//                        )
//                    } else if (profile.getAutoBanned() >= 3) {
//                        bannedUtils.setBannedWithTime(
//                            bannedUtils.getUser(),
//                            1,
//                            BannedElapsedTimes.WEEKS,
//                            "Spamming : " + bannedUtils.getProfile().getProfileProperties().getProperty("auto-banned")
//                        )
//                    } else if (profile.getAutoBanned() >= 5) {
//                        bannedUtils.setBannedWithTime(
//                            bannedUtils.getUser(),
//                            1,
//                            BannedElapsedTimes.YEARS,
//                            "Spamming : " + bannedUtils.getProfile().getProfileProperties().getProperty("auto-banned")
//                        )
//                    } else {
//                        bannedUtils.setBannedWithTime(
//                            bannedUtils.getUser(),
//                            5,
//                            BannedElapsedTimes.MINUTE,
//                            "Spamming : " + bannedUtils.getProfile().getProfileProperties().getProperty("auto-banned")
//                        )
//                    }
//                    bannedUtils.getProfile().addAutoBanned(1)
//                    bannedUtils.getProfile().setBanned(true)
//                    commandCooldown.remove(event.getAuthor())
//                    cooldownCount.remove(event.getAuthor())
//                    return
//                }
//                if (!commandCooldown.containsKey(event.getAuthor())) {
//                    count = AtomicInteger(0)
//                    Companion.commands[invoke]!!.handle(args, event)
//                    commandCooldown[event.getAuthor()] = 5
//                    cooldownCount[event.getAuthor()] = count
//                }
//                if (!invoke.contains("profile")) {
//                    profile.setLastTimeCommandUse(Date())
//                }
//            } else {
//                event.getChannel()
//                    .sendMessage(event.getAuthor().getAsMention() + ", this command is locked \uD83D\uDD12").queue()
//            }
//        }
//    }
//
//    companion object {
//        var commandCooldown = HashMap<User, Int>()
//        var cooldownCount: HashMap<User, AtomicInteger?> = HashMap<User, AtomicInteger?>()
//        val commands: MutableMap<String, Command> = HashMap()
//    }
//
//    init {
//        // Add commands
//        addCommand(helpCommand(this))
//        addCommand(HypixelCommand())
//        addCommand(informationCommand())
//        addCommand(readCommand())
//        addCommand(rolesCommand())
//        addCommand(permissionChannelCommand())
//        addCommand(verifyCommand())
//        addCommand(animeCommand())
//        addCommand(donationCommand())
//        addCommand(rpcGame())
//        addCommand(eventsGame())
//        addCommand(rukoCommand())
//        addCommand(shopCommand())
//        addCommand(OptifineVersionsListCommand())
//        addCommand(MinecraftNameListCommand())
//        addCommand(SuggestCommand())
//        addCommand(SuggestModerator())
//        addCommand(CountriesCommand())
//        addCommand(SendPrivateMessageCommand())
//        addCommand(bannedCommand())
//        addCommand(profileCommand())
//        addCommand(reactionMessageCommand())
//        addCommand(testcommand())
//        addCommand(NekoCommand())
//        addCommand(HypixelGuildCommand())
//        addCommand(ServerProfileIDCanvas())
//        addCommand(HypixelGuildFutureCommands())
//        addCommand(AchievementsCommand())
//        addCommand(findAnimeCommand())
//        addCommand(shutdownCommand())
//        addCommand(pickoneCommand())
//        addCommand(replaceMessageKeys())
//
//        // Add category
//        addCategory(Category.MANAGEMENT)
//        addCategory(Category.MODERATOR)
//        addCategory(Category.INFORMATION)
//        addCategory(Category.FUN)
//        addCategory(Category.MINECRAFT)
//        addCategory(Category.NSFW)
//    }
//}