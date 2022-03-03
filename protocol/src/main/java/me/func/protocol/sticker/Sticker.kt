package me.func.protocol.sticker

import me.func.protocol.DropRare
import me.func.protocol.Unique
// import me.func.protocol.util.UUIDSerializer
import java.util.UUID

/**
 * Created by Kamillaova on 14.02.2022
 */
data class Sticker(
    override val uuid: UUID,
    val name: String,
    val rare: DropRare,
    val openTime: Long
) : Unique
