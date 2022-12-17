package experimental.linepointer

import asColor
import me.func.protocol.data.color.RGB
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
    var rgb: RGB,
    var location: V3,
    var limitRendering: Int = 25, // Дальность прорисовки элементов (в блоках)
    var texture: String,
    var origin: V3? = null,
) {

    companion object {
        private const val OFFSET_Y = 1.2
        const val ARROWS_OFFSET = 8.0

        private const val MAX_ANIMATION_STEPS = 6.0
        private const val ELEMENT_PARTITION = 10
    }

    private fun sinusoid(x: Int) = OFFSET_Y - sin((x - PI * animationStep) / 3) * OFFSET_Y

    private var animationStep = 0.0
    var contexts = arrayListOf<Context3D>()

    private fun addSection() {

        val context = Context3D(V3())

        val rectangle = rectangle {
            color = rgb.asColor()
            size = V3(8.0, 8.0)

            align = CENTER
            origin = CENTER

            val parts = texture.split(":")
            textureLocation = clientApi.resourceManager().getLocation(parts[0], parts[1])
        }

        context.addChild(rectangle)

        contexts.add(context)

        UIEngine.worldContexts.add(context)
    }

    private fun removeSection() {

        UIEngine.worldContexts.remove(contexts.last())
        contexts.removeLast()
    }

    fun draw() {

        val priority = getPriorityLocation()

        val distanceX = location.x - priority.x
        val distanceY = location.y - priority.y
        val distanceZ = location.z - priority.z

        val totalDistance = distanceX.pow(2) + distanceZ.pow(2) + distanceY.pow(2)

        val pointsCount = getPointsCount()

        val dX = distanceX / pointsCount
        val dY = distanceY / pointsCount
        val dZ = distanceZ / pointsCount

        // Чтобы первая стрелка не была слишком близко к игроку
        val offsetMultiplier = if (origin == null) if (totalDistance >= 4.0) 2.0 else sqrt(totalDistance) else 0.0

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

            context.animate(LineManager.UPDATE_PERIOD_MILLIS / 1000.0) {
                offset.x = (priority.x + offsetMultiplier * dX) + index * dX
                offset.y = (priority.y + offsetMultiplier * dY) + index * dY + 0.25
                offset.z = (priority.z + offsetMultiplier * dZ) + index * dZ
            }

            context.rotation = Rotation(yaw - Math.PI / 2, 0.0, -1.0, 0.0)
        }

        changeOpacity()
    }

    fun animation() {

        if (animationStep > MAX_ANIMATION_STEPS) {
            animationStep = 0.0
        }

        contexts.filter { it.enabled }.forEachIndexed { index, context ->

            context.children.first().animate(LineManager.UPDATE_PERIOD_MILLIS / 1000.0) {
                offset.y = sinusoid(index)
            }
        }

        animationStep += 0.5
    }

    fun rebase(limit: Int = limitRendering * (16 / ARROWS_OFFSET).toInt() / ELEMENT_PARTITION) {

        val pointsCount = getPointsCount()

        var step = 0
        while (contexts.size != pointsCount && step <= limit) {

            if (pointsCount > contexts.size && contexts.size < limit * if (origin == null) ELEMENT_PARTITION else contexts.size + 1) {
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

                color = rgb.asColor()
                (this as RectangleElement).textureLocation = clientApi.resourceManager().getLocation(parts[0], parts[1])
            }
        }
    }

    fun changeVisibility() {

        val player = clientApi.minecraft().player

        if (origin != null) {

            contexts.forEach {
                val distance = (it.offset.x - player.x).pow(2) + (it.offset.z - player.z).pow(2)

                it.enabled = distance <= limitRendering * limitRendering
            }
        }
    }

    private fun changeOpacity() {

        if (origin == null) {

            val rendered = getRenderedElements()
            val alpha = 1.0 / rendered.size

            rendered.forEachIndexed { index, context ->
                context.children.first().color.alpha = 1.0 - index * alpha
            }
        }
    }

    private fun getRenderedElements(): List<Context3D> {

        val limit = limitRendering * (16 / ARROWS_OFFSET).toInt()

        val count = getPointsCount()

        if (limit > count) {
            return contexts
        }

        return contexts.filterIndexed { index, _ -> index + 1 <= limit }
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