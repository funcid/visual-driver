package me.func.mod.menu

interface Storage : Openable {
    var title: String
    var storage: MutableList<ReactiveButton>

    fun add(button: ReactiveButton) { storage.add(button) }

    fun clear() { storage.clear() }

    fun buttons(vararg button: ReactiveButton) { storage = button.toMutableList() }

}

