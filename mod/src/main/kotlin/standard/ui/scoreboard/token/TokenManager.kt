package standard.ui.scoreboard.token

import dev.xdark.clientapi.event.lifecycle.GameLoop
import readColoredUtf8
import readUuid
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.registerHandler
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.element.AbstractElement
import ru.cristalix.uiengine.element.CarvedRectangle
import ru.cristalix.uiengine.utility.*
import java.util.UUID
import kotlin.math.max

object TokenManager {

    private const val padding = 4.0

    val fullWidth = 146.0 / 2
    val block = 14.0
    val textBlock = 12.0

    private val flex = flex {

        flexDirection = FlexDirection.DOWN
        flexSpacing = padding * 4
    }

    private val hint = text {

        color = WHITE
        content = "cristalix.gg"
        align = BOTTOM
        origin = BOTTOM
    }

    private val container = rectangle {
        align = RIGHT
        origin = RIGHT
        offset.x -= padding * 3

        beforeRender {
            size.y = flex.size.y + hint.lineHeight + padding
        }

        addChild(flex, hint)
    }

    data class Token(val uuid: UUID) : CarvedRectangle() {

        var title: String = "Название"
            set(value) {
                titleElement.content = value
                field = value
            }
        var description: String = "Описание"
            set(value) {
                descriptionElement.content = value
                field = value
            }

        private val titleElement = text {
            content = title
            shadow = true
        }
        private val descriptionElement = text { content = description }

        init {
            super.size = V3(fullWidth, block)
            super.color = Color(0, 0, 0, .62)

            addChild(titleElement.apply {

                align = TOP
                origin = TOP
                offset.y -= textBlock
            }, descriptionElement.apply {

                align = LEFT
                origin = LEFT
                offset.x += padding
            })
        }
    }

    init {

        fun updateWidth() {

            val longest = flex.children
                .filterIsInstance<Token>()
                .maxByOrNull { max(it.description.length, it.title.length) } ?: return

            val word = if (longest.description.length > longest.title.length)
                longest.description else longest.title

            val width = max(fullWidth, UIEngine.clientApi.fontRenderer().getStringWidth(word) + 2 * padding)

            flex.children.forEach { it.size.x = width }
            container.size.x = width
        }

        mod.registerChannel("token:add") {

            repeat(readInt()) {
                flex.addChild(Token(readUuid()))
            }

            updateWidth()
        }

        fun remove(tokens: List<AbstractElement>) = flex.removeChild(*tokens.toTypedArray())

        mod.registerChannel("token:remove-uuid") {

            val uuid = readUuid()
            val tokenToRemove = flex.children.filterIsInstance<Token>().filter { it.uuid == uuid }

            remove(tokenToRemove)
        }

        mod.registerChannel("token:update") {

            val uuid = readUuid()
            val tokenToUpdate = flex.children.filterIsInstance<Token>().find { it.uuid == uuid }

            tokenToUpdate?.title = readColoredUtf8()
            tokenToUpdate?.description = readColoredUtf8()

            updateWidth()
        }

        registerHandler<GameLoop> {
            hint.enabled = flex.children.size > 1
        }

        UIEngine.overlayContext.addChild(container)
    }

}