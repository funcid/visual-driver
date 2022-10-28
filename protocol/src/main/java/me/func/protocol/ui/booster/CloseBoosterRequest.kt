package me.func.protocol.ui.booster

data class CloseBoosterRequest(var isShowBackground: Boolean) {
    constructor() : this(false)
}
