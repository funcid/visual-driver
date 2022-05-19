package me.func.mod.selection

interface Storage : Openable {

    var title: String
    var storage: MutableList<Button>

    fun add(button: Button) { storage.add(button) }

    fun clear() { storage.clear() }

    fun buttons(vararg button: Button) { storage = button.toMutableList() }

}