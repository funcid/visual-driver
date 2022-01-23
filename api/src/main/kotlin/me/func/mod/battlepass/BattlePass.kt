package me.func.mod.battlepass

import com.mojang.brigadier.arguments.UuidArgumentType.uuid
import dev.xdark.feder.NetUtil
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.protocol.battlepass.BattlePassUserData
import org.bukkit.entity.Player
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

object BattlePass {

    val battlePasses = mutableMapOf<UUID, BattlePassData>()

    fun new(price: Int, data: BattlePassData.() -> Unit): BattlePassData {
        val battlepass = BattlePassData().apply(data)
        battlepass.facade.price = price
        battlePasses[battlepass.uuid] = battlepass
        return battlepass
    }

    @JvmStatic
    fun new(data: BattlePassData): BattlePassData {
        battlePasses[data.uuid] = data
        return data
    }

    fun buy(channel: String, consumer: BiConsumer<UUID, Player>) {
        Anime.createReader(channel) { player, buffer ->
            if (!player.isOnline)
                return@createReader

            consumer.accept(
                try {
                    UUID.fromString(NetUtil.readUtf8(buffer))
                } catch (exception: Exception) {
                    return@createReader
                }, player
            )
        }
    }

    init {
        buy("bp:buy-upgrade") { uuid, player -> battlePasses[uuid]?.buyAdvanced?.accept(player) }
        buy("bp:buy-page") { uuid, player -> battlePasses[uuid]?.buyPage?.accept(player) }
    }

    fun show(player: Player, uuid: UUID, data: BattlePassUserData) {
        battlePasses[uuid]?.let { battlepass ->
            val newQuests = battlepass.questStatusUpdater?.apply(player)
            if (!newQuests.isNullOrEmpty() && newQuests != battlepass.quests) {
                battlepass.quests = newQuests.toMutableList()
                ModTransfer(uuid.toString(), newQuests.size)
                    .apply { newQuests.forEach { line -> string(line) } }
                    .send("bp:quests", player)
            }

            ModTransfer(uuid.toString(), data.exp, data.advanced).send("bp:show", player)
        }
    }

    fun show(player: Player, battlePass: BattlePassData, data: BattlePassUserData) = show(player, battlePass.uuid, data)

    fun send(player: Player, uuid: UUID) {
        battlePasses[uuid]?.let {
            ModTransfer(uuid.toString(), it.facade.price, it.facade.salePercent, it.facade.tags.size)
                .apply {
                    it.facade.tags.forEach { line -> string(line) }
                    integer(it.pages.size)
                    it.pages.forEach { page ->
                        integer(page.requiredExp)
                        integer(page.items.size)
                        page.items.forEach { item -> item(item) }
                        integer(page.advancedItems.size)
                        page.advancedItems.forEach { item -> item(item) }
                        integer(page.skipPrice)
                    }
                    integer(it.quests.size)
                    it.quests.forEach { quest -> string(quest) }
                }.send("bp:send", player)
        }
    }

    fun send(player: Player, battlePass: BattlePassData) = send(player, battlePass.uuid)

    fun BattlePassData.sale(percent: Double) {
        facade.salePercent = percent
    }

    fun BattlePassData.onBuyAdvanced(accept: Consumer<Player>) {
        buyAdvanced = accept
    }

    fun BattlePassData.onBuyPage(accept: Consumer<Player>) {
        buyPage = accept
    }
}