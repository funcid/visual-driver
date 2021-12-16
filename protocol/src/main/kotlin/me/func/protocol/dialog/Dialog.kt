package me.func.protocol.dialog

data class Dialog(var entrypoints: List<Entrypoint>) {
    constructor(vararg entrypoint: Entrypoint) : this(entrypoint.toList())
}