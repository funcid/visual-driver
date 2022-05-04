package me.func.protocol.personalization

import me.func.protocol.DropRare
import me.func.protocol.Unique
import java.util.UUID

class Sticker(
    override val uuid: UUID,
    val name: String,
    val rare: DropRare,
    val openTime: Long
) : Unique
