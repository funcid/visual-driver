package me.func.protocol.packet

import java.util.*

abstract class DataPackage {
    val id = UUID.randomUUID().toString()
}
