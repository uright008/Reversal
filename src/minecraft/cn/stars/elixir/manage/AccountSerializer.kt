package cn.stars.elixir.manage

import cn.stars.elixir.account.CrackedAccount
import cn.stars.elixir.account.MicrosoftAccount
import cn.stars.elixir.account.MinecraftAccount
import cn.stars.elixir.account.MojangAccount
import cn.stars.elixir.utils.set
import cn.stars.elixir.utils.string
import com.google.gson.JsonObject

object AccountSerializer {
    /**
     * write [account] to [JsonObject]
     */
    fun toJson(account: MinecraftAccount): JsonObject {
        val json = JsonObject()
        account.toRawJson(json)
        json["type"] = account.javaClass.canonicalName
        return json
    }

    /**
     * read [MinecraftAccount] from [json]
     */
    fun fromJson(json: JsonObject): MinecraftAccount {
        val account = Class.forName(json.string("type")!!).newInstance() as MinecraftAccount
        account.fromRawJson(json)
        return account
    }

    /**
     * get an instance of [MinecraftAccount] from [name] and [password]
     */
    fun accountInstance(name: String, password: String): MinecraftAccount {
        return if (name.startsWith("ms@")) {
            val realName = name.substring(3)
            if(password.isEmpty()) {
                MicrosoftAccount.buildFromAuthCode(realName, MicrosoftAccount.AuthMethod.MICROSOFT)
            } else {
                MicrosoftAccount.buildFromPassword(realName, password)
            }
        } else if(password.isEmpty()) {
            CrackedAccount().also { it.name = name }
        } else {
            MojangAccount().also { it.name = name; it.password = password }
        }
    }
}