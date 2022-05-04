package me.func.protocol.dialog

class Dialog(var entrypoints: List<Entrypoint>) {
    constructor(vararg entrypoint: Entrypoint) : this(entrypoint.toList())
}