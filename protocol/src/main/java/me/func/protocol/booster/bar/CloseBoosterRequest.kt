package me.func.protocol.booster.bar

import java.util.*

data class CloseBoosterRequest(var isShowBackground: Boolean) {
    constructor() : this(false)
}
