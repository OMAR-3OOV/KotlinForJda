package utilities.staffUtility

import java.awt.Color

/**
 * The roles that only special users has it to access some private commands
 */
enum class Roles(val key: String,open val displayName: String,open val color: Color,val id: Int) {

    ADMIN("admin", "Admin",  Color(150, 0, 0), 0),
    CEO("ceo", "CEO",  Color(0, 150, 170), 1),
    MOD("mod", "Mod",  Color(0, 150, 0), 2),
    EVERYONE("everyone", "Everyone", Color(150, 150, 150), 3),
    ;

    fun getRoleByKey(key: String): Roles {
        return values().first { role -> role.key == key }
    }
}