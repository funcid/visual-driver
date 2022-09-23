package standard.ui.scoreboard

import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import ru.cristalix.clientapi.readUtf8
import java.util.*

class ScoreBoardManager {

    init {
        var scoreboard: ScoreBoard? = null

        var lines = 0
        var uuid: UUID = UUID.randomUUID()

        mod.registerChannel("func:scoreboard-scheme") {
            scoreboard?.hide()
            uuid = readId()
            scoreboard = ScoreBoard(uuid, readUtf8(), readUtf8())
            lines = readInt()
            scoreboard?.show()
        }

        mod.registerChannel("func:scoreboard-remove") {
            scoreboard?.hide()
        }

        mod.registerChannel("func:scoreboard-update") {
            if (scoreboard == null) return@registerChannel
            scoreboard?.lineKey = ""
            scoreboard?.lineValue = ""

            if (scoreboard?.uuid != uuid) return@registerChannel

            repeat(lines) {
                scoreboard?.lineKey += readUtf8() + "\n"
                scoreboard?.lineValue += readUtf8() + "\n"
            }

            scoreboard?.update()
        }
    }
}