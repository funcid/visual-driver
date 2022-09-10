package me.func.mod.dialog

import me.func.mod.conversation.ModTransfer
import me.func.mod.util.command
import me.func.protocol.dialog.Dialog
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object Dialog : Listener {

    private val opened = hashMapOf<UUID, Dialog>()

    @EventHandler
    fun PlayerQuitEvent.handle() {
        opened.remove(player.uniqueId)
    }

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

}