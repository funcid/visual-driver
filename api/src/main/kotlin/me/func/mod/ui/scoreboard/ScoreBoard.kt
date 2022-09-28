package me.func.mod.ui.scoreboard

import me.func.mod.Anime
import me.func.mod.ui.scoreboard.raw.RawConstant
import me.func.mod.ui.scoreboard.raw.RawDynamic
import me.func.mod.ui.scoreboard.raw.RawEmpty
import me.func.mod.util.listener
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.function.Function

object ScoreBoard : Listener {

    private val list = arrayListOf<String>()
    private val schemes = hashMapOf<String, ScoreBoardScheme>()
    private val subscribers = hashMapOf<String, MutableList<Player>>()

    init {
        listener(this)

        Bukkit.getScheduler().runTaskTimer(Anime.provided, {
            list.forEach { key ->
                val data = schemes[key] ?: return@forEach
                val subscribers = subscribers[key] ?: return@forEach

                data.update(subscribers)
            }
        }, 10, 20)
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        unsubscribe(player)
    }

    @JvmStatic
    fun unsubscribe(player: Player) {
        subscribers.forEach {
                (_, players) -> players.remove(player)
        }
    }

    @JvmStatic
    fun hide(player: Player) = Anime.sendEmptyBuffer("func:scoreboard-remove", player)

    @JvmStatic
    fun subscribe(key: String, player: Iterable<Player>) = subscribe(key, *player.toList().toTypedArray())

    @JvmStatic
    fun subscribe(key: String, vararg players: Player) {

        val data = get(key) ?: return

        subscribers[data.key]?.addAll(players)
        players.forEach { data.bind(it) }
        data.update(*players)
    }

    @JvmStatic
    fun remove(key: String) {
        schemes.remove(key)
        subscribers.remove(key)
        list.remove(key)
    }

    @JvmStatic
    fun get(key: String): ScoreBoardScheme? {
        return schemes[key]
    }

    @JvmStatic
    fun builder() = Builder()

    class Builder(private val scheme: ScoreBoardScheme = ScoreBoardScheme()) {

        fun key(key: String) = apply { scheme.key = key }
        fun header(header: String) = apply { scheme.header = header }
        fun footer(footer: String) = apply { scheme.footer = footer }
        fun empty() = apply { scheme.content.add(RawEmpty) }
        fun line(left: String) = apply { scheme.content.add(RawConstant(left, "")) }
        fun line(left: String, right: String) = apply { scheme.content.add(RawConstant(left, right)) }
        fun <T, S> dynamic(accept: Function<Player, ScoreBoardRecord<T, S>>) =
            apply { scheme.content.add(RawDynamic(accept)) }

        fun <S> dynamic(left: String, accept: Function<Player, S>) = apply {
            scheme.content.add(RawDynamic { player ->
                ScoreBoardRecord(
                    left,
                    accept.apply(player)
                )
            })
        }

        fun build(): ScoreBoardScheme {
            schemes[scheme.key] = scheme
            subscribers[scheme.key] = arrayListOf()
            list.add(scheme.key)
            return scheme
        }
    }

}