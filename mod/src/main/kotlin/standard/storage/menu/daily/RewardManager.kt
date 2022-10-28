package standard.storage.menu.daily

import dev.xdark.feder.NetUtil
import ru.cristalix.clientapi.KotlinModHolder.mod
import standard.storage.menu.MenuManager
import java.util.*

class RewardManager {

    init {
        mod.registerChannel("func:weekly-reward") {

            val uuid = UUID.fromString(NetUtil.readUtf8(this))
            val day = readInt()
            val taken = readBoolean()
            val storage = MenuManager.readIcons(this)
            val menu = DailyRewardMenu(uuid, day, taken, storage)

            MenuManager.push(menu)

            menu.sendDayStatus()
            menu.openGui()
            menu.updateScale()
        }
    }
}
