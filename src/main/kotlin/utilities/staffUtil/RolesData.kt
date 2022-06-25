package utilities.staffUtil

import net.dv8tion.jda.api.entities.User
import org.simpleyaml.configuration.file.YamlFile
import java.io.File

data class RolesData(val user: User) {

    private val file = File("System/roles.yml")
    private val config = YamlFile(file)

    /**
     * Role is default as [Roles.EVERYONE] , otherwise, you can change it by using setRole(Role) method
     */
    private var role: Roles = Roles.EVERYONE

    init {
        loadConfig()

        if (!isUserExist()) {
            createNewDataForUser()
        }

    }

    /**
     * Load the config system
     */
    private fun loadConfig() {
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
            createNewDataForRoles()
            config.load(file)
        } else {
            config.load(file)
        }
    }

    fun getRole(): Roles {
        return this.role
    }

    fun setRole(role: Roles) {
        this.role = role
    }

    fun createNewDataForRoles() {
        Roles.values().forEach { role ->
            if (config.getStringList(role.key).isNullOrEmpty()) {
                config.createSection(role.key)
                config.save()
            }
        }
    }

    /**
     * Creating data user automatically!
     */
    fun createNewDataForUser() {
        Roles.values().forEach { role ->
            val retrieve = ArrayList<String>(config.getStringList(role.key))

            if (role.key == this.role.key && !config.getStringList(role.key).contains(user.id) && !user.id.equals("304609934967046144")) {
                val usersList = ArrayList<String>()
                usersList.addAll(retrieve)
                usersList.add(user.id)

                config.set(role.key, usersList)

                println("${user.name} has been added to ${role.displayName}!")
                config.save()
            } else if (user.id.equals("304609934967046144") && role.key.equals("admin") && !config.getStringList(role.key).contains(user.id)) {
                val usersList = ArrayList<String>()
                usersList.addAll(retrieve)
                usersList.add(user.id)

                config.set(role.key, usersList)

                println("${user.name} has been added to ${role.displayName}! ( Administration )")
                config.save()
            }
        }
    }

    /**
     * return User so specific user, it used when there is error in the system so user can make his own data later
     */
    fun createNewDataForUser(user: User) {
        val retrieve = ArrayList<String>(config.getStringList(this.role!!.key))

        Roles.values().forEach { role ->
            if (role.key == this.role.key && !config.getStringList(role.key).contains(user.id)) {
                val usersList = ArrayList<String>()
                usersList.addAll(retrieve)
                usersList.add(user.id)

                config.set(role.key, usersList)

                println("${user.name} has been added to ${role.displayName}!") // System notification
                config.save()
            }
        }
    }

    /**
     * User data checker!
     */
    private fun isUserExist(): Boolean {
        Roles.values().forEach { role ->
            if (config.isList(role.key)) {
                return config.getList(role.key).contains(user.id)
            }
        }

        return false
    }

    /**
     * User data getter
     */
    fun getUserRole(): Roles {
        if (isUserExist()) {
            Roles.values().forEach { role ->
                if (config.isList(role.key) && config.getList(role.key).contains(user.id)) {
                    return role
                }
            }
        }
        return Roles.EVERYONE
    }

    fun getConfig(): YamlFile {
        return config
    }
}
