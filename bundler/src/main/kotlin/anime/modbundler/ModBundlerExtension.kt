// @formatter:off

package anime.modbundler

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import java.io.Serializable

class ModBundlerExtension : Serializable {
    @[Input] var name: String? = null
    @[Input] var main: String? = null
    @[Input Optional] var author: String? = "Cristalix"
    @[Input Optional] var version: String? = null
    @[Input Optional] var jarFileName: String? = null
}
