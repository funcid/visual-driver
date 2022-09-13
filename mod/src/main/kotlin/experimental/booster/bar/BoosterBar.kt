package experimental.booster.bar

import com.google.gson.Gson
import dev.xdark.feder.NetUtil
import experimental.booster.bar.model.BoosterBarModel
import experimental.booster.bar.model.BoosterSegmentModel
import me.func.protocol.ui.booster.CloseBoosterRequest
import me.func.protocol.ui.booster.OpenBoosterRequest
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.floor

class BoosterBar {

    private val body = carved {
        carveSize = 2.0

        size = V3(500.0, 31.0)

        origin.x = CENTER.x
        origin.y = TOP.y

        align.x = CENTER.x
        origin.y = TOP.y

        offset.y = -35.0

        color = Color(0, 0, 0, 0.62)
    }

    private val titleElement = text {
        scale = V3(1.0, 1.0)

        origin = LEFT
        align = LEFT

        offset.y = 30.0
        offset.x = 10.0
    }

    private val subTitleElement = text {
        scale = V3(1.0, 1.0)

        origin = LEFT
        align = LEFT

        offset.y = 30.0
        offset.x = 60.0
    }

    private var boosterBar: BoosterBarModel? = null

    init {
        val gson = Gson()

        mod.registerChannel("boosterbar:open") {
            val json = NetUtil.readUtf8(this)
            val openBoosterRequest = gson.fromJson(json, OpenBoosterRequest::class.java)

            boosterBar = BoosterBarModel(
                segments = openBoosterRequest.segments
                    .map { BoosterSegmentModel(it) },
                title = openBoosterRequest.title,
                subtitle = openBoosterRequest.subtitle,
                isShowBackground = openBoosterRequest.isShowBackground,
                progress = openBoosterRequest.progress,

                titleElement = titleElement.also {
                    it.content = openBoosterRequest.title
                },
                subtitleElement = subTitleElement.also {
                    it.content = openBoosterRequest.subtitle
                },
                backgroundElement = body
            )

            createSegments(boosterBar!!)

            if (openBoosterRequest.isShowBackground) {
                body.offset.y = 15.0
                body.addChild(titleElement)
                body.addChild(subTitleElement)
            } else {
                body.animate(0.5, Easings.BACK_OUT) { offset.y = 15.0 }
            }

            UIEngine.overlayContext.addChild(body)
        }

        mod.registerChannel("boosterbar:close") {
            val json = NetUtil.readUtf8(this)

            val closeBoosterRequest = gson.fromJson(json, CloseBoosterRequest::class.java)

            if (closeBoosterRequest.isShowBackground) {
                body.offset.y = -30.0
            } else {
                body.animate(0.5, Easings.BACK_IN) { offset.y = -30.0 }
            }

            UIEngine.schedule(10.0) {
                UIEngine.overlayContext.removeChild(body)
            }
        }
    }

    private fun createSegments(boosterBar: BoosterBarModel) {
        val segments = boosterBar.segments.iterator()
        val amount = boosterBar.segments.size

        var parent = createSegment(
            true,
            segments.next().also {
                it.progress = calculateSegmentProgress(boosterBar.progress, amount, 0)
            }
        ).apply {
            body.addChild(this)
        }

        for (index in 0 until amount - 1) {
            val boosterRectangle = createSegment(
                false,
                segments.next().also {
                    it.progress = calculateSegmentProgress(boosterBar.progress, amount, index + 1)
                }
            )

            parent.addChild(boosterRectangle)
            parent = boosterRectangle
        }
    }

    private fun createSegment(isFirst: Boolean, segment: BoosterSegmentModel) = carved {
        check(segment.progress != null) { "Progress of the segment ${segment.label} is not set" }

        carveSize = 1.5
        size = V3(90.5, 15.0)
        color = Color(20, 98, 41, 0.62)

        if (!isFirst) {
            origin = LEFT
            align = RIGHT

            offset.x = 4.0
        } else {
            offset.x = 15.0
            offset.y = 8.0
        }

        +text {
            origin = CENTER
            align = CENTER

            content = segment.label

            offset.z = 50.0
            scale = V3(1.0, 1.0)
        }.also { segment.labelElement = it }

        +carved {
            carveSize = 1.5

            size = V3(90.5 * segment.progress!!, 15.0)
            color = Color(34, 174, 73, 0.62)

            if (segment.progress == 0.0) enabled = false

        }.also { segment.progressElement = it }
    }.also { segment.backgroundElement = it }

    private fun calculateSegmentProgress(progress: Double, amount: Int, index: Int): Double {
        val barPart = 1.0 / amount
        val fullBars = floor(progress / barPart).toInt()
        return if (index == fullBars) 1 / (barPart / (progress - fullBars * barPart)) else if (index < fullBars) 1.0 else 0.0
    }
}