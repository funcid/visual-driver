package me.func.mod.service

import org.bukkit.Bukkit
import ru.cristalix.core.CoreCredentials
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.SocketClient
import ru.cristalix.core.realm.RealmId
import java.lang.System.getenv

object Services {
    internal val socketClient: ISocketClient =
        ISocketClient.get() ?: SocketClient(Bukkit.getLogger()).apply {
            connect(
                getenv("TOWER_IP"),
                getenv("TOWER_PORT").toInt(),
                CoreCredentials::class.java.getDeclaredConstructor(
                    RealmId::class.java,
                    String::class.java,
                    String::class.java
                ).apply { isAccessible = true }.newInstance(
                    RealmId.of("ANIM", 600 + (Math.random() * 300).toInt()),
                    getenv("TOWER_LOGIN"),
                    getenv("TOWER_PASSWORD")
                )
            )
            waitForHandshake()
        }
}
