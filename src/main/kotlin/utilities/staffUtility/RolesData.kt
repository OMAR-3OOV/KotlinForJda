package utilities.staffUtility

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
     * Load the [config] system and create if the file not created yet!
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

    /**
     * This method will create the sections for the roles, so it will be existing but empty
     * Data getting from [Roles]
     */
    fun createNewDataForRoles() {
        Roles.values().forEach { role ->
            if (config.getStringList(role.key).isNullOrEmpty()) {
                config.createSection(role.key)
                config.save()
            }
        }
    }

    /**
     * This method will create a new [user] data & add the user to Default selection [Roles.EVERYONE]
     */
    fun createNewDataForUser() {
        if (this.user.isBot) return
        if (this.user.isSystem) return
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
     * Create new specific data user , till now it's useless, but maybe in future it going to be useful with upgrades
     *
     * @param user related to specific user the going to be mentioned when the method get use.
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
     * Add the [user] to the role section
     */
    fun addRole(role: Roles) {
        val retrieve = ArrayList<String>(this.config.getStringList(role.key))
        val usersList = ArrayList<String>()
        usersList.addAll(retrieve)
        usersList.add(this.user.id)

        config.set(role.key, usersList)
        config.save()
    }

    /**
     * remove [user] from role section
     */
    fun removeRole(role: Roles) {
        val retrieve = ArrayList<String>(this.config.getStringList(role.key))
        retrieve.remove(this.user.id)

        config.set(role.key, retrieve)
        config.save()
    }

    /**
     * [user] data checker!
     */
    private fun isUserExist(): Boolean {
        for (role in Roles.values()) {
            if (!config.getStringList(role.key).contains(this.user.id)) continue else {
                return true
            }
        }
        return false
    }

    /**
     * [user] data getter
     */
    fun getUserRole(): Roles {
        if (isUserExist()) {
            for (role in Roles.values()) {
                if (config.isList(role.key) && config.getStringList(role.key).contains(this.user.id)) {
                    return role
                }
            }
        }
        return Roles.EVERYONE
    }

    /**
     * related to the [config]
     */
    fun config(): YamlFile {
        return config
    }
}
