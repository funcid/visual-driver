package me.func.protocol.ui.dialog

class Dialog(var entrypoints: List<Entrypoint>) {
    constructor(vararg entrypoint: Entrypoint) : this(entrypoint.toList())
}