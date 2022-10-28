package model

import ru.cristalix.uiengine.element.AbstractElement

class LazyElement<out T>(
    override val initializer: () -> T,
) : Lazy<T>(initializer) where T : AbstractElement