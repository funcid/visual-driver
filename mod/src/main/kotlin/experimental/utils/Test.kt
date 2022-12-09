package experimental.utils

import readColoredUtf8
import ru.cristalix.clientapi.JavaMod.clientApi
import ru.cristalix.clientapi.KotlinModHolder.*

class Test {

    companion object {
        init {

            mod.registerChannel("open:url") {

                val url = readColoredUtf8()

                println("Opening url: $url")
                clientApi.minecraft().openUrl(url)
            }

            mod.registerChannel("open:p13n") {

                println("Opening p13n")
                clientApi.p13nProvider().openGui()
            }
        }
    }

}
