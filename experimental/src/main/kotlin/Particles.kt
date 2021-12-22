import dev.xdark.clientapi.event.lifecycle.GameLoop
import ru.cristalix.clientapi.registerHandler

object Particles {

    init {
        registerHandler<GameLoop> {  }
    }

}