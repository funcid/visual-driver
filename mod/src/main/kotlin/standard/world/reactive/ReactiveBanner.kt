package standard.world.reactive

import me.func.protocol.data.color.RGB
import me.func.protocol.data.color.Tricolor
import me.func.protocol.data.element.MotionType
import ru.cristalix.uiengine.utility.V3
import java.util.*

class ReactiveBanner {

    var uuid: UUID = UUID.randomUUID()
    var content: String = ""
    var location: V3 = V3(0.0, 0.0, 0.0)
    var height: Int = 100
    var weight: Int = 100
    var texture: String = ""
    var color: RGB = Tricolor(0, 0, 0)
    var opacity: Double = 0.62
    var motionType: MotionType = MotionType.CONSTANT
    var watchingOnPlayer: Boolean = false
    var watchingOnPlayerWithoutPitch: Boolean = false
    var motionSettings: MutableMap<String, Any> = hashMapOf(
        "yaw" to 0.0,
        "pitch" to 0.0,
        "xray" to false
    )
    var carveSize: Double = 2.0
}