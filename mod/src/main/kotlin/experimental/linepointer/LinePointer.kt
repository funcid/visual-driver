package experimental.linepointer

import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.clientApi
import ru.cristalix.uiengine.element.Context3D
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import java.util.*
import kotlin.math.*

class LinePointer(
    var uuid: UUID,
    var color: Color,
    var location: V3,
    var limitRendering: Int = 25, // Дальность прорисовки элементов (в блоках)
    var texture: String,
    var origin: V3? = null,
) {

    companion object {
        const val ARROWS_OFFSET = 8.0
    }

    var contexts = arrayListOf<Context3D>()

    private fun addSection() {

        val context = Context3D(V3())

        val rectangle = rectangle {
            this.color = color
            size = V3(8.0, 8.0)

            align = CENTER
            origin = CENTER

            val parts = texture.split(":")
            textureLocation = clientApi.resourceManager().getLocation(parts[0], parts[1])
        }

        context.addChild(rectangle)

        contexts.add(context)

        draw()
    }

    private fun removeSection() {

        UIEngine.worldContexts.remove(contexts.last())
        contexts.removeLast()

        draw()
    }

    fun draw() {

        val priority = getPriorityLocation()

        val distanceX = location.x - priority.x
        val distanceY = location.y - priority.y
        val distanceZ = location.z - priority.z

        val totalDistance = sqrt(distanceX.pow(2) + distanceZ.pow(2) + distanceY.pow(2))

        val pointsCount = getPointsCount()

        val dX = distanceX / pointsCount
        val dY = distanceY / pointsCount
        val dZ = distanceZ / pointsCount

        // Чтобы первая стрелка не была слишком близко к игроку
        val offsetMultiplier = if (origin == null) if (totalDistance >= 2.0) 2.0 else totalDistance else 0.0

        contexts.forEachIndexed { index, context ->

            val pitch = atan2(
                priority.y - location.y,
                sqrt(
                    (priority.x - location.x).pow(2) +
                            (priority.z - location.z).pow(2)
                )
            )

            val yaw = atan2(distanceZ, distanceX)

            context.children.first().rotation = Rotation(-(pitch - Math.PI / 2), -1.0, 0.0, 0.0)

            // 90% от частоты обновления указателей
            context.animate(LineManager.UPDATE_PERIOD_MILLIS / 1000 * 0.9) {
                offset.x = (priority.x + offsetMultiplier * dX) + index * dX
                offset.y = (priority.y + offsetMultiplier * dY) + index * dY + 0.25
                offset.z = (priority.z + offsetMultiplier * dZ) + index * dZ
            }

            context.rotation = Rotation(yaw - Math.PI / 2, 0.0, -1.0, 0.0)

            if (!UIEngine.worldContexts.contains(context)) UIEngine.worldContexts.add(context)
        }

        changeOpacity()
    }

    fun rebase() {

        val pointsCount = getPointsCount()

        val limit = limitRendering * (16 / ARROWS_OFFSET) / 10
        var step = 0

        while (contexts.size != pointsCount && step <= limit) {

            if (pointsCount > contexts.size) {
                addSection()
            } else if (pointsCount - 1 < contexts.size) {
                removeSection()
            }

            step++
        }
    }

    fun update() {

        val parts = texture.split(":")

        contexts.forEach {
            it.children.first().apply {

                this.color = color
                (this as RectangleElement).textureLocation = clientApi.resourceManager().getLocation(parts[0], parts[1])
            }
        }
    }

    private fun changeOpacity() {

        when (origin == null) {

            true -> {

                val rendered = getRenderedElements()
                val alpha = 1.0 / rendered.size

                rendered.forEachIndexed { index, context ->

                    context.children.first().color.alpha = 1.0 - (index * alpha)
                }
            }

            false -> {

                val rendered = getElementsAtDistance()
                val repeatCount = floor(rendered.size / 2.0).toInt()

                val alpha = 0.75 / repeatCount

                repeat(repeatCount) {

                    contexts[it].children.first().color.alpha = 0.25 + it * alpha
                    contexts[rendered.size - 1 - it].children.first().color.alpha = 0.0 + it * alpha
                }
            }
        }
    }

    private fun getRenderedElements(): List<Context3D> {

        val rendered = arrayListOf<Context3D>()
        val limit = limitRendering * (16 / ARROWS_OFFSET)

        val count = getPointsCount()

        if (limit > count) {
            return contexts
        }

        contexts.forEachIndexed { index, context ->

            if (index + 1 <= limit) {

                if (!UIEngine.worldContexts.contains(context)) UIEngine.worldContexts.add(context)
                rendered.add(context)
            } else {
                if (UIEngine.worldContexts.contains(context)) UIEngine.worldContexts.remove(context)
            }
        }

        return rendered
    }

    private fun getElementsAtDistance(): List<Context3D> {

        val player = clientApi.minecraft().player
        val elements = arrayListOf<Context3D>()

        contexts.forEach {

            val distance = (it.offset.x - player.x).pow(2) + (it.offset.y - player.y).pow(2) + (it.offset.z - player.z).pow(2)

            if (distance <= limitRendering * limitRendering) {

                if (!UIEngine.worldContexts.contains(it)) UIEngine.worldContexts.add(it)
                elements.add(it)
            } else {
                if (UIEngine.worldContexts.contains(it)) UIEngine.worldContexts.remove(it)
            }
        }

        return elements
    }

    private fun getPriorityLocation(): V3 {
        val player = clientApi.minecraft().player
        return if (origin == null) V3(player.x, player.y, player.z) else origin!!
    }

    private fun getPointsCount(): Int {
        val priority = getPriorityLocation()
        return getPointsCount(priority.x, priority.y, priority.z)
    }

    private fun getPointsCount(x: Double, y: Double, z: Double): Int {

        val distanceX = location.x - x
        val distanceY = location.y - y - 0.25
        val distanceZ = location.z - z

        val distance = sqrt(distanceX.pow(2) + distanceY.pow(2) + distanceZ.pow(2))

        return (distance / (ARROWS_OFFSET / 16)).toInt()
    }

    fun remove() = UIEngine.worldContexts.removeAll(contexts)
}