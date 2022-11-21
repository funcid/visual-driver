package me.func.protocol.math

import kotlin.math.pow

object RadiusCheck {

    fun inRadius(radius: Double, origin: DoubleArray, target: DoubleArray) =
        (origin[0] - target[0]).pow(2) + (origin[1] - target[1]).pow(2) + (origin[2] - target[2]).pow(2) < radius * radius


}