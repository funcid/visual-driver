package me.func.protocol.packet

data class GreetingPackage(
    var password: String? = null,
    var serverName: String? = null
) : DataPackage()