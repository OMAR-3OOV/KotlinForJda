package utilities.categoryUtil

import java.lang.NullPointerException

enum class Categories (val key: String,val displayName: String,val description: String,val icon: String,val id: Int) {

    FUN("fun", "Fun", "This category for having fun and chill using some fun commands!", "", 0),
    MANAGEMENT("management", "Management", "Management category made to be supported to your server!", "", 1),
    GAMES("games", "Games", "There is a server games that you can use to play with your friends", "", 2),
    INFORMATION("information", "Information", "You can have some information about your discord account or your server", "", 3),
    ;

    companion object {
        @JvmStatic
        fun getCategory(name: String): Categories {
            return Categories.valueOf(name.uppercase())
        }
    }
}