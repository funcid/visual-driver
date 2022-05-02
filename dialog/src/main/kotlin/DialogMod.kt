import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.xdark.clientapi.event.input.KeyPress
import dev.xdark.clientapi.event.input.MousePress
import dev.xdark.clientapi.event.lifecycle.GameLoop
import dev.xdark.clientapi.event.render.*
import dev.xdark.clientapi.event.window.WindowResize
import me.func.protocol.dialog.*
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import ru.cristalix.clientapi.KotlinMod
import ru.cristalix.clientapi.readUtf8
import ru.cristalix.uiengine.UIEngine
import ru.cristalix.uiengine.UIEngine.overlayContext
import ru.cristalix.uiengine.element.RectangleElement
import ru.cristalix.uiengine.element.TextElement
import ru.cristalix.uiengine.eventloop.animate
import ru.cristalix.uiengine.utility.*
import kotlin.math.sign

open class DialogMod : KotlinMod() {

    lateinit var buttons: RectangleElement
    lateinit var buttonCursor: RectangleElement
    lateinit var buttonsBG: RectangleElement

    lateinit var buttonPart: RectangleElement
    lateinit var npcTitle: TextElement
    lateinit var npcSubtitle: TextElement
    lateinit var npcDialog: RectangleElement

    lateinit var npcPart: RectangleElement
    lateinit var dialogBG: RectangleElement

    private var pickedItem = -1
    private var entrypoints: ArrayList<Entrypoint> = arrayListOf()

    private var visible = false
    private lateinit var entrypoint: Entrypoint
    private lateinit var screen: Screen
    private var history: ArrayList<Screen> = arrayListOf()

    private var yMargin = 2.34
    private var windowWidth: Double = 0.0
    private var windowHeight: Double = 0.0
    private val buttonHeight = 15
    private var dialogRunning = false
    private var maxWidth = 0
    private val buttonHotkeyNames: Array<Char> = arrayOf('G', 'Y', 'Z', 'X')
    private val buttonHotkeys: Array<Int> = arrayOf(Keyboard.KEY_G, Keyboard.KEY_Y, Keyboard.KEY_Z, Keyboard.KEY_X)

    override fun onEnable() {
        UIEngine.initialize(clientApi)

        buttons = rectangle { }
        buttonCursor = rectangle {
            offset = V3(x = 1.0)
            size = V3(y = 15.0)
            color = Color(42, 102, 189, 1.0)
        }
        buttonsBG = rectangle {
            offset = V3(x = 1.0)
        }

        buttonPart = rectangle {
            align = Relative.RIGHT
            addChild(buttonCursor, buttonsBG, buttons)
        }
        npcTitle = text {
            content = "NPC TITLE"
            origin = Relative.TOP
            align = Relative.TOP
            scale = V3(1.4, 1.4)
            color = Color(255, 195, 0, 1.0)
            offset = V3(y = yMargin * 3)
        }
        npcSubtitle = text {
            content = ""
            origin = Relative.TOP
            align = Relative.TOP
            scale = V3(0.9, 0.9)
            color = Color(213, 202, 141, 1.0)
            offset = V3(y = yMargin * 9)
        }

        npcDialog = rectangle {
            origin = Relative.TOP_LEFT
            align = Relative.TOP_LEFT
            offset = V3(y = 32.0)
        }

        npcPart = rectangle {
            size = V3(windowWidth, 55.0)
            color = Color(0, 0, 0, 0.62)
            offset = V3(y = 10.0)
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            addChild(npcTitle, npcSubtitle, npcDialog)
        }

        dialogBG = rectangle {
            size = V3(windowWidth, 0.33)
            color = Color(0, 0, 0, 0.0)
            enabled = visible
            origin = Relative.BOTTOM
            align = Relative.BOTTOM
            addChild(npcPart, buttonPart)
        }

        overlayContext.addChild(dialogBG)

        fun buttonMessage(message: String, index: Int) = buttonHotkeyNames[index] + "§f | " + message

        fun getMaxStrWidth(): Int {
            var width: Int
            for ((buttonInd, _) in buttons.children.withIndex()) {
                width =
                    clientApi.fontRenderer().getStringWidth(buttonMessage(screen.buttons!![buttonInd].text, buttonInd))
                if (width > maxWidth) maxWidth = width
            }
            return maxWidth
        }

        fun shiftButtonCursor(delta: Int) {
            pickedItem += delta
            if (pickedItem < 0) pickedItem = screen.buttons!!.size - 1
            if (pickedItem >= screen.buttons!!.size) pickedItem = 0

            for ((buttonIndex, button) in screen.buttons!!.withIndex()) {
                (buttons.children[buttonIndex] as TextElement).content = buttonMessage(button.text, buttonIndex)
                (buttons.children[buttonIndex] as TextElement).offset.x = (maxWidth - UIEngine.clientApi.fontRenderer()
                    .getStringWidth(buttonMessage(screen.buttons!![buttonIndex].text, buttonIndex))) / 2 + 0.2
                buttonsBG.children[buttonIndex].enabled = true
            }
            buttonCursor.animate(0.3, Easings.CUBIC_OUT) {
                offset.y = pickedItem * buttonHeight + 5.0 * pickedItem
            }
            buttonsBG.children[pickedItem].enabled = false
            buttonCursor.size.x = maxWidth + 19.0
        }

        fun resolve() {
            dialogRunning = false
            npcPart.animate(0, Easings.NONE) {
                rotation.z = 0.0
            }
            for (child in npcDialog.children) {
                child.animate(-1, Easings.NONE) {
                    rotation.z = 0.0
                }
                child.scale = V3(1.0, 1.0)
            }
            buttonPart.animate(0.3, Easings.CUBIC_OUT) { align.x = 0.5 }
            buttonCursor.animate(0.1, Easings.NONE) { offset.x = -10.0 }
            buttonsBG.animate(0.1, Easings.NONE) { offset.x = -10.0 }
        }

        fun close(target: Entrypoint) {
            buttonPart.align.x = 0.0
            buttonCursor.offset.x = 1.0
            buttonsBG.offset.x = 0.0
            entrypoint = target
            visible = false
            pickedItem = -1
            npcPart.size.y = 0.0
            buttonPart.align.x = 1.0
        }

        fun openEntrypoint(target: Entrypoint) {
            maxWidth = 0
            entrypoint = target
            history = arrayListOf()
            screen = target.screen
            history.add(screen)
            visible = true
            pickedItem = 0
        }

        fun openScreen(target: Screen) {
            maxWidth = 0
            history.add(target)
            screen = target
            visible = true
            pickedItem = 0
        }

        fun activateButton(button: Button) {
            button.actions?.forEach {
                when (it.type) {
                    "command" -> {
                        clientApi.chat().sendChatMessage(it.command!!)
                        close(entrypoint)
                    }
                    "open_screen" -> openScreen(it.screen!!)
                    "previous_screen" -> {
                        history.removeLast()
                        val screen = history.last()
                        openScreen(screen)
                    }
                    "close" -> close(entrypoint)
                }
            }
        }

        fun update() {
            windowWidth = UIEngine.clientApi.resolution().scaledWidth_double
            windowHeight = UIEngine.clientApi.resolution().scaledHeight_double

            npcDialog.size.x = windowWidth
            npcPart.size.x = windowWidth
            dialogBG.size.x = windowWidth
            dialogBG.size.y = windowHeight
            dialogBG.enabled = visible

            npcDialog.children.clear()

            var index = 0
            var wordDelay = 0
            var partLength = 0
            var totalPartDelay = 0
            var delay = 0
            for (line in screen.text) {
                val split: MutableList<String> = line.split(' ').toMutableList()
                split.reverse()
                var str = ""
                while (split.isNotEmpty()) {
                    var width = 0
                    val children: ArrayList<TextElement> = arrayListOf()
                    while (width < windowWidth * 0.8) {
                        if (split.isEmpty()) break
                        if (str.isNotEmpty()) str += ' '
                        val word = split.removeLast()
                        children.add(text {
                            content = word
                            offset = V3(
                                clientApi.fontRenderer().getStringWidth(str) + clientApi.fontRenderer()
                                    .getStringWidth(word) / 2.0, index * 10.0
                            )
                            origin = Relative.TOP
                            align = Relative.TOP
                        })
                        str += word
                        width = clientApi.fontRenderer().getStringWidth(str)
                    }
                    str = ""
                    index++
                    for (child in children) {
                        npcDialog.addChild(child)
                        child.offset.x -= width / 2
                        child.scale = V3(0.0001, 0.0001)
                        wordDelay++
                        partLength += child.content.length
                        delay = 60 * wordDelay + totalPartDelay * 40
                        child.animate(delay * 0.001, Easings.NONE) {
                            rotation.z = 0.0
                        }
                        UIEngine.schedule(delay * 0.001) {
                            child.scale = V3(1.0, 1.0)
                        }
                        if (child.content.matches(Regex("/[,\\.?!]/"))) {
                            totalPartDelay += partLength
                            partLength = 0
                        }
                    }
                }
            }
            dialogRunning = true
            npcPart.animate(delay * 0.001, Easings.NONE) {
                rotation.z = 0.0
            }
            UIEngine.schedule(1 * delay * 0.001) {
                if (dialogRunning)
                    resolve()
            }
            npcPart.animate(0.3, Easings.CUBIC_OUT) {
                size.y = index * 10 + 64.0
            }
            val npcPartHeight: Double = index * 10 + 64.0
            npcTitle.content = entrypoint.title
            npcSubtitle.content = ""
            if (entrypoint.subtitle != null)
                npcSubtitle.content = entrypoint.subtitle!!

            buttons.children.clear()
            buttonsBG.children.clear()
            var buttonIndex = 0
            screen.buttons?.forEach {
                val buttonText = text {
                    align = Relative.CENTER
                    content = buttonMessage(it.text, buttonIndex)
                    offset = V3(y = buttonHeight * buttonIndex + 3 + 5.0 * buttonIndex)
                }
                buttons.addChild(buttonText)
                val bg = rectangle {
                    size = V3(y = 15.0)
                    offset = V3(y = buttonHeight * buttonIndex + 3.0 + 5 * buttonIndex)
                    align = Relative.CENTER
                    color = Color(0, 0, 0, 100.0 / 255)

                    onClick {
                        if (Mouse.isButtonDown(0)) {
                            activateButton(it)
                            update()
                        }
                    }
                }
                buttonsBG.addChild(bg)
                bg.animate(0.3, Easings.CUBIC_OUT) {
                    offset.y = buttonIndex * buttonHeight + 5.0 * buttonIndex
                }
                bg.size.x = getMaxStrWidth() + 19.0
                buttonIndex++
            }
            buttonPart.offset.y = -buttonIndex * buttonHeight + 0.25 * (windowHeight - npcPartHeight)
            shiftButtonCursor(0)
        }

        fun pressEnter() {
            if (dialogRunning) {
                resolve()
                return
            }
            activateButton(screen.buttons!![pickedItem])
            update()
        }

        registerChannel("rise:dialog-screen") {
            val action = readUtf8()
            val raw = readUtf8()
            when (action) {
                "load" -> {
                    val json = JsonParser.parseString(raw).asJsonObject
                    val update: ArrayList<Entrypoint> = arrayListOf()
                    if (json["entrypoints"] != null) {
                        for (entrypoint in json["entrypoints"].asJsonArray) {
                            val ep = parseEntrypoint(entrypoint.asJsonObject)
                            update.add(ep!!)
                        }
                    }
                    entrypoints = update
                }

                "open" -> {
                    val id = JsonParser.parseString(raw).toString().replace("\"", "")
                    println("Opening $id entrypoint...")
                    var success = false

                    entrypoints.filter { it.id == id }.forEach {
                        success = true
                        openEntrypoint(it)
                        update()
                    }

                    if (!success) println("Entrypoint $id is not exists!")
                }
                "close" -> {
                    val id = JsonParser.parseString(raw).toString().replace("\"", "")
                    println("Closing $id entrypoint...")

                    var success = false

                    entrypoints.filter { it.id == id }.forEach {
                        success = true
                        close(it)
                        update()
                    }
                    if (!success) println("Entrypoint $id is not exists!")
                }
                else -> println("Unknown action: $action")
            }
        }


        registerHandler<KeyPress> {
            if (visible) {
                var i = 0
                while (i < buttonHotkeys.size) {
                    if (this.key == buttonHotkeys[i]) {
                        activateButton(screen.buttons!![i])
                        update()
                        println("" + screen.buttons!![i] + ' ' + i)
                    }
                    i++
                }
                when (this.key) {
                    Keyboard.KEY_UP -> shiftButtonCursor(-1)
                    Keyboard.KEY_DOWN -> shiftButtonCursor(+1)
                    Keyboard.KEY_RETURN -> pressEnter()
                }
            }
        }
        registerHandler<GameLoop> {
            val wheel = Mouse.getDWheel()
            if (visible && wheel != 0)
                shiftButtonCursor(sign(-wheel.toDouble()).toInt())
        }
        registerHandler<WindowResize> { if (visible) update() }
        registerHandler<HotbarRender> { if (visible) isCancelled = true }
        registerHandler<HungerRender> { if (visible) isCancelled = true }
        registerHandler<ExpBarRender> { if (visible) isCancelled = true }
        registerHandler<ArmorRender> { if (visible) isCancelled = true }
        registerHandler<HealthRender> { if (visible) isCancelled = true }
    }

    private fun parseScreen(json: JsonObject): Screen? {
        val buttons = mutableListOf<Button?>()
        (json["buttons"] ?: return null).asJsonArray
            .mapNotNull { it.asJsonObject }
            .forEach { buttons.add(parseButton(it)) }
        if (buttons.isEmpty()) return null
        return Screen((json["text"] ?: return null).asJsonArray
            .mapNotNull { it.asString.toString() }).buttons(*buttons.toTypedArray())
    }

    private fun parseButton(json: JsonObject): Button? {
        val text = json["text"] ?: return null
        val actionsArray = json["actions"] ?: return null
        val actions = mutableListOf<Action>()
        for (act in actionsArray.asJsonArray) {
            val type = act.asJsonObject["type"].asString ?: return null
            actions.add(
                when (type) {
                    "open_screen" -> Action(Actions.OPEN_SCREEN).screen(parseScreen(act.asJsonObject["screen"].asJsonObject))
                    "command" -> Action(Actions.COMMAND).command(act.asJsonObject["command"].asString)
                    "previous_screen" -> Action("previous_screen")
                    "close" -> Action(Actions.CLOSE)
                    else -> {
                        println("Wrong action type!")
                        Action(Actions.CLOSE)
                    }
                }
            )
        }
        return Button(text.asString).actions(*actions.toTypedArray())
    }

    private fun stringify(json: JsonElement?) = json?.asString

    private fun parseEntrypoint(json: JsonObject): Entrypoint? {
        val id = json["id"] ?: return null
        val title = json["title"] ?: return null
        val subtitle = json["subtitle"]
        val screenJson = json["screen"] ?: return null
        val screen = parseScreen(screenJson.asJsonObject)
        return Entrypoint(stringify(id)!!, stringify(title)!!, screen!!).subtitle(stringify(subtitle))
    }
}