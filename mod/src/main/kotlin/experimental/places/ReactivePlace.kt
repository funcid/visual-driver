package experimental.places

import me.func.protocol.data.color.RGB
import ru.cristalix.uiengine.utility.V3
import java.util.*

class ReactivePlace(
    val uuid: UUID = UUID.randomUUID(),
    var rgb: RGB,
    var location: V3,
    val radius: Double = 1.3,
    val angles: Int = 12
)