package me.func.mod.world

import me.func.mod.reactive.ReactiveBanner
import java.util.*

object Banners {

    var banners = mutableMapOf<UUID, ReactiveBanner>()

    @JvmStatic
    fun add(vararg banners: ReactiveBanner) {
        for (banner in banners) {
            this.banners[banner.uuid] = banner
        }
    }

    @JvmStatic
    fun remove(vararg banners: ReactiveBanner) {
        for (banner in banners) {
            this.banners.remove(banner.uuid)
        }
    }

}
