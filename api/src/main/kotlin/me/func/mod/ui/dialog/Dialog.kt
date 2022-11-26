package me.func.mod.ui.dialog

import me.func.mod.conversation.ModTransfer
import me.func.mod.conversation.broadcast.PlayerSubscriber
import me.func.mod.util.command
import me.func.protocol.ui.dialog.Dialog
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object Dialog : PlayerSubscriber {

    private val opened = hashMapOf<UUID, Dialog>()

    init {
        command("dialog-callback") { player, args ->
            if (!opened.containsKey(player.uniqueId)) return@command
            val openedDialog = opened[player.uniqueId]
            openedDialog?.let {
                it.entrypoints.forEach { entry ->
                    entry.screen.buttons?.forEach { button ->
                        button.actions
                            ?.filter { action -> action.custom != null && action.command?.endsWith(args[0]) == true }
                            ?.forEach { action -> action.custom?.run() }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun dialog(player: Player, dialog: Dialog, openEntrypoint: String) {
        sendDialog(player, dialog)
        openDialog(player, openEntrypoint)
    }

    @JvmStatic
    fun sendDialog(player: Player, dialog: Dialog) {
        opened[player.uniqueId] = dialog
        ModTransfer()
            .string("load")
            .json(dialog)
            .send("rise:dialog-screen", player)
    }

    @JvmStatic
    fun openDialog(player: Player, dialogId: String) = ModTransfer()
        .string("open")
        .string(dialogId)
        .send("rise:dialog-screen", player)

    override val isConstant = true

    override fun removeSubscriber(player: Player) { opened.remove(player.uniqueId) }

    override fun getSubscribersCount() = opened.size
    override val uuid: UUID = UUID.randomUUID()

}