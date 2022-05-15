package utilities.staffUtil

import net.dv8tion.jda.api.entities.User
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

data class RolesData(val user: User, val role: Roles) {

    private val file = File("System/roles.yml")
    private val config = YamlFile(file)

    init {
        newData()

        if (!isUserExist()) {
            createNewDataForUser()
        }

    }

    fun newData() {
        if (!file.exists()) {
            file.createNewFile()

            Roles.values().forEach { role ->
                val list = java.util.ArrayList<String>()
                config.set(role.key, list)
                println("${role.key} Role has been made!")
            }

            config.save()
        } else {
            Roles.values().forEach { role ->
                if (config.getStringList(role.key) == null) {
                    val list = java.util.ArrayList<String>()
                    config.set(role.key, list)
                    println("${role.key} Role has been created!")

                    config.save()
                }
            }
        }

        config.load(file)
    }

    fun createNewDataForUser() {
        Roles.values().forEach { role ->
            if (role.key == this.role.key && !config.getStringList(role.key).contains(user.id)) {
                println(config.getStringList(role.key))
                val roleList = Arrays.asList(config.getStringList(role.key).stream().filter { f -> f.isNotEmpty() || f.isNotBlank() }.toList(), user.id)

                config.set(role.key, roleList)

                println("${user.name} has been added to ${role.displayName}!")
                config.save()
                println(config.getStringList(role.key))
            }
        }
    }

    fun createNewDataForUser(user: User) {
        Roles.values().forEach { role ->
            if (role.key == this.role.key && !config.getStringList(role.key).contains(user.id)) {
                val roleList = ArrayList<String>(config.getStringList(role.key))
                roleList.add(user.id)

                config.set(role.key, roleList)

                println("${user.name} has been added to ${role.displayName}!")
                config.save()
            }
        }
    }

    fun isUserExist(): Boolean {
        Roles.values().forEach { role ->
            if (config.isList(role.key)) {
                return config.getList(role.key).contains(user.id)
            }
        }

        return false
    }

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

//    fun config(): JsonConfiguration {
//        return JsonConfiguration.loadConfiguration(file)
//    }
}
