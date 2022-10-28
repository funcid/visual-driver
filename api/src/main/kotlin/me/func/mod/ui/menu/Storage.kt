package me.func.mod.ui.menu

import me.func.mod.reactive.ReactiveButton

interface Storage : Openable {
    var title: String
    var info: String
    var storage: MutableList<ReactiveButton>

    fun add(button: ReactiveButton) { storage.add(button) }

    fun clear() { storage.clear() }

    fun buttons(vararg button: ReactiveButton) { storage = button.toMutableList() }

}

