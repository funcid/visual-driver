package model

import kotlin.reflect.KProperty

abstract class Lazy<out T>(open val initializer: () -> T) {

    var value: @UnsafeVariance T? = null

    operator fun getValue(data: Any, property: KProperty<*>): T {
        if (value == null) value = initializer.invoke()
        return value!!
    }
}