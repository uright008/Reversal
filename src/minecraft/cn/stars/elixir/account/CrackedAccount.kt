package cn.stars.elixir.account

import cn.stars.elixir.compat.Session
import cn.stars.elixir.utils.set
import cn.stars.elixir.utils.string
import com.google.gson.JsonObject
import java.util.*

class CrackedAccount : MinecraftAccount("Cracked") {
    override var name = "Player"

    override val session: Session
        get() = Session(name, UUID.nameUUIDFromBytes(name.toByteArray(Charsets.UTF_8)).toString(), "-", "legacy")

    override fun update() {
        // has nothing to update with cracked account
    }

    override fun toRawJson(json: JsonObject) {
        json["name"] = name
    }

    override fun fromRawJson(json: JsonObject) {
        name = json.string("name")!!
    }
}
