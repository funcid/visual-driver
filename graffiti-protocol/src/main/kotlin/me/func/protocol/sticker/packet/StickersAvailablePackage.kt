package me.func.protocol.sticker.packet

import me.func.protocol.sticker.Sticker
import ru.cristalix.core.network.CorePackage

class StickersAvailablePackage : CorePackage() {
    var list: MutableList<Sticker> = mutableListOf()
}