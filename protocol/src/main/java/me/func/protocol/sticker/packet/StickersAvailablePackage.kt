package me.func.protocol.sticker.packet

import me.func.protocol.packet.DataPackage
import me.func.protocol.sticker.Sticker

class StickersAvailablePackage : DataPackage() {
    var list: MutableList<Sticker> = mutableListOf()
}