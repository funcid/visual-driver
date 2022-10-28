package standard.ui.scoreboard

import readColoredUtf8
import ru.cristalix.clientapi.KotlinModHolder.mod
import ru.cristalix.clientapi.readId
import java.util.*

class ScoreBoardManager {

    init {
        var scoreboard: ScoreBoard? = null

        var lines = 0
        var uuid: UUID = UUID.randomUUID()

        mod.registerChannel("func:scoreboard-scheme") {
            scoreboard?.hide()
            uuid = readId()
            scoreboard = ScoreBoard(uuid, readColoredUtf8(), readColoredUtf8())
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

            val currentUuid = readId()
            if (currentUuid != uuid) return@registerChannel

            repeat(lines) {
                val notLast = lines != it + 1
                scoreboard?.lineKey += readColoredUtf8() + if (notLast) "\n" else ""
                scoreboard?.lineValue += readColoredUtf8() + if (notLast) "\n" else ""
            }

            scoreboard?.update()
        }
    }
}