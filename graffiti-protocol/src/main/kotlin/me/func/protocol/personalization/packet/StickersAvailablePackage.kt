package me.func.protocol.personalization.packet

import me.func.protocol.personalization.Sticker
import ru.cristalix.core.network.CorePackage

class StickersAvailablePackage : CorePackage() {
    var list: MutableList<Sticker> = mutableListOf()
}